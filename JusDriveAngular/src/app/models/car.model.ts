export interface Car {
  carId: number;             
  brand: string;              
  model: string;              
  registrationNumber: string;  
  year: number | null;                
  color: string;              
  carType: string;            
  seatingCapacity: number | null;    
  pricePerDay: number | null;        
  status: string;              
  isAvailable: boolean;        
  image?: Blob;                
  createdAt?: Date;            
  modifiedAt?: Date;    
}       
