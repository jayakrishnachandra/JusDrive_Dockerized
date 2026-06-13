import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-user-signup',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  templateUrl: './user-signup.component.html',
  styleUrls: ['./user-signup.component.css']
})
export class UserSignupComponent {
  constructor(private router: Router, private authService: AuthService) {
    localStorage.setItem('role', 'customer');
    console.log('Role set to user in local storage');
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
  }
  onSignup(form: any) {
    const signupData = {
      name: form.value.name,
      email: form.value.email,
      password: form.value.password,
      phone: form.value.phone,
      address: form.value.address,
    };
    console.log('Signup triggered:', signupData);

    this.authService.signup(signupData).subscribe({
      next: (res: any) => {
        console.log('Signup successful:', res);
        alert(res.message || 'Signup successful!');
        this.authService.saveToken(res.token);
        this.router.navigate(['/booking']);
      },
      error: (err) => {
        console.error('Signup failed:', err);
        const msg = err.error?.message || 'Something went wrong';
        alert('Signup failed: ' + msg);
      }
    });
  }
}
