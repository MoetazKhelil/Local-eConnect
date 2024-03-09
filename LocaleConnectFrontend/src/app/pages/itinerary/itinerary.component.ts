import {
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { Itinerary, Tag } from '../../model/itinerary';
import { ItineraryService } from '../../service/itinerary.service';
import { ItineraryDialogComponent } from './itinerary-dialog/itinerary-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { debounceTime, distinctUntilChanged, Subscription } from 'rxjs';
import { UserService } from '../../service/user.service';
import { ReviewService } from '../../service/review.service';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ImagesService } from '../../service/image.service';
import { NotificationService } from '../../service/notification.service';
import { GuideProfile } from '../../model/guide';
import { MatPaginator } from '@angular/material/paginator';
import { ApiResponse } from 'src/app/model/apiResponse';
import { User } from 'src/app/model/user';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-itinerary',
  templateUrl: './itinerary.component.html',
  styleUrls: ['./itinerary.component.scss'],
})
export class ItineraryComponent implements OnInit, OnDestroy {
  allItineraries: Itinerary[] = [];
  itineraries: Itinerary[] = [];
  filterItineraries: Itinerary[] = [];
  images: string[] = [];
  subscription: Subscription = new Subscription();
  searchControl = new FormControl('');
  filterForm: FormGroup;
  tagOptions: Tag[] = Object.values(Tag).filter((key) =>
    isNaN(Number(key))
  ) as Tag[];
  totalLength = 0;
  displayedItineraries: Itinerary[] = [];
  pageSize = 10;
  showAllImages = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private notificationService: NotificationService,
    private imageService: ImagesService,
    private userService: UserService,
    private itineraryService: ItineraryService,
    private reviewService: ReviewService,
    public dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private formBuilder: FormBuilder
  ) {
    this.filterForm = this.formBuilder.group({
      place: [''],
      days: [''],
      tag: [''],
    });
  }

  ngOnInit(): void {
    this.initializeDisplayedItineraries();
    this.itineraryService.getItineraries().subscribe({
      next: (itineraryRes: ApiResponse) => {
        this.allItineraries = itineraryRes.data as Itinerary[];
        this.itineraries = [...this.allItineraries];
        this.totalLength = this.allItineraries.length;
        this.initializeDisplayedItineraries();
        this.filterItineraries = [...this.allItineraries];
        this.allItineraries.forEach((itinerary) => {
          // itinerary.mappedTags = this.itineraryService.mapTags(itinerary.tags);
          if (itinerary.imageUrls.length > 0) {
            if (itinerary.imageUrls[0].length > 0) {
              this.imageService.getImage(itinerary.imageUrls[0]).subscribe({
                next: (gcpRes: ApiResponse) => {
                  itinerary.imageUrls = [];
                  itinerary.imageUrls.push(gcpRes.data.toString());
                },
                error: (errorMessage: ApiResponse) =>
                  console.error(errorMessage.errors),
              });
            }
          }

          this.userService.getUserById(itinerary.userId).subscribe({
            next: (res: ApiResponse) => {
              const user = res.data as User;
              itinerary.userName = user.userName;
            },
          });
        });
      },
      error: (errorMessage: ApiResponse) => console.error(errorMessage.errors),
    });

    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((searchTerm) => {
        this.performSearch(searchTerm);
      });
  }
  checkUserItinerariesBeforeDeletion(itineraryId: number): void {
    const travellerId = this.userService.getTravellerId();
    const itinerary = this.itineraries.find(
      (itinerary) => itinerary.id === itineraryId
    );
    if (itinerary?.userId === travellerId) {
      this.deleteItinerary(itineraryId);
    } else {
      this.notificationService.showError(
        'Itinerary was created by another traveller!'
      );
    }
  }

  deleteItinerary(id: number): void {
    const confirmDelete = confirm(
      'Are you sure you want to delete this itinerary?'
    );
    if (!confirmDelete) return;
    this.itineraryService.deleteItinerary(id).subscribe({
      next: () => {
        this.notificationService.showSuccess('Itinerary deleted successfully!');
        this.itineraries = this.itineraries.filter(
          (itinerary) => itinerary.id !== id
        );
        this.filterItineraries = [...this.itineraries];
        this.allItineraries = [...this.itineraries];
        this.totalLength = this.itineraries.length;
        this.updateDisplayedItineraries();
      },
      error: (error) => {
        console.error('Error deleting itinerary', error);
        this.notificationService.showError('Failed to delete itinerary.');
      },
    });
  }

  performSearch(searchTerm: string | null = ''): void {
    this.displayedItineraries = searchTerm
      ? this.itineraryService.searchItineraries(searchTerm, this.itineraries)
      : [...this.itineraries];
  }

  performFilter(): void {
    const filterValues = this.filterForm.value;
    const place = filterValues.place || null;
    const tag = filterValues.tag || null;
    const days = filterValues.days ? parseInt(filterValues.days, 10) : null;

    this.allItineraries = this.itineraryService.filterItineraries(
      this.itineraries,
      place,
      tag,
      days
    );
  }

  // addItinerary(newItinerary: Itinerary): void {

  //   this.itineraryService.addItinerary(newItinerary).subscribe({
  //     next: (res: ApiResponse) => {

  //       // this.allItineraries.push(itinerary);
  //     },
  //     error: (e: any) => {
  //       console.error('Error adding itinerary', e);
  //     },
  //   });
  // }

  toggleDetails(itinerary: Itinerary): void {
    itinerary.expand = !itinerary.expand;
  }

  openAddItineraryDialog(): void {
    const dialogRef = this.dialog.open(ItineraryDialogComponent, {
      width: '600px',
      height: '600px',
    });

    dialogRef.afterClosed().subscribe((newItinerary: Itinerary) => {
      this.allItineraries.push(newItinerary);
      this.itineraries = [...this.allItineraries];
      this.filterItineraries = [...this.allItineraries];
      this.totalLength = this.allItineraries.length;
      this.initializeDisplayedItineraries();
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  ngAfterViewInit() {
    this.paginator.page.subscribe(() => {
      this.updateDisplayedItineraries();
    });
  }

  initializeDisplayedItineraries(): void {
    this.displayedItineraries = this.itineraries.slice(0, this.pageSize);
  }

  updateDisplayedItineraries(): void {
    const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
    const endIndex = startIndex + this.paginator.pageSize;
    this.displayedItineraries = this.allItineraries.slice(startIndex, endIndex);
  }

  submitRating(itinerary: Itinerary, rating: number): void {
    if (itinerary.rating !== 0) {
      itinerary.ratingSubmitted = true;

      const userId = this.userService.getTravellerId();
      this.itineraryService
        .rateItinerary(itinerary.id, userId, rating)
        .subscribe({
          next: (res: ApiResponse) => {
            const updatedItinerary: Itinerary = res.data as Itinerary;
            itinerary.averageRating = updatedItinerary.averageRating;
            itinerary.ratingsCount = updatedItinerary.ratingsCount;
          },
          error: (error: HttpErrorResponse) => {
            this.notificationService.showError(error.error.errors.errors[0]);
          },
        });
      this.notificationService.showSuccess(
        'You submitted the rate successfully!'
      );
      this.initializeDisplayedItineraries();
    }
  }
  toggleImagesDisplay() {
    this.showAllImages = !this.showAllImages;
  }
}
