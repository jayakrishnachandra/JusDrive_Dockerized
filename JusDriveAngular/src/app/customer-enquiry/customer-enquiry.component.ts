import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { EnquiryService } from '../services/enquiry.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-customer-enquiry',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './customer-enquiry.component.html',
  styleUrls: ['./customer-enquiry.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class CustomerEnquiryComponent implements OnInit {

  enquiries: any[] = [];
  customerId: any;
  constructor(private enquiryService: EnquiryService) { }
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
    this.enquiryService.getCustomerEnquiries(this.customerId).subscribe({
        next: (data) => {
            console.log('Raw API Response:', data);
            this.enquiries = data.map((item: any) => ({
                image: item.image,
                carModel: item.carModel,
                carBrand: item.carBrand,
                registrationNumber: item.registrationNumber,
                color: item.color,
                carType: item.carType,
                seatingCapacity: item.seatingCapacity,
                pricePerDay: item.pricePerDay,
                message: item.message,
                createdAt: item.createdAt,
                status: item.status,
                response: item.response,
                respondedAt: item.respondedAt
            }));
            console.log('Mapped Enquiries:', this.enquiries);
            this.enquiries.reverse();
            
        },
        error: (error) => {
            console.error('Error fetching enquiries', error);
        }
    });
}
}
