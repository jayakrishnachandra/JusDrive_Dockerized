import { Component, OnInit } from '@angular/core';
import { BookingService } from '../services/booking.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-booking-status',
  templateUrl: './booking-status.component.html',
  standalone: true,
   imports: [CommonModule],
  styleUrls: ['./booking-status.component.css']
})

export class BookingStatusComponent implements OnInit {
  bookings: any[] = [];
  userID = JSON.parse(localStorage.getItem('userData') || '{}').id;

  constructor(private bookingService: BookingService) {}

  ngOnInit(): void {
    this.fetchBookings();
  }

  fetchBookings(): void {
    this.bookingService.fetchBookingsByOwner().subscribe(data => {
      this.bookings = data.sort((a, b) => b.bookingId - a.bookingId);
      console.log(data);
    });
  }

  updateStatus(bookingId: number, status: 'CONFIRMED' | 'CANCELLED'): void {
    this.bookingService.updateBookingStatus(bookingId, status).subscribe(() => {
      this.fetchBookings();
    });
  }
}
