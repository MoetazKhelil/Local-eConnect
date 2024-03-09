import {Component} from '@angular/core';
import {RegisterComponent} from "../register/register.component";
import {MatDialog} from "@angular/material/dialog";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../service/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private authService: AuthService, public dialog: MatDialog, private router: Router) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value.email, this.loginForm.value.password).subscribe(
        {
          next: (user) => {
            this.router.navigate(['pages/home']).then(r => this.dialog.closeAll()
            );

            return user;
          },
          error: (errorMessage) => {
            console.error("Login error", errorMessage);
            this.handleError(errorMessage);
          }
        }
      );

    }
  }

  openRegisterDialog(): void {
    this.dialog.open(RegisterComponent, {
      width: '400px', height: '700px'
    });
  }

  private handleError(errorMessage: string) {
    if (errorMessage.includes('email not found')) {
      this.loginForm.controls['email'].setErrors({userDoesntExist: true});
    }
    if (errorMessage.includes('wrong password')) {
      this.loginForm.controls['password'].setErrors({wrongPassword: true});
    }
  }
}
