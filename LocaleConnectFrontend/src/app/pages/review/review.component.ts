import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Review } from '../../model/review';
import { ReviewService } from '../../service/review.service';

@Component({
  selector: 'app-review',
  templateUrl: './review.component.html',
  styleUrls: ['./review.component.scss'],
})
export class ReviewComponent implements OnInit {
  @Input() itineraryId: number = 0;
  reviews: Review[] = [];
  @Input() rating: number = 0;
  @Input() readOnly: boolean = false;
  @Output() ratingChange = new EventEmitter<number>();

  constructor(private reviewService: ReviewService) {}

  ngOnInit(): void {}
  onSelect(rating: number): void {
    if (!this.readOnly) {
      this.rating = rating;
      this.ratingChange.emit(this.rating);
    }
  }

  array(num: number): any[] {
    return Array(num);
  }
  loadReviews(): void {
    this.reviewService
      .getAllReviews(this.itineraryId)
      .subscribe((reviews: Review[]) => (this.reviews = reviews));
  }
  getStars(rating: number = 0) {
    const fullStars = Math.floor(rating);
    const halfStar = rating % 1 !== 0 ? 1 : 0;
    const emptyStars = Math.max(5 - fullStars - halfStar, 0);

    return {
      fullStars: Array(fullStars).fill('star'),
      halfStar: Array(halfStar).fill('star_half'),
      emptyStars: Array(emptyStars).fill('star_border'),
    };
  }

  protected readonly onsubmit = onsubmit;
}
