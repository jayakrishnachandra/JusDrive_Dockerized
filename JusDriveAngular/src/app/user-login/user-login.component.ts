import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { HttpClientModule } from '@angular/common/http';
import { log } from 'node:console';

@Component({
  selector: 'app-user-login',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})
  export class UserLoginComponent {
    email: string = '';
    password: string = '';


  constructor(private router: Router, private authService: AuthService) {
    localStorage.setItem('role', 'customer');
    console.log('Role set to user in local storage');
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
  }
navigateToForgotPassword(userType: string) {
  this.router.navigate(['/forgot-password'],{queryParams:{userType:'user'}});
}

  onLogin(form: any) {
    if (form.invalid) return;

    console.log('Login triggered:', { email: this.email, password: this.password });

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (res: any) => {
        console.log('Login successful:', res);
        alert(res.message || 'Login successful!');
        this.authService.saveToken(res.token);
        console.log(this.authService.isLoggedIn);
        console.log(this.authService.getToken() + '\nthis is from local storage');
        this.router.navigate(['/booking']);
      },
      error: (err) => {
        console.error('Login failed:', err);
        const msg = err.error?.message || 'Invalid credentials';
        alert('Login failed: ' + msg);
      }
    });
  }
}

