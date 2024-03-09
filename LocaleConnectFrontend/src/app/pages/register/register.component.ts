import { Component } from '@angular/core';
import { LoginComponent } from '../login/login.component';
import { MatDialog } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RegisterGuideComponent } from '../register-guide/register-guide.component';
import { AuthService } from '../../service/auth.service';
import { Traveler } from '../../model/traveler';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {
  registerForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    public dialog: MatDialog
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      userName: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      dateOfBirth: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      const formValue = this.registerForm.value;
      const formattedDateOfBirth = formValue.dateOfBirth
        .toISOString()
        .split('T')[0];
      const newUser: Traveler = {
        ...formValue,
        dateOfBirth: formattedDateOfBirth,
        isEnabled: true,
      };

      this.authService.registerTraveler(newUser).subscribe({
        next: (user) => {
          user = newUser;
          this.switchToLogin();
          return user;
        },
        error: (errorMessage) => {
          console.error('Registration error', errorMessage);
          this.handleError(errorMessage);
        },
      });
    }
  }

  switchToLogin(): void {
    this.dialog.closeAll();
    this.dialog.open(LoginComponent, {
      width: '400px',
      maxHeight: '600px',
    });
  }

  openGuideDialog(): void {
    this.dialog.open(RegisterGuideComponent, {
      width: '400px',
      height: '700px',
    });
  }

  private handleError(errorMessage: string) {
    if (errorMessage.includes('userName is already taken')) {
      this.registerForm.controls['userName'].setErrors({ userExists: true });
    }
    if (errorMessage.includes('email is already in use')) {
      this.registerForm.controls['email'].setErrors({ emailExists: true });
    }
  }
}
