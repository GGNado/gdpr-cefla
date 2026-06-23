import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe, LowerCasePipe } from '@angular/common';
import { Subject, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

import { ApiService } from '../../core/services/api.service';
import { DeleteLog } from '../../core/models/delete-log.model';
import { UserSearch } from '../../core/models/user-search.model';

@Component({
  selector: 'app-dashboard',
  imports: [FormsModule, DatePipe, LowerCasePipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  private readonly api = inject(ApiService);
  private searchSubject = new Subject<string>();

  // Delete user state
  protected userId = signal('');
  protected searchQuery = signal('');
  protected searchResults = signal<UserSearch[]>([]);
  protected showDropdown = signal(false);
  protected deleteLoading = signal(false);
  protected deleteMessage = signal('');
  protected deleteError = signal(false);

  // Batch state
  protected batchLoading = signal(false);
  protected batchMessage = signal('');
  protected batchError = signal(false);

  // Logs state
  protected logs = signal<DeleteLog[]>([]);
  protected logsLoading = signal(false);

  ngOnInit(): void {
    this.loadLogs();
    
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => query.length >= 2 ? this.api.searchUsers(query) : of([]))
    ).subscribe({
      next: (results) => {
        this.searchResults.set(results);
        this.showDropdown.set(results.length > 0);
      },
      error: () => {
        this.searchResults.set([]);
        this.showDropdown.set(false);
      }
    });
  }

  onSearchChange(query: string): void {
    this.searchQuery.set(query);
    this.userId.set(''); // Clear UUID because they are typing a name
    if (query.trim().length < 2) {
      this.searchResults.set([]);
      this.showDropdown.set(false);
    } else {
      this.searchSubject.next(query.trim());
    }
  }

  selectUser(user: UserSearch): void {
    this.userId.set(user.id);
    this.searchQuery.set(`${user.nome} ${user.cognome} (${user.email})`);
    this.showDropdown.set(false);
  }

  protected showConfirmModal = signal(false);
  protected dryRunLoading = signal(false);
  protected dryRunResults = signal<any[]>([]);

  deleteUser(): void {
    const id = this.userId().trim();
    if (!id) return;

    // Invece di cancellare subito, apriamo la modale e facciamo il dry-run
    this.showConfirmModal.set(true);
    this.dryRunLoading.set(true);
    this.dryRunResults.set([]);

    this.api.dryRun(id).subscribe({
      next: (data) => {
        this.dryRunResults.set(data);
        this.dryRunLoading.set(false);
      },
      error: () => {
        this.dryRunLoading.set(false);
      }
    });
  }

  cancelDeletion(): void {
    this.showConfirmModal.set(false);
  }

  confirmDeletion(): void {
    const id = this.userId().trim();
    if (!id) return;

    this.showConfirmModal.set(false);
    this.deleteLoading.set(true);
    this.deleteMessage.set('');
    this.deleteError.set(false);

    this.api.deleteUser(id).subscribe({
      next: (msg) => {
        this.deleteMessage.set(msg || 'Utente cancellato con successo.');
        this.deleteError.set(false);
        this.deleteLoading.set(false);
        this.userId.set('');
        this.searchQuery.set('');
        this.loadLogs();
      },
      error: (err) => {
        this.deleteMessage.set(err.error || 'Errore durante la cancellazione.');
        this.deleteError.set(true);
        this.deleteLoading.set(false);
      }
    });
  }

  // Modal Batch state
  protected showBatchModal = signal(false);
  protected dryRunBatchLoading = signal(false);
  protected batchPreviewResults = signal<any[]>([]);

  runBatch(): void {
    this.showBatchModal.set(true);
    this.dryRunBatchLoading.set(true);
    this.batchPreviewResults.set([]);

    this.api.dryRunBatch().subscribe({
      next: (data) => {
        this.batchPreviewResults.set(data);
        this.dryRunBatchLoading.set(false);
      },
      error: () => {
        this.dryRunBatchLoading.set(false);
      }
    });
  }

  cancelBatch(): void {
    this.showBatchModal.set(false);
  }

  confirmBatch(): void {
    this.showBatchModal.set(false);
    this.batchLoading.set(true);
    this.batchMessage.set('');
    this.batchError.set(false);

    this.api.runBatchDeletion().subscribe({
      next: (msg) => {
        this.batchMessage.set(msg || 'Batch completato con successo.');
        this.batchError.set(false);
        this.batchLoading.set(false);
        this.loadLogs();
      },
      error: (err) => {
        this.batchMessage.set(err.error || 'Errore durante il batch.');
        this.batchError.set(true);
        this.batchLoading.set(false);
      }
    });
  }

  loadLogs(): void {
    this.logsLoading.set(true);
    this.api.getLogs().subscribe({
      next: (data) => {
        this.logs.set(data);
        this.logsLoading.set(false);
      },
      error: () => {
        this.logsLoading.set(false);
      }
    });
  }
}
