import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { delay, tap, catchError, map } from 'rxjs/operators';

import { environment } from '../../../environments/environment';

const STORAGE_KEY_EMAIL = 'gdpr_user_email';
const STORAGE_KEY_TOKEN = 'gdpr_auth_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl;

  /** Reactive signal: email of the authenticated user, null if not authenticated */
  private readonly _currentUser = signal<string | null>(null);
  readonly currentUser = this._currentUser.asReadonly();

  /** Reactive signal: auth token (for future JWT support) */
  private readonly _token = signal<string | null>(null);
  readonly token = this._token.asReadonly();

  /** Computed: convenience boolean for guards/interceptors/templates */
  readonly isAuthenticated = computed(() => this._currentUser() !== null);

  login(email: string, password?: string): Observable<boolean> {
    // In questa fase, il frontend si aspetta 'username' o 'email' nel payload.
    // L'API Spring richiede 'usernameOrEmail' e 'password'.
    const payload = {
      usernameOrEmail: email,
      password: password || 'Password123!' // Fallback di sicurezza se omesso in questo step
    };

    return this.http.post<any>(`${this.baseUrl}/api/auth/signin`, payload).pipe(
      tap((res) => {
        if (res && res.token) {
          this._currentUser.set(res.email || email);
          this._token.set(res.token);
          localStorage.setItem(STORAGE_KEY_EMAIL, res.email || email);
          localStorage.setItem(STORAGE_KEY_TOKEN, res.token);
        }
      }),
      map((res) => !!res.token),
      catchError((err) => {
        console.error('[AuthService] Login failed:', err);
        return throwError(() => err);
      })
    );
  }

  /** Clears all auth state and storage */
  logout(): void {
    this._currentUser.set(null);
    this._token.set(null);
    localStorage.removeItem(STORAGE_KEY_EMAIL);
    localStorage.removeItem(STORAGE_KEY_TOKEN);
  }

  /**
   * Restores a previous session from localStorage.
   * Called once via APP_INITIALIZER at bootstrap.
   */
  restoreSession(): void {
    const email = localStorage.getItem(STORAGE_KEY_EMAIL);
    const token = localStorage.getItem(STORAGE_KEY_TOKEN);
    if (email) {
      this._currentUser.set(email);
      this._token.set(token);
    }
  }
}
