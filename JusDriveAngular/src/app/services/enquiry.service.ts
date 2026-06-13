import { Injectable} from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../environments/environments";
@Injectable({
  providedIn: 'root'
})
export class EnquiryService{
  private baseUrl=`${environment.gatewayBaseUrl}/api/enquiries`;

  constructor(private http: HttpClient){}
  getCustomerEnquiries(customerId: number): Observable<any[]> {
    const url = `${this.baseUrl}/customer/details`;
    return this.http.get<any[]>(url);
}


loadEnquiries(customerId : number){
  return this.http.get<any[]>(`${this.baseUrl}/owner`);
    
}

sendReply(enquiryId: any , body: any) {
  return this.http.post(`${this.baseUrl}/${enquiryId}/response`, body);
}

}

