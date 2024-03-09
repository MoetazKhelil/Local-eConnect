import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map, Observable, throwError } from 'rxjs';
import { Guide, GuideProfile } from '../model/guide';
import { TripPreview } from '../model/trip';
import { User } from '../model/user';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';
import { ApiResponse } from '../model/apiResponse';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${environment.API_URL}/api/user/secured`;
  headers: HttpHeaders;

  constructor(private http: HttpClient, private authService: AuthService) {
    this.headers = this.authService.getHttpHeaders();
  }

  getuserName(userId: number): Observable<string> {
    return this.http.get<string>(`${this.apiUrl}/${userId}`);
  }

  searchGuides(
    searchTerm: string,
    searchGuides: GuideProfile[]
  ): GuideProfile[] {
    if (!searchTerm) {
      return [...searchGuides];
    }
    return searchGuides.filter((guide) =>
      guide.name.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/all`);
  }

  getUserById(userId: number): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.apiUrl}/${userId}`);
  }

  getCurrentUserId(): number {
    return this.authService.currentUserValue?.id
      ? this.authService.currentUserValue.id
      : 0;
  }

  updateUser(user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/update`, user);
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${userId}`);
  }

  followUser(userId: number, followerId: number): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/${userId}/follow/${followerId}`,
      {},
      { headers: this.headers }
    );
  }

  unfollowUser(userId: number, followeeId: number): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/${userId}/unfollow/${followeeId}`,
      {},
      { headers: this.headers }
    );
  }

  rateLocalGuide(
    guideId: number,
    travelerId: number,
    rating: number
  ): Observable<ApiResponse> {
    const params = new HttpParams().set('rating', rating.toString());
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/${guideId}/rate/${travelerId}`,
      {},
      { headers: this.headers, params: params }
    );
  }

  getAllGuides(): Observable<GuideProfile[]> {
    const httpHeaders = this.authService.getHttpHeaders();
    return this.http
      .get<ApiResponse>(`${this.apiUrl}/guides`, { headers: httpHeaders })
      .pipe(map((response) => response.data as GuideProfile[] | []));
  }

  filterLocalGuideByCity(keyword: string): Observable<User[]> {
    return this.http.get<User[]>(
      `${this.apiUrl}/filter-guides-city?keyword=${keyword}`,
      { headers: this.headers }
    );
  }

  searchTravelers(keyword: string): Observable<User[]> {
    return this.http.get<User[]>(
      `${this.apiUrl}/search-traveler?keyword=${keyword}`,
      { headers: this.headers }
    );
  }

  getFollowers(userId: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/${userId}/followers`);
  }

  getProfile(userId: number): Observable<User> {
    return this.http
      .get<ApiResponse>(`${this.apiUrl}/${userId}/profile`, {
        headers: this.headers,
      })
      .pipe(map((res) => res.data as User));
  }

  getAllFollowing(userId: number | undefined): Observable<User[]> {
    return this.http
      .get<ApiResponse>(`${this.apiUrl}/${userId}/following`, {
        headers: this.headers,
      })
      .pipe(map((res) => (res.data ? (res.data as User[]) : [])));
  }

  getAverageRatingOfLocalGuide(userId: number): Observable<number> {
    return this.http
      .get<ApiResponse>(`${this.apiUrl}/${userId}/rating`, {
        headers: this.headers,
      })
      .pipe(map((res) => (res.data ? (res.data as number) : 0)));
  }

  getRatingCountOfLocalGuide(userId: number): Observable<number> {
    return this.http
      .get<ApiResponse>(`${this.apiUrl}/${userId}/rating-count`, {
        headers: this.headers,
      })
      .pipe(map((res) => (res.data ? (res.data as number) : 0)));
  }

  public getTravellerId(): number {
    let traveller = this.authService.getUserFromLocalStorage();
    if (!traveller || !traveller.id) return -1;

    return traveller.id;
  }

  public uploadProfile(
    travellerId: number,
    image: String
  ): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/${travellerId}/upload-profile`,
      {
        profilePhoto: image,
      }
    );
  }
}
