import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import {
  BehaviorSubject,
  catchError,
  Observable,
  of,
  tap,
  throwError,
} from 'rxjs';
import { User, UserProfile } from '../model/user';
import { Traveler } from '../model/traveler';
import { Guide } from '../model/guide';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../model/apiResponse';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${environment.API_URL}/api/user/auth`;
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User | null>(
      this.getUserFromLocalStorage()
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  getCurrentUserProfile(): UserProfile | null {
    const user = this.getUserFromLocalStorage();
    return user
      ? {
          id: user.id,
          name: `${user.firstName} ${user.lastName}`,
          userName: user.userName,
          bio: user.bio,
          profilePicture: user.profilePicture,
        }
      : null;
  }

  login(email: string, password: string): Observable<void> {
    return this.http
      .post<ApiResponse>(`${this.apiUrl}/login`, { email, password })
      .pipe(
        tap((response) => {
          this.setSession(response.data as string);
        }),
        catchError(this.handleError<any>('login'))
      );
  }

  fetchCurrentUserProfile(): Observable<User> {
    // const storedUserProfile = localStorage.getItem('currentUser');
    // if (storedUserProfile) {
    //   const userProfile: User = JSON.parse(storedUserProfile);
    //   return of(userProfile);
    // } else {
    const httpHeaders = this.getHttpHeaders();
    return this.http
      .get<User>(`${environment.API_URL}/api/user/secured/profile`, {
        headers: httpHeaders,
      })
      .pipe(
        tap((userProfile) => {
          localStorage.setItem('currentUser', JSON.stringify(userProfile));
        }),
        catchError((error) => {
          console.error('Error fetching user profile:', error);
          return throwError(error);
        })
      );
    // }
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('expires_at');
    this.currentUserSubject.next(null);
  }

  public isLoggedIn(): boolean {
    return (
      new Date().getTime() < parseInt(localStorage.getItem('expires_at') || '0')
    );
  }

  public isLoggedOut(): boolean {
    return !this.isLoggedIn();
  }

  registerTraveler(traveler: Traveler): Observable<Traveler> {
    return this.http
      .post<Traveler>(`${this.apiUrl}/register-traveler`, traveler)
      .pipe(catchError(this.handleError<Traveler>('register traveler')));
  }

  registerGuide(guide: Guide): Observable<Guide> {
    return this.http
      .post<Guide>(`${this.apiUrl}/register-localguide`, guide)
      .pipe(catchError(this.handleError<Guide>('register guide')));
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('token');
    return !!token;
  }

  public getUserIdFromLocalStorage(): number | undefined {
    return this.getUserFromLocalStorage()
      ? this.getUserFromLocalStorage()?.id
      : undefined;
  }
  public getuserNameFromLocalStorage(): string | undefined {
    return this.getUserFromLocalStorage()
      ? this.getUserFromLocalStorage()?.userName
      : undefined;
  }
  public getUserFromLocalStorage(): User | null {
    const storedUser = localStorage.getItem('currentUser');
    return storedUser ? JSON.parse(storedUser) : null;
  }

  private setSession(token: string): void {
    const expiresAt = JSON.stringify(1000 * 60 * 30 + new Date().getTime());
    localStorage.setItem('token', token);
    localStorage.setItem('expires_at', expiresAt);
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: HttpErrorResponse): Observable<T> => {
      let errorMessage = '';

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = error.error.message;
      } else {
        // Backend error
        if (error.status === 400) {
          if (error.error.includes('userName already exists')) {
            errorMessage =
              'This userName is already taken. Please try a different one.';
          } else if (error.error.includes('email already exists')) {
            errorMessage =
              'This email is already in use. Please try a different one.';
          } else if (error.error.includes('userName does not exist')) {
            errorMessage = 'This userName doesnt exist.';
          } else if (error.error.includes('wrong password')) {
            errorMessage = 'This password is wrong.';
          } else {
            errorMessage = 'Validation error. Please check your input.';
          }
        } else if (error.status >= 500) {
          errorMessage = 'Server error. Please try again later.';
        } else {
          errorMessage = 'An unexpected error occurred. Please try again.';
        }
      }
      return throwError(errorMessage);
    };
  }

  public getTokenFromLocalStorage(): string | null {
    return localStorage.getItem('token');
  }

  public getHttpHeaders(): HttpHeaders {
    const token = this.getTokenFromLocalStorage();
    const httpOptions = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
    return httpOptions;
  }
}
