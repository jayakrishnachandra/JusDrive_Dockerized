
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingService } from '../services/booking.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-booking-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './booking-history.component.html',
  styleUrls: ['./booking-history.component.css']
})
export class BookingHistoryComponent implements OnInit {
  bookings : any[] = [];

  customerId: number | null = null;

  constructor(
    private bookingService: BookingService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userData = JSON.parse(localStorage.getItem('userData') || '{}');
    if (userData && userData.id) {
      this.customerId = userData.id;
      this.fetchBookingsByCustomer();
    } else {
      console.error('No user data found in localStorage');
    }
  }

  fetchBookingsByCustomer(): void {
    if (!this.customerId) return;

    this.bookingService.fetchBookingsByCustomer().subscribe(
      (data: any[]) => {
        console.log('Raw booking data:', data);
        this.bookings = data.map((booking) => ({
          bookingId: booking.bookingId,
          bookingDate: new Date(booking.bookingDate),
          returnDate: new Date(booking.returnDate),
          totalAmount: booking.totalAmount,
          status: booking.status,
          carBrand: booking.carBrand,
          carModel: booking.carModel,
          registrationNumber: booking.registrationNumber,
          color: booking.color,
          carType: booking.carType,
          seatingCapacity: booking.seatingCapacity,
          pricePerDay: booking.pricePerDay,
          image: booking.image
        }));
        console.log('Processed bookings:', this.bookings);
        this.bookings = data.sort((a, b) => b.bookingId - a.bookingId);
      },
      (error) => {
        console.error('Error fetching bookings:', error);
      }
    );
  }
}
