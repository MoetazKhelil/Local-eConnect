import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { debounceTime, distinctUntilChanged, Subscription } from 'rxjs';
import { FormControl } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { NotificationService } from '../../service/notification.service';
import { UserService } from '../../service/user.service';
import { ReviewService } from '../../service/review.service';
import { MatDialog } from '@angular/material/dialog';
import { Trip } from '../../model/trip';
import { TripService } from '../../service/trip.service';
import { TripDialogComponent } from './trip-dialog/trip-dialog.component';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiResponse } from 'src/app/model/apiResponse';
import { ImagesService } from 'src/app/service/image.service';
import { User } from 'src/app/model/user';

@Component({
  selector: 'app-trip',
  templateUrl: './trip.component.html',
  styleUrls: ['./trip.component.scss'],
})
export class TripComponent implements OnInit, OnDestroy {
  trips: Trip[] = [];
  searchTrips: Trip[] = [];
  searchControl = new FormControl('');
  subscription: Subscription = new Subscription();
  totalLength = 0;
  displayedTrips: Trip[] = [];
  pageSize = 10;
  showAllImages = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private notificationService: NotificationService,
    private dialog: MatDialog,
    private reviewService: ReviewService,
    private tripService: TripService,
    private userService: UserService,
    private imageService: ImagesService
  ) {}

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  ngOnInit(): void {
    const travellerId = this.userService.getTravellerId();
    this.tripService.getAllTrips().subscribe({
      next: (res: ApiResponse) => {
        const trips = res.data as Trip[];
        this.trips = trips;
        this.totalLength = this.trips.length;
        this.initializeDisplayedTrips();
        this.searchTrips = [...this.trips];
        this.trips.forEach((trip) => {
          if (trip.tripAttendees.includes(travellerId)) {
            trip.isAttending = true;
          } else {
            trip.isAttending = false;
          }

          // populate userName
          this.userService.getUserById(trip.localguideId).subscribe({
            next: (res: ApiResponse) => {
              const user = res.data as User;
              trip.localguideuserName = user.userName;
            },
          });

          if (trip.imageUrls.length > 0) {
            if (trip.imageUrls[0].length > 0) {
              this.imageService.getImage(trip.imageUrls[0]).subscribe({
                next: (gcpRes: ApiResponse) => {
                  trip.imageUrls = [];
                  trip.imageUrls.push(gcpRes.data.toString());
                },
                error: (errorMessage: ApiResponse) =>
                  console.error(errorMessage.errors),
              });
            }
          }

          this.userService.getUserById(trip.localguideId).subscribe({
            next: (res: ApiResponse) => {
              const user = res.data as User;
              trip.localguideuserName = user.userName;
            },
          });
        });
      },
      error: (error: HttpErrorResponse) => {},
    });

    this.subscription = this.tripService.currentTrip.subscribe((trip) => {
      if (trip) {
        this.trips.push(trip);
        this.totalLength = this.trips.length;
        this.updateDisplayedTrips();
      }
    });

    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((searchTerm) => {
        this.performSearch(searchTerm);
      });
  }

  ngAfterViewInit() {
    this.paginator.page.subscribe(() => {
      this.updateDisplayedTrips();
    });
  }

  initializeDisplayedTrips(): void {
    this.displayedTrips = this.trips.slice(0, this.pageSize);
  }

  updateDisplayedTrips(): void {
    const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
    const endIndex = startIndex + this.paginator.pageSize;
    this.displayedTrips = this.trips.slice(startIndex, endIndex);
  }

  checkGuideTripsAndDelete(tripId: number): void {
    const travellerId = this.userService.getTravellerId();
    const trip = this.trips.find((trip) => trip.id === tripId);
    if (trip?.localguideId === travellerId) {
      this.deleteTrip(tripId);
    } else {
      this.notificationService.showError(
        'Trip was created by another localguide!'
      );
    }
  }

  deleteTrip(id: number): void {
    const confirmDelete = confirm('Are you sure you want to delete this trip?');

    if (!confirmDelete) return;
    this.tripService.deleteTrip(id).subscribe({
      next: () => {
        this.notificationService.showSuccess('Trip deleted successfully!');
        this.trips = this.trips.filter((trip) => trip.id !== id);
        this.totalLength = this.trips.length;
        this.updateDisplayedTrips();
      },
      error: (error) => {
        console.error('Error deleting itinerary', error);
        this.notificationService.showError('Failed to delete Trip!');
      },
    });
  }

  performSearch(searchTerm: string | null = ''): void {
    if (searchTerm) {
      this.tripService.searchTrip(searchTerm).subscribe((data) => {
        this.displayedTrips = data;
      });
    } else {
      this.displayedTrips = [...this.searchTrips];
    }
  }

  toggleDetails(trip: Trip): void {
    trip.expand = !trip.expand;
  }

  submitRating(trip: Trip, rating: number): void {
    if (trip.rating !== 0) {
      trip.ratingSubmitted = true;

      const userId = this.userService.getTravellerId();
      this.tripService.rateItinerary(trip.id, userId, rating).subscribe({
        next: (res: ApiResponse) => {
          const updatedItinerary: Trip = res.data as Trip;
          trip.averageRating = updatedItinerary.averageRating;
          trip.ratingsCount = updatedItinerary.ratingsCount;
        },
        error: (error: HttpErrorResponse) => {
          this.notificationService.showError(error.error.errors.errors[0]);
        },
      });
      this.notificationService.showSuccess(
        'You submitted the rate successfully!'
      );
      this.updateDisplayedTrips();
    }
  }

  joinTrip(tripId: number): void {
    const travellerId = this.userService.getTravellerId();
    this.tripService.joinTrip(tripId, travellerId).subscribe({
      next: (res: ApiResponse) => {
        if (res.status === 200) {
          const currentTrip = this.trips.find((trip) => trip.id === tripId);
          if (currentTrip) currentTrip.isAttending = true;
          this.initializeDisplayedTrips();
        }
        this.notificationService.showSuccess('You joined Trip Successfully!');
      },
      error: (error) => {
        this.notificationService.showError('Failed to join Trip.');
      },
    });
  }

  leaveTrip(tripId: number): void {
    const travellerId = this.userService.getTravellerId();
    this.tripService.leaveTrip(tripId, travellerId).subscribe({
      next: (res: ApiResponse) => {
        if (res.status === 200) {
          const currentTrip = this.trips.find((trip) => trip.id === tripId);
          if (currentTrip) currentTrip.isAttending = false;
        }
        this.notificationService.showSuccess('You left Trip Successfully!');
      },
      error: (error) => {
        this.notificationService.showError('Failed to leave Trip.');
      },
    });
  }
  openAddMeetupDialog(): void {
    const dialogRef = this.dialog.open(TripDialogComponent, {
      width: '600px',
      height: '600px',
    });

    dialogRef.afterClosed().subscribe((newTrip: Trip) => {
      newTrip.expand = false;
      this.trips.push(newTrip);
      this.searchTrips = [...this.trips];
      this.totalLength = this.trips.length;
      this.updateDisplayedTrips();
    });
  }

  toggleJoin(trip: Trip): void {
    if (trip?.isAttending) {
      this.leaveTrip(trip.id);
    } else {
      this.joinTrip(trip.id);
    }
    // if (trip.isAttending) {
    //   this.tripService.joinTrip(trip.id, travellerId).subscribe(() => {
    //     trip.isAttending = false;
    //     trip.isAttending = !trip.isAttending;
    //   });
    // } else {
    //   this.tripService.leaveTrip(trip.id, travellerId).subscribe(() => {
    //     trip.isAttending = true;
    //     trip.isAttending = !trip.isAttending;
    //   });
    // }
  }
  toggleImagesDisplay() {
    this.showAllImages = !this.showAllImages;
  }
}
