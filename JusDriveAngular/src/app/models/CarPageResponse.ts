import { Car } from "./car.model";

export interface CarPageResponse {
    cars: Car[];
    currentPage: number;
    totalPages: number;
    totalItems: number;
  }