import { Component, EventEmitter, Output } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Itinerary, Tag } from '../../../model/itinerary';
import { ItineraryService } from '../../../service/itinerary.service';
import * as DataHelper from 'src/app/helper/DataHelper';
import { ImagesService } from '../../../service/image.service';
import { ApiResponse } from 'src/app/model/apiResponse';
import { UserService } from 'src/app/service/user.service';
import { AuthService } from 'src/app/service/auth.service';
import { User } from 'src/app/model/user';
import { NotificationService } from 'src/app/service/notification.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-itinerary-dialog',
  templateUrl: 'itinerary-dialog.component.html',
  styleUrls: ['./itinerary-dialog.component.scss'],
})
export class ItineraryDialogComponent {
  itineraryForm: FormGroup;
  tagOptions: Tag[] = Object.values(Tag).filter((key) =>
    isNaN(Number(key))
  ) as Tag[];

  constructor(
    private imageService: ImagesService,
    public dialogRef: MatDialogRef<ItineraryDialogComponent>,
    private formBuilder: FormBuilder,
    private itineraryService: ItineraryService,
    private userService: UserService,
    private notificationService: NotificationService
  ) {
    this.itineraryForm = this.formBuilder.group({
      name: [, Validators.required],
      description: [, Validators.required],
      numberOfDays: ['1', [Validators.required, Validators.min(1)]],
      tags: [],
      placesToVisit: [, Validators.required],
      dailyActivities: [],
      imageUrls: [],
    });
  }

  onNoClick(): void {
    this.dialogRef.close(null);
  }

  onSubmit(): void {
    if (this.itineraryForm.valid) {
      const formData = this.itineraryForm.value;

      formData.placesToVisit = DataHelper.dataToList(formData.placesToVisit);
      formData.dailyActivities =
        formData.dailyActivities === null
          ? []
          : DataHelper.dataToList(formData.dailyActivities);

      const travellerId = this.userService.getTravellerId();
      const itinerary: Itinerary = {
        ...formData,
        userId: travellerId,
        tags: formData.tags === null ? [] : formData.tags,
        meetupAttendees: [],
        mappedTags: formData.tags,
        imageUrls: formData.imageUrls === null ? [] : formData.imageUrls,
        rating: 0,
        averageRating: 0,
        placesToVisit: formData.placesToVisit,
        dailyActivities: formData.dailyActivities,
        expand: false,
      };
      this.itineraryService.addItinerary(itinerary).subscribe({
        next: (res: ApiResponse) => {
          this.dialogRef.close(res.data);
          this.notificationService.showSuccess(
            'Itinerary Created Successfully!'
          );
        },
        error: (error: HttpErrorResponse) => {
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
            this.itineraryForm.value.imageUrls = images;
          }
        };
        reader.readAsDataURL(<Blob>file);
      });
    }
  }
}
