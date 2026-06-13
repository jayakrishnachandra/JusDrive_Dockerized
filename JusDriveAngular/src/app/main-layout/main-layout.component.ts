import { Component } from '@angular/core';
import { SidebarComponent } from "../commonComponents/sidebar/sidebar.component";
import { HeaderComponent } from "../commonComponents/header/header.component";
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FooterComponent } from "../commonComponents/footer/footer.component";
import { AuthService } from '../services/auth.service';
import { HomeComponent } from '../home/home.component';


@Component({
  selector: 'app-main-layout',
  standalone: true,
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css'],
  imports: [SidebarComponent, HeaderComponent, CommonModule,
    RouterModule, FooterComponent, HomeComponent]
})
export class MainLayoutComponent {

  constructor(private router: Router, private authService: AuthService) {}

  isLogin = this.authService.isLoggedIn;


  redirectToLogin(){
    alert('Please login first!');
    this.router.navigate(['/login']);
  }
}

