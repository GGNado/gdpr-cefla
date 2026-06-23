import { Component, inject } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet],
  templateUrl: './layout.html',
  styleUrl: './layout.css'
})
export class Layout {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  protected readonly email = this.authService.currentUser;

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
