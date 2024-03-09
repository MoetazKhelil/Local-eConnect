// trip.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Trip } from '../model/trip';
import { Itinerary } from '../model/itinerary';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../model/apiResponse';

@Injectable({
  providedIn: 'root',
})
export class TripService {
  private apiUrl = `${environment.API_URL}/api/trip`;
  private tripSource = new BehaviorSubject<Trip | null>(null);
  currentTrip = this.tripSource.asObservable();

  constructor(private http: HttpClient) {}
  changeTrip(trip: any) {
    if (trip) {
      this.tripSource.next(trip);
    }
  }

  createTrip(trip: Trip): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.apiUrl}/create`, trip);
  }

  getAllTrips(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.apiUrl}/all`);
  }

  getTripById(tripId: number): Observable<Trip> {
    return this.http.get<Trip>(`${this.apiUrl}/${tripId}`);
  }

  updateTrip(tripId: number, trip: Trip): Observable<Trip> {
    return this.http.put<Trip>(`${this.apiUrl}/update/${tripId}`, trip);
  }

  deleteTrip(tripId: number): Observable<{}> {
    return this.http.delete(`${this.apiUrl}/delete/${tripId}`);
  }

  joinTrip(id: number, travellerId: number): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.apiUrl}/${id}/attend`, {
      travellerId,
    });
  }

  leaveTrip(id: number, travellerId: number): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.apiUrl}/${id}/unattend`, {
      travellerId,
    });
  }

  searchTrip(tripName: string): Observable<Trip[]> {
    return this.http.get<Trip[]>(`${this.apiUrl}/search?name=${tripName}`);
  }
  getLocalguideTrips(localguideId: number): Observable<Trip[]> {
    return this.http.get<Trip[]>(
      `${this.apiUrl}/allByLocalguide/${localguideId}`
    );
  }

  rateItinerary(tripId: number, userId: number, rating: number) {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/${tripId}/rate/${userId}?rating=${rating}`,
      {}
    );
  }
}
