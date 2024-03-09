import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TripService } from '../../../service/trip.service';
import { LANGUAGES } from '../../../helper/DataHelper';
import { Trip } from '../../../model/trip';
import * as DataHelper from 'src/app/helper/DataHelper';
import { getFormattedDateAndTime } from 'src/app/helper/DateHelper';
import { UserService } from 'src/app/service/user.service';
import { NotificationService } from 'src/app/service/notification.service';
import { ApiResponse } from 'src/app/model/apiResponse';

@Component({
  selector: 'app-trip-dialog',
  templateUrl: './trip-dialog.component.html',
  styleUrls: ['./trip-dialog.component.scss'],
})
export class TripDialogComponent {
  tripForm: FormGroup;
  readonly LANGUAGES = LANGUAGES;

  constructor(
    public dialogRef: MatDialogRef<TripDialogComponent>,
    private formBuilder: FormBuilder,
    private tripService: TripService,
    private userService: UserService,
    private notificationService: NotificationService
  ) {
    this.tripForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: [''],
      departureTime: ['', Validators.required],
      destination: ['', Validators.required],
      durationInDays: ['', [Validators.required, Validators.min(1)]],
      capacity: ['', [Validators.required, Validators.min(1)]],
      languages: ['', Validators.required],
      placesToVisit: ['', Validators.required],
      dailyActivities: [''],
      imageUrls: [],
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    const travellerId = this.userService.getTravellerId();
    if (this.tripForm.valid) {
      const formData = this.tripForm.value;
      formData.placesToVisit = DataHelper.dataToList(formData.placesToVisit);
      formData.dailyActivities = DataHelper.dataToList(
        formData.dailyActivities
      );

      const newTrip: Trip = {
        ...formData,
        departureTime: getFormattedDateAndTime(formData.departureTime).split(
          ' '
        )[0],
        localguideId: travellerId,
        ratingsTotal: 0,
        averageRating: 0,
        travelers: [travellerId],
        durationInHours: formData.durationInDays,
        imageUrls: formData.imageUrls === null ? [] : formData.imageUrls,
      };

      delete newTrip.durationInDays;

      this.tripService.createTrip(newTrip).subscribe({
        next: (res: ApiResponse) => {
          if (!res.errors) {
            console.log(res);

            this.dialogRef.close(res.data);
            this.notificationService.showSuccess('Trip Created Successfully!');
          }
        },
        error: (error) => {
          console.error('Error creating trip:', error);
          this.notificationService.showError(error.error.errors.errors[0]);
        },
      });
    }
  }

  onFileSelected(event: any) {
    const files = event.target.files;
    const images: string[] = [];

    if (files) {
      Array.from(files).forEach((file) => {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          images.push(e.target.result);
          if (images.length === files.length) {
            this.tripForm.value.imageUrls = images;
          }
        };
        reader.readAsDataURL(<Blob>file);
      });
    }
  }
}
