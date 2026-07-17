/** Métadonnées de pagination renvoyées par Spring Data (PagedModel). */
export interface PageMetadata {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

/** Page générique : contenu + métadonnées. */
export interface Page<T> {
  content: T[];
  page: PageMetadata;
}
