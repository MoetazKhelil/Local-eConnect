import {Component} from '@angular/core';
import {LoginComponent} from "../../pages/login/login.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent{
  title = 'LocaleConnect';

  constructor(public dialog: MatDialog) {

  }

  openLoginDialog(): void {
    this.dialog.open(LoginComponent, {
      width: '400px'
    });
  }
}
