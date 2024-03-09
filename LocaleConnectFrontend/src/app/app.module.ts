import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {CoreModule} from "./core/core.module";
import {LandingPageModule} from "./pages/landing-page/landing-page.module";
import {LoginComponent} from './pages/login/login.component';
import {MatCardModule} from "@angular/material/card";
import {MatInputModule} from "@angular/material/input";
import {MatDialogModule} from "@angular/material/dialog";
import {RegisterComponent} from './pages/register/register.component';
import {MatButtonModule} from "@angular/material/button";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RegisterGuideComponent} from './pages/register-guide/register-guide.component';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatSelectModule} from "@angular/material/select";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MAT_DATE_LOCALE, MatNativeDateModule} from "@angular/material/core";
import {ItineraryComponent} from './pages/itinerary/itinerary.component';
import {ItineraryDialogComponent} from './pages/itinerary/itinerary-dialog/itinerary-dialog.component';
import {MatIconModule} from "@angular/material/icon";
import {MatChipsModule} from "@angular/material/chips";
import {ReviewComponent} from './pages/review/review.component';
import {FeedComponent} from './pages/feed/feed.component';
import {MatListModule} from "@angular/material/list";
import {AddPostDialogComponent} from './pages/feed/add-post-dialog/add-post-dialog.component';
import {PickerModule} from "@ctrl/ngx-emoji-mart";
import {MeetupComponent} from './pages/meetup/meetup.component';
import {MeetupDialogComponent} from './pages/meetup/meetup-dialog/meetup-dialog.component';
import {GuideComponent} from './pages/guide/guide.component';
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {TripPreviewComponent} from './pages/guide/trip-preview/trip-preview.component';
import {MatStepperModule} from "@angular/material/stepper";
import {TripComponent} from './pages/trip/trip.component';
import {TripDialogComponent} from './pages/trip/trip-dialog/trip-dialog.component';
import {AuthInterceptor} from "./service/auth-interceptor";


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    RegisterGuideComponent,
    ItineraryComponent,
    ItineraryDialogComponent,
    ReviewComponent,
    FeedComponent,
    AddPostDialogComponent,
    MeetupComponent,
    MeetupDialogComponent,
    GuideComponent,
    TripPreviewComponent,
    TripComponent,
    TripDialogComponent,


  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    CoreModule,
    LandingPageModule,
    MatCardModule,
    MatInputModule,
    MatDialogModule,
    MatButtonModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatDatepickerModule,
    MatSelectModule,
    MatCheckboxModule,
    MatNativeDateModule,
    FormsModule,
    MatIconModule,
    MatChipsModule,
    MatListModule,
    PickerModule,
    MatPaginatorModule,
    MatSnackBarModule,
    MatStepperModule


  ],
  providers: [MatDatepickerModule, {provide: MAT_DATE_LOCALE, useValue: 'de-DE'},    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }],


  bootstrap: [AppComponent]
})
export class AppModule {
}
