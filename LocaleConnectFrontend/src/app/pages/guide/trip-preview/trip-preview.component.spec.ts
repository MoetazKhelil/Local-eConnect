import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripPreviewComponent } from './trip-preview.component';

describe('TripPreviewComponent', () => {
  let component: TripPreviewComponent;
  let fixture: ComponentFixture<TripPreviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TripPreviewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TripPreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
