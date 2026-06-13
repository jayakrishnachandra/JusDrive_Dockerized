import { Component, OnInit, ViewEncapsulation, ChangeDetectorRef }
from '@angular/core';
import { BookingService } from '../services/booking.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CarPageResponse } from '../models/CarPageResponse';
 
@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class BookingComponent implements OnInit {
 
  enquiryFormVisible: boolean = false;
  bookingFormVisible: boolean = false;
  showBookingForm: boolean = false;
  enquiryMessage :string ='';
  bookingDate: string = '';
  returnDate: string = '';
  totalAmount: number = 0;

  
 
  selectedCar: any = null;
 
  showEnquiryForm: boolean = false;
  gmailId: string ='';
 
  customerId: any;
  suggestions: string[] = [
    'Toyota', 'Honda', 'Hyundai', 'Tata', 'Tesla', 'Ford',
    'Swift', 'Nexon', 'Model S', 'City', 'Creta', 'Scorpio','tigor','Thar',
    'Red', 'Blue', 'Black', 'White', 'Silver', 'Grey',
    'SUV', 'Sedan', 'Hatchback',
    '2 Seater', '4 Seater', '5 Seater', '7 Seater'
  ];
 
  searchKeyword: any;
 
  bookingDateError: string = '';
  returnDateError: string = '';

  
  availableCars: any[] = [];
  currentPage: number = 0;
  totalPages: number = 0;
  pageSize: number = 6;
 
  constructor(private bookingService: BookingService) {}
 
  ngOnInit(): void {
    const userData = JSON.parse(localStorage.getItem('userData') || '{}');
    if (userData) {
      console.log('User ID is:', userData.id);
      this.customerId = userData.id;
    } else {
      console.error('No user data found in localStorage');
    }
    this.fetchAvailableCars();
  }
 
  openEnquiryForm(car: any): void {
    this.selectedCar = car;
    this.enquiryMessage = ''; // Reset
    this.enquiryFormVisible = true; //show form
    console.log('selected car for enquiry:', this.selectedCar);
  }
 
  closeEnquiryForm(): void {
    this.selectedCar = null;
    this.enquiryMessage = '';
    this.enquiryFormVisible = false; // Hide
  }
 

  fetchAvailableCars(): void {
    this.bookingService.getAvailableCars(this.currentPage, this.pageSize).subscribe(
      (response : CarPageResponse) => {
        this.availableCars = response.cars;
        this.currentPage = response.currentPage;
        this.totalPages = response.totalPages;
      },
      (error) => {
        if (error.status === 204) {
          this.availableCars = [];
          this.totalPages = 0;
          console.warn('No available cars found.');
        } else {
          console.error('Error fetching available cars:', error);
        }
      }
    );
  }
  

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.fetchAvailableCars();
    }
  }

 
  onBook(): void {
    console.log('gmailId:', this.gmailId);
    console.log('bookingDate:', this.bookingDate);
    console.log('returnDate:', this.returnDate);
    console.log('totalAmount:', this.totalAmount);
 
    if (!this.gmailId || !this.bookingDate || !this.returnDate || !this.totalAmount) {
      alert('Please fill in all booking details.');
      return;
    }
 
    const bookingDetails = {
      customerId: this.customerId,
      carId: this.selectedCar.carId,
      bookingDate: this.bookingDate,
      returnDate: this.returnDate,
      totalAmount: this.totalAmount,
      status: "BOOKED",
      customerEmail: this.gmailId
    };
 
    this.bookingService.bookCar(bookingDetails).subscribe({
      next: (response) => {
        console.log('Booking created successfully:', response);
        alert('Booking created successfully! Confirmation sent to email.');
 
        const ownerId = this.selectedCar.ownerId;
 
        this.closeBookingForm(); // Close form
      },
      error: (error) => {
        console.error('Error creating booking:', error);
        alert('Failed to create booking. Please try again.');
      }
    });
  }
 
  onEnquiry(): void {
    if (!this.selectedCar || !this.enquiryMessage) {
      alert('Please enter a valid enquiry message.');
      return;
    }
 
    const carId = this.selectedCar.carId;
    this.bookingService.enquireCar(this.customerId, carId, this.enquiryMessage).subscribe({
      next: (response) => {
        console.log(`Enquiry for car with ID ${carId} submitted successfully`, response);
        alert(`Enquiry submitted successfully!`);
        this.closeEnquiryForm(); // Close form
      },
      error: (error) => {
        console.error(`Error submitting enquiry for car with ID ${carId}:`, error);
        alert(`Failed to submit enquiry for car with ID ${carId}.`);
      }
    });
  }
 
  openBookingForm(car: any): void {
    this.selectedCar = car;
    this.bookingFormVisible = true; // Show
  }
 
  closeBookingForm(): void {
    this.bookingFormVisible = false; // Hide
    this.selectedCar = null; // Reset
    this.bookingDate = '';
    this.returnDate = '';
    this.totalAmount = 0;
    this.gmailId = '';
    this.fetchAvailableCars();
  }
 
  calculateTotalAmount(): void {
    this.bookingDateError = '';
    this.returnDateError = '';
    this.totalAmount = 0;
 
    if (this.bookingDate && this.returnDate && this.selectedCar?.pricePerDay) {
      const now = new Date();
      now.setHours(0, 0, 0, 0);
 
      const bookingDate = new Date(this.bookingDate);
      const returnDate = new Date(this.returnDate);
 
      const oneMonthFromNow = new Date();
      oneMonthFromNow.setMonth(now.getMonth() + 1);
 
      const sixMonthsFromNow = new Date();
      sixMonthsFromNow.setMonth(now.getMonth() + 6);
 
      if (bookingDate < now) {
        this.bookingDateError = 'Booking date cannot be in the past.';
        return;
      }
 
      if (bookingDate > oneMonthFromNow) {
        this.bookingDateError = 'Booking date must be within 1 month from today.';
        return;
      }
 
      if (returnDate < now) {
        this.returnDateError = 'Return date cannot be in the past.';
        return;
      }
 
      if (returnDate > sixMonthsFromNow) {
        this.returnDateError = 'Return date cannot be more than 6 months from today.';
        return;
      }
 
      if (returnDate <= bookingDate) {
        this.returnDateError = 'Return date must be after the booking date.';
        return;
      }
 
      const days = Math.ceil((returnDate.getTime() - bookingDate.getTime()) / (1000 * 60 * 60 * 24));
      this.totalAmount = days * this.selectedCar.pricePerDay;
    } else {
      this.totalAmount = 0;
    }
  }
 
  onSearch() {
    this.bookingService.searchCars(this.searchKeyword).subscribe(
      (data) => {
        this.availableCars = data;
        console.log('Available cars:', this.availableCars);
      },
      (error) => {
        console.error('Error fetching available cars:', error);
      }
    );
  }
}
 
 