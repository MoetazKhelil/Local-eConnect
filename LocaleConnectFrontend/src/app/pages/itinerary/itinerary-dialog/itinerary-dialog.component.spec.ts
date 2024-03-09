import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItineraryDialogComponent } from './itinerary-dialog.component';

describe('ItineraryDialogComponent', () => {
  let component: ItineraryDialogComponent;
  let fixture: ComponentFixture<ItineraryDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ItineraryDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ItineraryDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
