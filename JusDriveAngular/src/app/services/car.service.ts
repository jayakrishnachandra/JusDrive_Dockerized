// src/app/services/car.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Car } from '../models/car.model';
import { log } from 'node:console';
import { response } from 'express';
import { environment } from '../../environments/environments';

@Injectable({
  providedIn: 'root'
})
export class CarService {
  private baseUrl = `${environment.gatewayBaseUrl}/api/cars`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwtToken');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Access-Control-Allow-Origin': '*'
    });
  }

  // Get all
  getOwnerCars(ownerId: number): Observable<Car[]> {
    return this.http.get<Car[]>(`${this.baseUrl}/owner`, { headers: this.getHeaders() });
  }

  addCar(ownerId: number, car: Car): Observable<Car> {
    return this.http.post<Car>(`${this.baseUrl}/newCar`, car, { headers: this.getHeaders() });
  }

  editCar(carId: number, ownerId: number, car: Car): Observable<Car> {
    return this.http.patch<Car>(`${this.baseUrl}/${carId}/edit`, car, { headers: this.getHeaders() });
  }

  removeCar(carId: number, ownerId: number): Observable<string> {
    return this.http.delete<string>(`${this.baseUrl}/${carId}/remove`, { headers: this.getHeaders() });
  }

  getCarImage(carId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/getCarImage/${carId}`, { headers: this.getHeaders(), responseType: 'blob' });
  }

  toggleStatus(carId: number): Observable<Car> {
    return this.http.patch<Car>(`${this.baseUrl}/${carId}/toggleStatus`, {}, { headers: this.getHeaders() }); 
  }

  updateCarImage(carId: number, file: File): Observable<Car> {
    const formData = new FormData();
    formData.append('image', file, file.name);
    return this.http.put<Car>(`${this.baseUrl}/updateCarImage?carId=${carId}`, formData, { headers: this.getHeaders() });
  }

  getCarStatistics(ownerId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/statistics`, { headers: this.getHeaders() });
  }
}

