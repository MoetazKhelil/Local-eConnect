import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Meetup } from '../../model/meetup';
import { MeetupService } from '../../service/meetup.service';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subscription } from 'rxjs';
import { UserService } from '../../service/user.service';
import { ReviewService } from '../../service/review.service';
import { MatDialog } from '@angular/material/dialog';
import { MeetupDialogComponent } from './meetup-dialog/meetup-dialog.component';
import { NotificationService } from '../../service/notification.service';
import { MatPaginator } from '@angular/material/paginator';
import { GuideProfile } from '../../model/guide';
import { ApiResponse } from 'src/app/model/apiResponse';
import { AuthService } from 'src/app/service/auth.service';
import { User } from 'src/app/model/user';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-meetup',
  templateUrl: './meetup.component.html',
  styleUrls: ['./meetup.component.scss'],
})
export class MeetupComponent implements OnInit, OnDestroy {
  meetups: Meetup[] = [];
  searchMeetups: Meetup[] = [];
  searchControl = new FormControl('');
  subscription: Subscription = new Subscription();
  totalLength = 0;
  displayedMeetups: Meetup[] = [];
  pageSize = 10;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private notificationService: NotificationService,
    private dialog: MatDialog,
    private reviewService: ReviewService,
    private meetupService: MeetupService,
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  ngOnInit(): void {
    this.initializeDisplayedMeetups();

    this.meetupService.getAllMeetups().subscribe({
      next: (meetupRes: ApiResponse) => {
        this.meetups = meetupRes.data as Meetup[];
        this.meetups = [...this.meetups];
        this.totalLength = this.meetups.length;
        const travellerId = this.userService.getTravellerId();
        this.meetups.forEach((meetup) => {
          if (meetup.meetupAttendees.includes(travellerId)) {
            meetup.isAttending = true;
          } else {
            meetup.isAttending = false;
          }

          // populate userName
          this.userService.getUserById(meetup.creatorId).subscribe({
            next: (res: ApiResponse) => {
              const user = res.data as User;
              meetup.creatorName = user.userName;
            },
          });
        });
        this.initializeDisplayedMeetups();
        this.searchMeetups = [...this.meetups];
      },
      error: (errorMessage: ApiResponse) => console.error(errorMessage.errors),
    });

    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((searchTerm) => {
        this.performSearch(searchTerm);
      });
  }

  ngAfterViewInit() {
    this.paginator.page.subscribe(() => {
      this.updateDisplayedMeetups();
    });
  }

  initializeDisplayedMeetups(): void {
    this.displayedMeetups = this.meetups.slice(0, this.pageSize);
  }

  updateDisplayedMeetups(): void {
    const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
    const endIndex = startIndex + this.paginator.pageSize;
    this.displayedMeetups = this.meetups.slice(startIndex, endIndex);
  }

  performSearch(searchTerm: string | null = ''): void {
    this.displayedMeetups = searchTerm
      ? this.meetupService.searchMeetups(searchTerm, this.searchMeetups)
      : [...this.searchMeetups];
  }

  deleteMeetup(id: number): void {
    const confirmDelete = confirm(
      'Are you sure you want to delete this Meetup?'
    );

    if (!confirmDelete) return;
    this.meetupService.deleteMeetup(id).subscribe({
      next: () => {
        this.notificationService.showSuccess('Meetup deleted successfully!');
        this.meetups = this.meetups.filter((meetup) => meetup.id !== id);
        this.updateDisplayedMeetups();
      },
      error: (error) => {
        console.error('Error deleting meetup', error);
        this.notificationService.showError('Failed to delete meetup.');
      },
    });
  }

  attendMeetup(meetupId: number): void {
    const travellerId = this.userService.getTravellerId();
    this.meetupService.attendMeetup(meetupId, travellerId).subscribe({
      next: (res: ApiResponse) => {
        if (res.status === 200) {
          const currentMeetup = this.meetups.find(
            (meetup) => meetup.id === meetupId
          );
          if (currentMeetup) currentMeetup.isAttending = true;
          this.updateDisplayedMeetups();
        }
      },
      error: (errorMessage: ApiResponse) => console.error(errorMessage.errors),
    });
  }

  checkUserMeetupsBeforeDeletion(meetupId: number): void {
    const travellerId = this.userService.getTravellerId();
    const meetup = this.meetups.find((meetup) => meetup.id === meetupId);
    if (meetup?.creatorId === travellerId) {
      this.deleteMeetup(meetupId);
    } else {
      this.notificationService.showError(
        'Meetup was created by another traveller!'
      );
    }
  }

  changeMeetupAttendance(meetupId: number): void {
    const currentMeetup = this.meetups.find((meetup) => meetup.id === meetupId);
    if (currentMeetup?.isAttending) {
      this.unattendMeetup(meetupId);
    } else {
      this.attendMeetup(meetupId);
    }
  }

  unattendMeetup(meetupId: number): void {
    const travellerId = this.userService.getTravellerId();
    this.meetupService.unattendMeetup(meetupId, travellerId).subscribe({
      next: (res: ApiResponse) => {
        if (res.status === 200) {
          const currentMeetup = this.meetups.find(
            (meetup) => meetup.id === meetupId
          );
          if (currentMeetup) currentMeetup.isAttending = false;
          this.initializeDisplayedMeetups();
        }
      },
      error: (errorMessage: ApiResponse) => console.error(errorMessage.errors),
    });
  }

  toggleDetails(meetup: Meetup): void {
    meetup.expand = !meetup.expand;
  }

  submitRating(meetup: Meetup, rating: number): void {
    if (meetup.rating !== 0) {
      meetup.ratingSubmitted = true;

      const userId = this.userService.getTravellerId();
      this.meetupService.rateMeetup(meetup.id, userId, rating).subscribe({
        next: (res: ApiResponse) => {
          const updatedMeetup: Meetup = res.data as Meetup;
          meetup.averageRating = updatedMeetup.averageRating;
          meetup.ratingsCount = updatedMeetup.ratingsCount;
        },
        error: (error: HttpErrorResponse) => {
          this.notificationService.showError(error.error.errors.errors[0]);
        },
      });
      this.notificationService.showSuccess(
        'You submitted the review successfully!'
      );
      this.initializeDisplayedMeetups();
    }
  }

  openAddMeetupDialog(): void {
    const dialogRef = this.dialog.open(MeetupDialogComponent, {
      width: '600px',
      height: '600px',
    });
    dialogRef.afterClosed().subscribe((newMeetup: Meetup) => {
      this.meetups.push(newMeetup);
      this.initializeDisplayedMeetups();
    });
  }

  // toggleAttend(meetup: Meetup): void {
  //   meetup.isAttending = !meetup.isAttending;
  //   if (meetup.isAttending) {
  //     this.meetupService
  //       .attendMeetup(meetup.id, this.userService.getTravellerId())
  //       .subscribe(() => {
  //         meetup.isAttending = false;
  //       });
  //   } else {
  //     this.meetupService
  //       .unattendMeetup(meetup.id, this.userService.getTravellerId())
  //       .subscribe(() => {
  //         meetup.isAttending = true;
  //       });
  //   }
  // }
}
