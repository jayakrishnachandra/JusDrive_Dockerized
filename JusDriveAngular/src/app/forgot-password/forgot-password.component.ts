import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
 
@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  email = '';
  otp = '';
  password = '';
  confirmPassword = '';
  stage: 'email' | 'otp' | 'reset' = 'email';
  token = '';
  userType = 'owner';
 
  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.route.queryParams.subscribe(params => {
      this.userType = params['userType'] || 'owner';
    });
  }
 
  sendResetLink() {
    if (!this.email) {
      alert('Please enter a valid email');
      return;
    }
 
    this.authService.forgotPassword({ email: this.email }).subscribe({
      next: (res: any) => {
        alert(res.message || 'OTP sent');
        this.stage = 'otp';
      },
      error: (err) => {
        alert(err?.error?.message || 'Failed to send OTP.');
      }
    });
  }
 
  verifyOtp() {
    if (!this.otp) {
      alert('Please enter the OTP');
      return;
    }
 
    const otpData = { email: this.email, otp: this.otp };
 
    this.authService.verifyResetCode(otpData).subscribe({
      next: (res: any) => {
        this.token = res.token;
        this.authService.saveToken(res.token);
        alert('OTP verification successful.');
        this.stage = 'reset';
      },
      error: (err) => {
        alert(err?.error?.message || 'OTP verification failed.');
      }
    });
  }
 
  resetPassword() {
    if (!this.password || this.password.length < 6) {
      alert('Password must be at least 6 characters');
      return;
    }
 
    if (this.password !== this.confirmPassword) {
      alert('Passwords do not match');
      return;
    }
 
    const resetData = { email: this.email, password: this.password };
 
    this.authService.resetPassword(resetData).subscribe({
      next: (res: any) => {
        alert('Password reset successful.');
        this.authService.saveToken(res.token);
        this.router.navigate([this.userType === 'owner' ? '/owner-login' : '/user-login']);
      },
      error: (err) => {
        alert(err?.error?.message || 'Password reset failed.');
      }
    });
  }
}