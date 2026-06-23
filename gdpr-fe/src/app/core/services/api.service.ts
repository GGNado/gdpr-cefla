import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { DeleteLog } from '../models/delete-log.model';
import { environment } from '../../../environments/environment';

export interface TableCount {
  tableName: string;
  recordCount: number;
}

export interface BatchPreview {
  idUtente: string;
  nome: string;
  cognome: string;
  email: string;
  ultimaScadenzaLicenza: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl;

  deleteUser(idUtente: string): Observable<string> {
    return this.http.delete(`${this.baseUrl}/api/userDeletes/delete/${idUtente}`, {
      responseType: 'text'
    });
  }

  searchUsers(query: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/userDeletes/search?q=${query}`);
  }

  dryRun(idUtente: string): Observable<TableCount[]> {
    return this.http.get<TableCount[]>(`${this.baseUrl}/api/userDeletes/dry-run/${idUtente}`);
  }

  dryRunBatch(): Observable<BatchPreview[]> {
    return this.http.get<BatchPreview[]>(`${this.baseUrl}/api/userDeletes/dry-run-batch`);
  }

  runBatchDeletion(): Observable<string> {
    return this.http.delete(`${this.baseUrl}/api/userDeletes/delete-batch`, {
      responseType: 'text'
    });
  }

  getLogs(): Observable<DeleteLog[]> {
    return this.http.get<DeleteLog[]>(`${this.baseUrl}/api/deleteLogs/logs`);
  }
}
