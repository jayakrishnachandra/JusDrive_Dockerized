import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class AuthService {
  
  private baseUrl = `${environment.gatewayBaseUrl}/auth`;

  
  constructor(private http: HttpClient) {
    console.log(this.baseUrl);
  }

  private isBrowser(): boolean {
    return typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  }

  private getRole(): string | null {
    return this.isBrowser() ? localStorage.getItem('role') : null;
  }

  public get isLoggedIn(): boolean {
    return this.isBrowser() && localStorage.getItem("isLogin") === 'true';
  }

  setLoginStatus(status: boolean) {
    if (this.isBrowser()) {
      localStorage.setItem("isLogin", status ? 'true' : 'false');
    }
  }

  logout() {
    localStorage.clear();
    console.log('Local storage cleared.');
  }

  saveToken(token: string) {
    if (this.isBrowser()) {
      localStorage.setItem('jwtToken', token);
    }
  }

  getToken(): string | null {
    return this.isBrowser() ? localStorage.getItem('jwtToken') : null;
  }

  clearToken() {
    if (this.isBrowser()) {
      localStorage.removeItem('jwtToken');
    }
  }

  login(data: any): Observable<{ success: string; message: string; token: string }> {
    const role = this.getRole();
    if (!role) {
      throw new Error('Role is not defined in localStorage');
    }
    return this.http.post<{ success: string; message: string; token: string }>(
      `${this.baseUrl}/${role}/login`,
      data
    ).pipe(
      tap((response) => {
        console.log('Login response received:', response);
        if (response.success === 'true') {
          this.setLoginStatus(true);
          this.saveToken(response.token);
          this.getUserDetails().subscribe({});

        } else {
          console.error('Login failed:', response.message);
        }
      }),
      catchError((error) => {
        console.error('Login request failed:', error);
        throw error;
      })
    );
  }

  signup(data: any): Observable<{ success: string; message: string; token: string }> {
    const role = this.getRole();
    if (!role) {
      throw new Error('Role is not defined in localStorage');
    }
    return this.http.post<{ success: string; message: string; token: string }>(
      `${this.baseUrl}/${role}/register`,
      data,
    ).pipe(
      tap((response) => {
        console.log('Signup response received:', response);
        if (response.success === 'true') {
          this.setLoginStatus(true);
          this.saveToken(response.token);
        } else {
          console.error('Signup failed:', response.message);
        }
      }),
      catchError((error) => {
        console.error('Signup request failed:', error);
        throw error;
      })
    );
  }

  forgotPassword(data: any): Observable<any> {
    const role = this.getRole();
    if (!role) {
      throw new Error('Role is not defined in localStorage');
    }
    return this.http.post(`${this.baseUrl}/${role}/forgotPassword`, data).pipe(
      tap((response) => {
        console.log('Forgot password response received:', response);
      }),
      catchError((error) => {
        console.error('Forgot password request failed:', error);
        throw error;
      })
    );
  }

  verifyResetCode(data: any): Observable<any> {
    const role = this.getRole();
    if (!role) {
      throw new Error('Role is not defined in localStorage');
    }
    return this.http.post(`${this.baseUrl}/${role}/verifyResetCode`, data).pipe(
      tap((response) => {
        console.log('Verify reset code response received:', response);
      }),
      catchError((error) => {
        console.error('Verify reset code request failed:', error);
        throw error;
      })
    );
  }

  resetPassword(data: any): Observable<any> {
    const role = this.getRole();
    if (!role) {
      throw new Error('Role is not defined in localStorage');
    }
    return this.http.post(`${this.baseUrl}/${role}/resetPassword`, data).pipe(
      tap((response) => {
        console.log('Reset password response received:', response);
      }),
      catchError((error) => {
        console.error('Reset password request failed:', error);
        throw error;
      })
    );
  }


  getUserDetails(): Observable<any> {
    const role = this.getRole();
    if (!role) {
      throw new Error('Role is not defined in localStorage');
    }
    return this.http.get(`${this.baseUrl}/${role}/user`).pipe(
      tap((response: any) => {
        console.log(`User details at auth.service.ts`);
        console.log(`${role} details received:`, response);
        if (response && response[role.toLowerCase()]) {
          localStorage.setItem('userData', JSON.stringify(response[role.toLowerCase()])); // Save user data in localStorage
        } else {
          console.error(`Unexpected response structure:`, response);
          throw new Error(`Failed to process user details for role: ${role}`);
        }
      }),
      catchError((error) => {
        console.error(`Failed to fetch ${role} details:`, error);
        throw error;
      })
    );
  }
}
