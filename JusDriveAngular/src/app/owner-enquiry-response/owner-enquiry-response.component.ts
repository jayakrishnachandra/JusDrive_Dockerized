import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { EnquiryService } from '../services/enquiry.service';

@Component({
  selector: 'app-owner-enquiry-response',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './owner-enquiry-response.component.html',
  styleUrl: './owner-enquiry-response.component.css'
})
export class OwnerEnquiryResponseComponent implements OnInit {
  enquiries: any[] = [];
  customerId: any;

  constructor(private enqyiryService : EnquiryService) {}

  ngOnInit(): void {
    const userData = JSON.parse(localStorage.getItem('userData') || '{}');
    if (userData) {
      console.log('User ID is:', userData.id);
      this.customerId = userData.id; 
      this.loadEnquiries();
    } else {
      console.error('No user data found in localStorage');
    }
  }
  
  loadEnquiries(): void {
    this.enqyiryService.loadEnquiries(this.customerId).subscribe(data => {
        data.reverse();
        this.enquiries = data.sort((a, b) => {
          if (a.status === 'pending' && b.status !== 'pending') return -1;
          if (a.status !== 'pending' && b.status === 'pending') return 1;
          return 0;
        });
      });
  }

  sendReply(enquiry: any) {
    const body = { response: enquiry.replyText };
    this.enqyiryService.sendReply(enquiry.enquiryId, body).subscribe((updated: any) => {
        enquiry.status = 'replied';
        enquiry.response = enquiry.replyText;
        enquiry.respondedAt = new Date();
        enquiry.replyText = '';
      });
  }
}
