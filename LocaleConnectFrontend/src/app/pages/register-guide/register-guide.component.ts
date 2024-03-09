import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { LoginComponent } from '../login/login.component';
import { AuthService } from '../../service/auth.service';
import { Guide } from '../../model/guide';
import { LANGUAGES, COUNTRIES } from '../../helper/DataHelper';

@Component({
  selector: 'app-register-guide',
  templateUrl: './register-guide.component.html',
  styleUrls: ['./register-guide.component.scss'],
})
export class RegisterGuideComponent {
  registerForm: FormGroup;
  currentPage = 1;
  totalPages = 3;
  profilePictureSrc = 'assets/pictures/profil.png';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    public dialog: MatDialog,
    private cdRef: ChangeDetectorRef
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      userName: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      dateOfBirth: ['', [Validators.required, this.adultValidator]],
      languages: ['', [Validators.required, this.minLanguagesValidator(2)]],
      bio: [''],
      city: ['', Validators.required],
      visitedCountries: ['']
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      const formValue = this.registerForm.value;
      const formattedDateOfBirth: Date = formValue.dateOfBirth
        .toISOString()
        .split('T')[0];

      const newUser: Guide = {
        ...formValue,
        dateOfBirth: formattedDateOfBirth,
        isEnabled: true,
        rating: 0,
      };
      this.authService.registerGuide(newUser).subscribe({
        next: (user) => {
          user = newUser;
          this.switchToLogin();
          return user;
        },
        error: (errorMessage) => {
          this.handleError(errorMessage);
        },
      });
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  switchToLogin(): void {
    this.dialog.closeAll();
    this.dialog.open(LoginComponent, {
      width: '400px',
      maxHeight: '600px',
    });
  }

  addProfilePicture(event: any): void {
    if (event.target.files && event.target.files[0]) {
      const file = event.target.files[0];

      const reader = new FileReader();
      reader.onload = (e: any) => (this.profilePictureSrc = e.target.result);
      reader.readAsDataURL(file);
    }
  }

  private handleError(errorMessage: string) {
    if (errorMessage.includes('userName is already taken')) {
      this.registerForm.controls['userName'].setErrors({ userExists: true });
    }
    if (errorMessage.includes('email is already in use')) {
      this.registerForm.controls['email'].setErrors({ emailExists: true });
    }
  }

  // Custom validator to check if the user is at least 18 years old
  private adultValidator(control: FormControl): ValidationErrors | null {
    if (control.value) {
      const birthDate = new Date(control.value);
      const adultAge = 18;
      const currentDate = new Date();
      const eligibleDate = new Date(
        currentDate.getFullYear() - adultAge,
        currentDate.getMonth(),
        currentDate.getDate()
      );

      if (birthDate > eligibleDate) {
        return { tooYoung: true };
      }
    }
    return null;
  }

  //custom validator to check if the user speaks more than one language
  private minLanguagesValidator(min = 2): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const selected = control.value;
      return selected?.length >= min
        ? null
        : { minLanguages: { value: control.value } };
    };
  }

  protected readonly LANGUAGES = LANGUAGES;
  protected readonly COUNTRIES = COUNTRIES;
}
