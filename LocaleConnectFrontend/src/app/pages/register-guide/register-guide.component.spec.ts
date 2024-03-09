import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterGuideComponent } from './register-guide.component';

describe('RegisterGuideComponent', () => {
  let component: RegisterGuideComponent;
  let fixture: ComponentFixture<RegisterGuideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterGuideComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterGuideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
