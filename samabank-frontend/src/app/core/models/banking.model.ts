export type TypeCompte = 'COURANT' | 'EPARGNE';
export type StatutCompte = 'EN_CREATION' | 'ACTIF' | 'BLOQUE' | 'CLOTURE';
export type StatutTransaction = 'EN_ATTENTE_VALIDATION' | 'VALIDEE' | 'REJETEE';
export type TypeTransaction = 'VIREMENT' | 'DEPOT' | 'RETRAIT';
export type Canal = 'WEB' | 'MOBILE' | 'AGENCE';
export type DecisionValidation = 'APPROUVER' | 'REJETER';
export type TypeUtilisateur = 'CONSEILLER' | 'ADMIN';

export interface CompteResponse {
  id: number;
  numeroCompte: string;
  type: TypeCompte;
  solde: number;
  soldeDisponible: number;
  devise: string;
  statut: StatutCompte;
  dateOuverture: string;
  clientId: number;
}

export interface TransactionResponse {
  id: number;
  reference: string;
  type: TypeTransaction;
  montant: number;
  statut: StatutTransaction;
  libelle: string | null;
  dateOperation: string;
  compte: string;
  compteDestination: string | null;
  motif: string | null;
  canal: Canal | null;
}

export interface VirementRequest {
  compteSource: string;
  compteDestination: string;
  montant: number;
  motif?: string;
}

export interface ClientResponse {
  id: number;
  numeroClient: string;
  nom: string;
  prenom: string;
  cni: string;
  email: string;
  telephone: string | null;
  adresse: string | null;
  actif: boolean;
  dateCreation: string;
}

export interface CreationClientRequest {
  nom: string;
  prenom: string;
  cni: string;
  email: string;
  motDePasse: string;
  telephone?: string;
  adresse?: string;
}

export interface OuvertureCompteRequest {
  type: TypeCompte;
  devise?: string;
  decouvertAutorise?: number;
  tauxInteret?: number;
}

export interface OperationRequest {
  montant: number;
  canal?: Canal;
  libelle?: string;
}

export interface ChangementStatutRequest {
  statut: StatutCompte;
}

export interface ValidationRequest {
  decision: DecisionValidation;
}

export interface ProfilUpdateRequest {
  telephone?: string;
  adresse?: string;
  nouveauMotDePasse?: string;
}

export interface CreationUtilisateurRequest {
  email: string;
  motDePasse: string;
  type: TypeUtilisateur;
  matricule?: string;
  agence?: string;
  niveauAcces?: number;
}

export interface UtilisateurResponse {
  id: number;
  email: string;
  type: string;
  roles: string[];
  actif: boolean;
  dateCreation: string;
}

export interface AuditLogResponse {
  id: number;
  acteur: string;
  action: string;
  cibleType: string | null;
  cibleId: string | null;
  valeurAvant: string | null;
  valeurApres: string | null;
  horodatage: string;
  adresseIp: string | null;
}
