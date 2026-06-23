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

  /**
   * Validates an email against the backend and authenticates the user.
   *
   * Currently uses a mock implementation. When the BE endpoint is ready,
   * replace the mock Observable with:
   *   this.http.post<{ valid: boolean; token?: string }>(`${this.baseUrl}/api/auth/validate-email`, { email })
   */
  login(email: string): Observable<boolean> {
    // ── Mock implementation ──────────────────────────────────────────
    // Simulates a BE call: any well-formed email is considered valid.
    const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    const mockResponse$ = of({ valid: isValid, token: isValid ? 'mock-jwt-token' : null }).pipe(
      delay(800)
    );
    // ── End mock ─────────────────────────────────────────────────────

    return mockResponse$.pipe(
      tap((res) => {
        if (res.valid) {
          this._currentUser.set(email);
          this._token.set(res.token ?? null);
          localStorage.setItem(STORAGE_KEY_EMAIL, email);
          if (res.token) {
            localStorage.setItem(STORAGE_KEY_TOKEN, res.token);
          }
        }
      }),
      map((res) => res.valid),
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
