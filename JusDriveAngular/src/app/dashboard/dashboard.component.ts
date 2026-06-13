

import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CarService } from '../services/car.service';
import { AuthService } from '../services/auth.service';
import { BookingService } from '../services/booking.service';
import { User } from '../models/user.model';

import { ChartModule } from 'primeng/chart';
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule,ChartModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  selectedReportData: {
    daily: any;
    weekly: any;
    monthly: any;
  } = { daily: null, weekly: null, monthly: null };
  

  
  reportChartOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: { beginAtZero: true, ticks: { precision: 0 } }
    },
    plugins: {
      legend: { position: 'bottom' }
    }
  };

  UserId: number = 0;
  registeredCarsCount = 0;
  availableCarsCount = 0;
  InActiveCarsCount = 0;
  BookedCarsCount = 0;

  selectedReportType: 'daily' | 'weekly' | 'monthly' = 'daily';
  options: any;
  data: any;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private carService: CarService,
    private authService: AuthService,
    private bookingService: BookingService
  ) {}

  ngOnInit(): void {
    this.authService.getUserDetails().subscribe({
      next: (data: User) => {
        const userData = JSON.parse(localStorage.getItem('userData') || '{}');
        if (userData?.id) {
          this.UserId = userData.id;
          this.fetchCarStatistics();
        } else {
          console.error('No user data found in localStorage');
        }
      },
      error: (err) => console.error('Error fetching user details:', err)
    });
  }


  
  initChart(): void {
    this.data = {
      labels: [ 'Available Cars', 'Inactive Cars', 'Booked Cars'],
      datasets: [
        {
          data: [
            this.availableCarsCount,
            this.InActiveCarsCount,
            this.BookedCarsCount
          ],
          backgroundColor: [ 'rgba(9, 159, 105, 0.7)','rgba(246, 141, 42, 0.7)','rgba(51, 133, 240, 0.7)'],
          hoverBackgroundColor: [ '#099f69', '#f68d2a','#3385f0']
        }
      ]
    };

    this.options = {
      cutout: '60%',
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            color: '#333'
          }
        }
      }
    };
  }

  fetchCarStatistics(): void {
    if (this.UserId) {
      this.carService.getCarStatistics(this.UserId).subscribe({
        next: (data) => {
          this.registeredCarsCount = data.totalCars;
          this.availableCarsCount = data.availableCars;
          this.InActiveCarsCount = data.inactiveCars;
          this.BookedCarsCount = data.bookedCars;
          
          this.initChart();
          this.loadAllReports();
        },
        error: (err) => console.error('Error fetching car statistics:', err)
      });
    }
  }


  private loadAllReports(): void {
    this.bookingService.getDailyReport(7).subscribe(data => {
      this.selectedReportData.daily = this.buildChartData(data, 'dateLabel', '#42A5F5');
    });
    this.bookingService.getWeeklyReport(4).subscribe(data => {
      this.selectedReportData.weekly = this.buildChartData(data, 'weekLabel', '#66BB6A');
    });
    this.bookingService.getMonthlyReport(12).subscribe(data => {
      this.selectedReportData.monthly = this.buildChartData(data, 'monthLabel',  '#FFA726');
    });
  }
  
  private buildChartData(data: any[], labelField: string,  color: string) {
    return {
      labels: data.map(d => d[labelField]),
      datasets: [
        {
          label: 'Bookings',
          data: data.map(d => d.bookingsCount),
          borderColor: color,
          fill: false,
          tension: 0.3
        }
      ]
    };
  }


  
  goToVehicleManagement(): void {
    this.router.navigate(['/vehicle-management']);
  }
}
