import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Itinerary, Tag } from '../model/itinerary';
import { ApiResponse } from '../model/apiResponse';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';
import { UserProfile } from '../model/user';

@Injectable({
  providedIn: 'root',
})
export class ItineraryService {
  // private itinerarySource = new BehaviorSubject<Itinerary | null>(null);
  // currentItinerary = this.itinerarySource.asObservable();
  currentUserId: number | null = null;
  private apiUrl = `${environment.API_URL}/api/itinerary`;

  constructor(private http: HttpClient, private authService: AuthService) {}

  // changeItinerary(itinerary: any) {
  //   if (itinerary) {
  //     this.itinerarySource.next(itinerary);
  //   }
  // }
  getItineraries(): Observable<ApiResponse> {
    const httpHeaders = this.authService.getHttpHeaders();
    return this.http.get<ApiResponse>(`${this.apiUrl}/all`,{
      headers: httpHeaders,
    });
  }

  addItinerary(itinerary: Itinerary): Observable<ApiResponse> {
    const httpHeaders = this.authService.getHttpHeaders();

    return this.http.post<ApiResponse>(`${this.apiUrl}/create`, itinerary, {
      headers: httpHeaders,
    });
  }

  deleteItinerary(id: number): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(`${this.apiUrl}/delete/${id}`);
  }

  getUserItineraries(userId: number): Observable<Itinerary[]> {
    return this.http.get<Itinerary[]>(`${this.apiUrl}/allByUser/${userId}`);
  }

  filterItineraries(
    searchItineraries: Itinerary[],
    place: string,
    tag: Tag | null,
    days: number | null
  ): Itinerary[] {
    return searchItineraries.filter((itinerary) => {
      const places = itinerary.placesToVisit.map((x) => x.toLowerCase());
      const matchesPlace = place ? places.includes(place.toLowerCase()) : true;
      const matchesTag = tag ? itinerary.tags.includes(tag) : true;
      const matchesDays = days ? itinerary.numberOfDays <= days : true;

      return matchesPlace && matchesTag && matchesDays;
    });
  }

  mapTags(tags: Tag[]): string[] {
    return tags.map((tag) => Tag[tag]);
  }

  searchItineraries(
    searchTerm: string,
    searchItineraries: Itinerary[]
  ): Itinerary[] {
    if (!searchTerm) {
      return [...searchItineraries];
    }
    return searchItineraries.filter((itinerary) =>
      itinerary.name.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }

  rateItinerary(itineraryId: number, userId: number, rating: number) {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/${itineraryId}/rate/${userId}?rating=${rating}`,
      {}
    );
  }
}
