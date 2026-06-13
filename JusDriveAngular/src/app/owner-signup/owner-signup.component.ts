import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { from } from 'rxjs';

@Component({ selector: 'app-owner-signup',
  standalone:true,
  templateUrl: './owner-signup.component.html',
  imports: [FormsModule, HttpClientModule, CommonModule],
  styleUrls: ['./owner-signup.component.css'] })
  export class OwnerSignupComponent implements OnInit { ownerType: string = 'individual';
  indName: string = '';
  indEmail: string = '';
  indPhone: string = '';
  indPassword: string = '';
  indConfirmPassword: string = '';
  indAddress: string='';

constructor(private authService: AuthService, private router: Router) {
  localStorage.setItem('role', 'owner');
  console.log('Role set to owner in local storage');
}

ngOnInit() {}

onIndividualSignup(form: NgForm) { if (form.invalid || this.indPassword !== this.indConfirmPassword) {
  alert('Invalid form or passwords do not match.'); return; }

const data = {
  name: this.indName,
  email: this.indEmail,
  phone: this.indPhone,
  password: this.indPassword,
  address: this.indAddress,
};
this.authService.signup(data).subscribe({
  next: (res: any) => {
    console.log('Signup successful:', res);
    alert(res.message || 'Signup successful!');
    this.authService.saveToken(res.token);
    this.router.navigate(['/dashboard']);
  },
  error: (err) => {
    console.error(' Signup failed:', err);
    alert(err.error?.message || 'Signup failed: Something went wrong');
  }
});
}
navigateTo(path:string){
  this.router.navigate([path]);
}
}

