import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";
import {Review} from "../model/review";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private reviews: Review[] = [];
  private reviewData = new BehaviorSubject<Review[]>(this.reviews);

  constructor(private http: HttpClient) {}

  createReview(review: Review): Observable<Review> {
    this.reviews.push(review);
    return this.http.post<Review>('/api/create-review', review);
  }

  updateReview(id: number, review: Review): Observable<Review> {
    return this.http.put<Review>(`/api/update-review/${id}`, review);
  }

  deleteReview(id: number): Observable<void> {
    return this.http.delete<void>(`/api/delete-review/${id}`);
  }

  getAllReviews(itineraryId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`/api/all-reviews/${itineraryId}`);
  }
  getRatingsForEntity(entityId: number, entityType: 'itinerary' | 'meetup'|'trip'|'guide'): { averageRating: number, totalRatings: number } {
    const filteredReviews = this.reviews.filter(review => review.entityId === entityId && review.entityType === entityType);
    const totalRatings = filteredReviews.length;
    const averageRating = filteredReviews.reduce((acc, curr) => acc + curr.rating, 0) / totalRatings || 0;

    return { averageRating, totalRatings };
  }

  getReviewData() {
    return this.reviewData.asObservable();
  }

}



