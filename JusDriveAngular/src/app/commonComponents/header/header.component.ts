

import { Component, HostListener, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LogoImageComponent } from "../logo-image/logo-image.component";
import { AvatarImageComponent } from "../avatar-image/avatar-image.component";
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, LogoImageComponent, AvatarImageComponent],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})



export class HeaderComponent implements OnInit {
  isMobile: boolean = false;

  userData : any;
  firstName : string | undefined;

  @HostListener('window:resize')
  onResize() {
    this.checkScreenSize();
  }

  ngOnInit() {
    this.checkScreenSize();
    this.authService.getUserDetails().subscribe({
        next : () =>
        {
          this.userData = JSON.parse(localStorage.getItem('userData') ?? '{}');
          this.firstName  = this.userData.name;
          this.firstName = this.firstName?.split(" ").at(0);
          console.log(this.firstName);
        }
      }
    );  
  }

  checkScreenSize() {

    if (typeof window !== 'undefined') {
      this.isMobile = window.innerWidth <= 768;
    }
  }

  constructor(private authService: AuthService) {
  }


  logout() {
    this.authService.logout();
    console.log('User logged out');
  
  }
}



