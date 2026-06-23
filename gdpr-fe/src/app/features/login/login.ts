import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  protected email = signal('');
  protected emailFocused = signal(false);
  protected loading = signal(false);
  protected errorMessage = signal('');

  login(): void {
    const value = this.email().trim();
    if (!value || this.loading()) return;

    this.loading.set(true);
    this.errorMessage.set('');

    this.authService.login(value).subscribe({
      next: (valid) => {
        this.loading.set(false);
        if (valid) {
          this.router.navigate(['/dashboard']);
        } else {
          this.errorMessage.set('Email non riconosciuta. Verifica l\'indirizzo e riprova.');
        }
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Errore di connessione al server. Riprova più tardi.');
      }
    });
  }
}
