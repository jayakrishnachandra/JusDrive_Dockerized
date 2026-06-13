import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnerEnquiryResponseComponent } from './owner-enquiry-response.component';

describe('OwnerEnquiryResponseComponent', () => {
  let component: OwnerEnquiryResponseComponent;
  let fixture: ComponentFixture<OwnerEnquiryResponseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OwnerEnquiryResponseComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OwnerEnquiryResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
