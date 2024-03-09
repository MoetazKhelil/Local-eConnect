import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatButtonModule } from '@angular/material/button';
import {NgModule} from "@angular/core";
import {LandingPageComponent} from "./landing-page.component";
import {NgOptimizedImage} from "@angular/common";

@NgModule({
  declarations: [
    LandingPageComponent
  ],
  imports: [
    MatCardModule,
    MatGridListModule,
    MatButtonModule,
    NgOptimizedImage,
  ],
})
export class LandingPageModule { }
