import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CompteResponse } from '../../core/models/banking.model';
import { AdminService } from '../../core/services/admin.service';
import { AuthService } from '../../core/services/auth.service';
import { CompteService } from '../../core/services/compte.service';
import { ConseillerService } from '../../core/services/conseiller.service';
import { CountUp } from '../../shared/ui/count-up';
import { Icon } from '../../shared/ui/icon';

@Component({
  selector: 'app-home',
  imports: [RouterLink, Icon, DecimalPipe, CountUp],
  templateUrl: './home.html',
})
export class Home {
  private readonly auth = inject(AuthService);
  private readonly compteService = inject(CompteService);
  private readonly conseillerService = inject(ConseillerService);
  private readonly adminService = inject(AdminService);

  protected readonly email = this.auth.email;
  protected readonly estClient = computed(() => this.auth.roles().includes('ROLE_CLIENT'));
  protected readonly estConseiller = computed(() => this.auth.roles().includes('ROLE_CONSEILLER'));
  protected readonly estAdmin = computed(() => this.auth.roles().includes('ROLE_ADMIN'));

  // Client
  protected readonly comptes = signal<CompteResponse[]>([]);
  protected readonly soldeTotal = computed(() =>
    this.comptes().reduce((total, c) => total + c.solde, 0),
  );
  protected readonly devise = computed(() => this.comptes()[0]?.devise ?? 'XOF');
  protected readonly soldeMax = computed(() => Math.max(1, ...this.comptes().map((c) => c.solde)));

  // Conseiller
  protected readonly nbClients = signal(0);
  protected readonly nbEnAttente = signal(0);

  // Admin
  protected readonly nbAudit = signal(0);
  protected readonly nbAuditJour = signal(0);

  constructor() {
    if (this.estClient()) {
      this.compteService.mesComptes().subscribe({ next: (cs) => this.comptes.set(cs) });
    }
    if (this.estConseiller()) {
      this.conseillerService
        .listerClients(0, 1)
        .subscribe({ next: (p) => this.nbClients.set(p.page.totalElements) });
      this.conseillerService
        .operationsEnAttente(0, 1)
        .subscribe({ next: (p) => this.nbEnAttente.set(p.page.totalElements) });
    }
    if (this.estAdmin()) {
      this.adminService.audit({}, 0, 1).subscribe({ next: (p) => this.nbAudit.set(p.page.totalElements) });
      const debutJour = new Date();
      debutJour.setHours(0, 0, 0, 0);
      this.adminService
        .audit({ dateDebut: debutJour.toISOString() }, 0, 1)
        .subscribe({ next: (p) => this.nbAuditJour.set(p.page.totalElements) });
    }
  }

  protected pourcentage(compte: CompteResponse): number {
    return Math.round((compte.solde / this.soldeMax()) * 100);
  }
}
