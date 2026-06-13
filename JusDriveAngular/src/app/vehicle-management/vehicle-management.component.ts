import { Component, inject, signal, effect, OnInit } from '@angular/core';
import { NgFor, NgIf, NgTemplateOutlet } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CarService } from '../services/car.service';
import { Car } from '../models/car.model';
 
interface CarWithUI extends Car {
  editing?: boolean;
  confirmingRemoval?: boolean;
}
 
@Component({
  selector: 'app-vehicle-management',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule,NgTemplateOutlet],
  templateUrl: './vehicle-management.component.html',
  styleUrls: ['./vehicle-management.component.css']
})
export class VehicleManagementComponent implements OnInit{
  private carService = inject(CarService);
 
  cars = signal<CarWithUI[]>([]);
  carImages = signal<{ [key: number]: string }>({});
  showAddCarForm = signal(false);
  currentYear = new Date().getFullYear();
  UserId: any;
 
  get carList(): CarWithUI[] {
    return this.cars() ?? [];
  }
 
 
  newCar: Partial<Car> = {
    brand: '',
    model: '',
    registrationNumber: '',
    year: null,
    color: '',
    carType: '',
    seatingCapacity: null,
    pricePerDay: null,
    status: '',
    isAvailable: true
  };
 
  selectedFile: File | null = null;
  editingCar: CarWithUI | null = null;
  editingFile: File | null = null;
 
  constructor() {
    effect(() => {
      const data = this.cars();
      data.forEach(car => {
        this.carService.getCarImage(car.carId).subscribe(imageBlob => {
          const url = URL.createObjectURL(imageBlob);
          this.carImages.update(prev => ({ ...prev, [car.carId]: url }));
        });
      });
    });
  }
  ngOnInit(): void {
    const userData = JSON.parse(localStorage.getItem('userData') || '{}');
    if (userData) {
      console.log('User ID is:', userData.id);
      this.UserId = userData.id; 
      this.refreshCars();
    } else {
      console.error('No user data found in localStorage');
    }
  }
 
  refreshCars(): void {
    this.carService.getOwnerCars(this.UserId).subscribe(list => this.cars.set(list));
  }
 
  toggleAddCarForm(): void {
    this.showAddCarForm.update(v => !v);
    if (!this.showAddCarForm()) {
      this.resetForm();
    }
  }
 
  resetForm(): void {
    this.newCar = {
      brand: '',
      model: '',
      registrationNumber: '',
      year: null,
      color: '',
      carType: '',
      seatingCapacity: null,
      pricePerDay: null,
      status: '',
      isAvailable: true
    };
    this.selectedFile = null;
  }
 
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.selectedFile = input.files[0];
    }
  }
 
  onAddCar(): void {
    if (!this.validateCar(this.newCar)) return;
 
    this.carService.addCar(this.UserId, this.newCar as Car).subscribe({
      next: (added) => {
        if (this.selectedFile) {
          this.carService.updateCarImage(added.carId, this.selectedFile).subscribe(() => {
            this.carService.getCarImage(added.carId).subscribe(img => {
              const url = URL.createObjectURL(img);
              this.carImages.update(prev => ({ ...prev, [added.carId]: url }));
            });
          });
        }
        this.toggleAddCarForm();
        this.refreshCars();
      },
      error: (err) => {
        if (err.status === 409) {
          alert('A car with this registration number already exists.');
        } else if (err.status === 400) {
          alert('Invalid input. Please check all required fields.');
        } else if (err.status === 404) {
          alert('The specified owner does not exist.');
        } else {
          console.error('Unexpected error while adding car:', err);
          alert('An unexpected error occurred. Please try again later.');
        }
      }
    });
  }
 
  validateCar(car: Partial<Car>): boolean {
    const plate = /^[A-Z]{2}[- ]?[0-9]{2}[- ]?[A-Z]{2}[- ]?[0-9]{4}$/;
    if (!car.registrationNumber || !plate.test(car.registrationNumber)) {
      alert('Invalid registration number. Use TN01AB1234 or TN-01-AB-1234 or TN 01 AB 1234.');
      return false;
    }
    if (!car.year || car.year < 1980 || car.year > this.currentYear) {
      alert('Please enter a valid manufacturing year.');
      return false;
    }
    return true;
  }
 
 
  enterEditMode(car: CarWithUI): void {
    this.editingCar = { ...car };
    this.editingFile = null;
  }
 
  cancelEdit(): void {
    this.editingCar = null;
    this.editingFile = null;
  }
 
  onEditFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.editingFile = input.files[0];
    }
  }
 
  onSaveEdit(): void {
    if (!this.editingCar || !this.validateCar(this.editingCar)) return;
 
    this.carService.editCar(this.editingCar.carId, this.UserId, this.editingCar).subscribe(updated => {
      if (this.editingFile) {
        this.carService.updateCarImage(updated.carId, this.editingFile).subscribe(() => {
          this.carService.getCarImage(updated.carId).subscribe(img => {
            const url = URL.createObjectURL(img);
            this.carImages.update(prev => ({ ...prev, [updated.carId]: url }));
          });
        });
      }
      this.refreshCars();
      this.cancelEdit();
    });
  }
 
  toggleRemoveConfirmation(car: CarWithUI): void {
    car.confirmingRemoval = true;
  }
 
  cancelRemove(car: CarWithUI): void {
    car.confirmingRemoval = false;
  }
 
  onRemoveCar(car: CarWithUI): void {
    this.carService.removeCar(car.carId, this.UserId).subscribe(() => {
      this.cars.update(list => list.filter(c => c.carId !== car.carId));
    });
  }
 
  onToggleStatus(car: Car): void {
    if (car.status.toLowerCase() === 'booked') return;
    this.carService.toggleStatus(car.carId).subscribe(updated => {
      car.status = updated.status;
      car.isAvailable = updated.isAvailable;
    });
  }
}
 
 