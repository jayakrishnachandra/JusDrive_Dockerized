import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environments';

export interface DailyReportDTO {
  date: string;         
  bookingsCount: number;
  dateLabel: string;   
}

export interface WeeklyReportDTO {
  weekNumber: number;
  startDate: string;   
  endDate: string;     
  bookingsCount: number;
  weekLabel: string;  
}

export interface MonthlyReportDTO {
  month: number;        
  year: number;
  bookingsCount: number;
  monthLabel: string;   
}


@Injectable({
  providedIn: 'root'
})
export class BookingService {

  
  private baseApiUrl = `${environment.gatewayBaseUrl}/api/cars`; 
  private bookingApiUrl = `${environment.gatewayBaseUrl}/api/bookings`;
  private enquiryApiUrl = `${environment.gatewayBaseUrl}/api/enquiries`;


  

  constructor(private http: HttpClient) {}

  // Fetch cars
  getAvailableCars(pg: number, size: number): Observable<any> {
    return this.http.get<any[]>(`${this.baseApiUrl}/available?pg=${pg}&size=${size}`);
  }

  searchCars(searchKeyword : String)
  {
     return this.http.get<any[]>(`${this.baseApiUrl}/search/${searchKeyword}`);
  }




  
  bookCar(bookingDetails: any): Observable<any> {
    return this.http.post(`${this.bookingApiUrl}/new`, bookingDetails);
  }

  enquireCar(customerId: number, carId: number, message: string): Observable<any> {
    console.log('enquiry payload:', { customerId, carId, message });
    const enquiryPayload = {
      customerId: customerId,
      carId: carId,
      message: message
    };
    return this.http.post(`${this.enquiryApiUrl}/new`, enquiryPayload);
  }


  fetchBookingsByCustomer(): Observable<any[]> {
    return this.http.get<any[]>(`${this.bookingApiUrl}/customer`);
  }


  fetchBookingsByOwner() {
    return this.http.get<any[]>(`${this.bookingApiUrl}/owner`);
  }


  updateBookingStatus(bookingId: number, status: 'CONFIRMED' | 'CANCELLED') {

    console.log(status);
    return this.http.put(`${this.bookingApiUrl}/${bookingId}/status`, null, {
      params: { status }
    });
  }


  getDailyReport(days = 7): Observable<DailyReportDTO[]> {
    return this.http.get<DailyReportDTO[]>(
      `${this.bookingApiUrl}/owner/reports/daily?days=${days}`
    );
  }



  getWeeklyReport(weeks = 4): Observable<WeeklyReportDTO[]> {
    return this.http.get<WeeklyReportDTO[]>(
      `${this.bookingApiUrl}/owner/reports/weekly?weeks=${weeks}`
    );
  }


  getMonthlyReport(months = 12): Observable<MonthlyReportDTO[]> {
    return this.http.get<MonthlyReportDTO[]>(
      `${this.bookingApiUrl}/owner/reports/monthly?months=${months}`
    );
  }
  


}
