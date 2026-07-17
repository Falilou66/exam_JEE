package sn.samabank.samabank_backend.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;
import sn.samabank.samabank_backend.entity.Client;
import sn.samabank.samabank_backend.entity.Compte;
import sn.samabank.samabank_backend.entity.Transaction;
import sn.samabank.samabank_backend.entity.Virement;
import sn.samabank.samabank_backend.exception.CompteIntrouvableException;
import sn.samabank.samabank_backend.repository.CompteRepository;
import sn.samabank.samabank_backend.repository.TransactionRepository;

/**
 * Génère un relevé de compte au format PDF (SEQ-05) via OpenPDF. Liste les
 * transactions du mois demandé, avec un montant signé selon le sens (débit /
 * crédit) du point de vue du compte.
 */
@Service
@RequiredArgsConstructor
public class ReleveService {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Color GRIS = new Color(230, 230, 230);

    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public byte[] genererReleve(Long compteId, YearMonth mois, String emailDemandeur, boolean accesTotal) {
        Compte compte = compteRepository.findById(compteId)
                .orElseThrow(() -> new CompteIntrouvableException(compteId));

        if (!accesTotal && !compte.getClient().getEmail().equals(emailDemandeur)) {
            throw new AccessDeniedException("Ce compte ne vous appartient pas");
        }

        LocalDateTime debut = mois.atDay(1).atStartOfDay();
        LocalDateTime fin = mois.plusMonths(1).atDay(1).atStartOfDay();
        List<Transaction> transactions = transactionRepository.findReleve(compteId, debut, fin);

        return construirePdf(compte, mois, transactions);
    }

    private byte[] construirePdf(Compte compte, YearMonth mois, List<Transaction> transactions) {
        try (ByteArrayOutputStream sortie = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, sortie);
            document.open();

            ajouterEntete(document, compte, mois);
            ajouterTableau(document, compte, transactions);

            document.close();
            return sortie.toByteArray();
        } catch (DocumentException | java.io.IOException ex) {
            throw new IllegalStateException("Erreur lors de la génération du relevé PDF", ex);
        }
    }

    private void ajouterEntete(Document document, Compte compte, YearMonth mois) throws DocumentException {
        Font titre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(30, 60, 120));
        Paragraph paragrapheTitre = new Paragraph("SamaBank — Relevé de compte", titre);
        paragrapheTitre.setSpacingAfter(10);
        document.add(paragrapheTitre);

        Client client = compte.getClient();
        Font info = FontFactory.getFont(FontFactory.HELVETICA, 11);
        document.add(new Paragraph("Titulaire : " + client.getPrenom() + " " + client.getNom(), info));
        document.add(new Paragraph("Compte : " + compte.getNumeroCompte() + " (" + compte.getType() + ")", info));
        document.add(new Paragraph("Période : " + mois, info));
        document.add(new Paragraph(
                "Solde actuel : " + compte.getSolde() + " " + compte.getDevise(), info));

        Paragraph espace = new Paragraph(" ");
        espace.setSpacingAfter(8);
        document.add(espace);
    }

    private void ajouterTableau(Document document, Compte compte, List<Transaction> transactions)
            throws DocumentException {
        PdfPTable table = new PdfPTable(new float[] {2.2f, 3.5f, 1.8f, 2f});
        table.setWidthPercentage(100);

        for (String titre : new String[] {"Date", "Libellé", "Type", "Montant"}) {
            table.addCell(celluleEntete(titre));
        }

        if (transactions.isEmpty()) {
            PdfPCell vide = new PdfPCell(new Paragraph("Aucune opération sur la période"));
            vide.setColspan(4);
            vide.setHorizontalAlignment(Element.ALIGN_CENTER);
            vide.setPadding(8);
            table.addCell(vide);
        } else {
            for (Transaction transaction : transactions) {
                BigDecimal montantSigne = montantSigne(transaction, compte.getId());
                table.addCell(cellule(transaction.getDateOperation().format(FORMAT_DATE)));
                table.addCell(cellule(transaction.getLibelle() != null ? transaction.getLibelle() : ""));
                table.addCell(cellule(transaction.getType().name()));
                table.addCell(celluleMontant(montantSigne + " " + compte.getDevise(),
                        montantSigne.signum() >= 0));
            }
        }
        document.add(table);
    }

    /** Montant signé du point de vue du compte : crédit positif, débit négatif. */
    private BigDecimal montantSigne(Transaction transaction, Long compteId) {
        return switch (transaction.getType()) {
            case DEPOT -> transaction.getMontant();
            case RETRAIT -> transaction.getMontant().negate();
            case VIREMENT -> {
                Virement virement = (Virement) transaction;
                boolean estDestination = virement.getCompteDestination().getId().equals(compteId);
                yield estDestination ? virement.getMontant() : virement.getMontant().negate();
            }
        };
    }

    private PdfPCell celluleEntete(String texte) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        PdfPCell cellule = new PdfPCell(new Paragraph(texte, font));
        cellule.setBackgroundColor(GRIS);
        cellule.setPadding(6);
        return cellule;
    }

    private PdfPCell cellule(String texte) {
        PdfPCell cellule = new PdfPCell(new Paragraph(texte,
                FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cellule.setPadding(5);
        return cellule;
    }

    private PdfPCell celluleMontant(String texte, boolean credit) {
        Color couleur = credit ? new Color(0, 128, 0) : new Color(180, 0, 0);
        PdfPCell cellule = new PdfPCell(new Paragraph(texte,
                FontFactory.getFont(FontFactory.HELVETICA, 10, couleur)));
        cellule.setPadding(5);
        cellule.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cellule;
    }
}
