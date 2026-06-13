import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
@Component({
  selector: 'app-owner-login',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  templateUrl: './owner-login.component.html',
  styleUrls: ['./owner-login.component.css']
})
export class OwnerLoginComponent {
  email: string = '';
  password: string = '';

  constructor(private router: Router, private authService: AuthService) {
    localStorage.setItem('role', 'owner');
    console.log('Role set to owner in local storage');
  }

  onLogin(form: any) {
    if (form.invalid) return;
    console.log('Login triggered:', { email: this.email, password: this.password });
    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (res: any) => {
        console.log('Login successful:', res);
        alert(res.message);
        this.authService.saveToken(res.token);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Login failed:', err);
        const errorMsg = err.error?.message || 'Invalid credentials';
        alert(errorMsg);
      }
    });
  }
  navigateToForgotPassword(userType: string) {
    this.router.navigate(['/forgot-password'],{queryParams:{userType:'owner'}});
  }
  navigateTo(path: string) {
    this.router.navigate([path]);
}
}
