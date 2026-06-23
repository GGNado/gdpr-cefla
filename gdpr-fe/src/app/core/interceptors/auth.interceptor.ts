import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Clone the request and add auth headers if user is authenticated
  let authReq = req;
  const email = authService.currentUser();
  const token = authService.token();

  if (email) {
    const headers: Record<string, string> = {
      'X-User-Email': email
    };

    // Add Authorization header when a real token is available
    if (token && token !== 'mock-jwt-token') {
      headers['Authorization'] = `Bearer ${token}`;
    }

    authReq = req.clone({ setHeaders: headers });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
