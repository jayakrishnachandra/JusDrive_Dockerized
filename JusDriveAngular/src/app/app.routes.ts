import { Routes } from '@angular/router';
import { OwnerLoginComponent } from './owner-login/owner-login.component';
import { OwnerSignupComponent } from './owner-signup/owner-signup.component';
import { UserLoginComponent } from './user-login/user-login.component';
import { UserSignupComponent } from './user-signup/user-signup.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CustomerEnquiryComponent } from './customer-enquiry/customer-enquiry.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { VehicleManagementComponent } from './vehicle-management/vehicle-management.component';
import { MainLayoutComponent } from './main-layout/main-layout.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { BookingComponent } from './booking/booking.component';
import { BookingHistoryComponent } from './booking-history/booking-history.component';
import { OwnerEnquiryResponseComponent } from './owner-enquiry-response/owner-enquiry-response.component';
import { HomeComponent } from './home/home.component';
import { BookingStatusComponent } from './booking-status/booking-status.component';

export const routes: Routes = [
  { path: 'user-login', component: UserLoginComponent },
  { path: 'user-signup', component: UserSignupComponent },
  { path: 'owner-login', component: OwnerLoginComponent },
  { path: 'owner-signup', component: OwnerSignupComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  {path: 'home' , component : HomeComponent},
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  
  
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'vehicle-management', component: VehicleManagementComponent },
      { path: 'booking-history', component: BookingHistoryComponent },
      { path: 'customer-enquiry', component: CustomerEnquiryComponent },
      {path: 'booking', component: BookingComponent},
      {path: 'owner-enquiry-response', component: OwnerEnquiryResponseComponent},
      {path : 'owner-booking-status', component : BookingStatusComponent},
    ]
  },
  { path: '**', component: PageNotFoundComponent}

];

