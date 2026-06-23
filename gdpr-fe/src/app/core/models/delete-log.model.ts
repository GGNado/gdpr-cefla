export interface DeleteLog {
  id: number;
  logFileName: string;
  tableName: string;
  recordId: string;
  deletedAt: string;
  rowsAffected: number;
  errorMessage: string | null;
}
