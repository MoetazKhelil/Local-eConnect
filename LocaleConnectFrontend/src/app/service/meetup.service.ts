import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Meetup } from '../model/meetup';
import { BehaviorSubject, Observable } from 'rxjs';
import { ApiResponse } from '../model/apiResponse';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class MeetupService {
  private apiUrl = `${environment.API_URL}/api/meetup`;

  private meetupSource = new BehaviorSubject<Meetup | null>(null);
  currentMeetup = this.meetupSource.asObservable();

  constructor(private http: HttpClient, private authService: AuthService) {}

  changeMeetup(meetup: any) {
    if (meetup) {
      this.meetupSource.next(meetup);
    }
  }

  getAllMeetups() {
    const httpHeaders = this.authService.getHttpHeaders();

    return this.http.get<ApiResponse>(`${this.apiUrl}/all`, {
      headers: httpHeaders,
    });
  }
  getCreatorMeetups(id: number) {
    const httpHeaders = this.authService.getHttpHeaders();

    return this.http.get<ApiResponse>(`${this.apiUrl}/allByCreator/${id}`, {
      headers: httpHeaders,
    });
  }
  getMeetupById(id: number) {
    const httpHeaders = this.authService.getHttpHeaders();
    return this.http.get<Meetup>(`${this.apiUrl}/${id}`, {
      headers: httpHeaders,
    });
  }

  createMeetup(meetup: Meetup) {
    return this.http.post<ApiResponse>(`${this.apiUrl}/create`, meetup);
  }

  updateMeetup(id: number, meetup: Meetup) {
    return this.http.put<ApiResponse>(`${this.apiUrl}/update/${id}`, meetup);
  }

  attendMeetup(id: number, travellerId: number) {
    return this.http.post<ApiResponse>(`${this.apiUrl}/${id}/attend`, {
      travellerId,
    });
  }

  unattendMeetup(id: number, travellerId: number) {
    return this.http.post<ApiResponse>(`${this.apiUrl}/${id}/unattend`, {
      travellerId,
    });
  }

  deleteMeetup(id: number) {
    return this.http.delete<ApiResponse>(`${this.apiUrl}/delete/${id}`);
  }

  rateMeetup(meetupId: number, userId: number, rating: number) {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/${meetupId}/rate/${userId}?rating=${rating}`,
      {}
    );
  }

  searchMeetups(searchTerm: string, searchMeetups: Meetup[]): Meetup[] {
    if (!searchTerm) {
      return [...searchMeetups];
    }
    return searchMeetups.filter((meetup) =>
      meetup.name.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }
}
