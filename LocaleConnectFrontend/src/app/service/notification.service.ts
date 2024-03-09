import {EventEmitter, Injectable, Output} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {Notification} from '../model/notification';
import {MatSnackBar} from '@angular/material/snack-bar';
import * as Stomp from 'stompjs';
import {AuthService} from './auth.service';
import {User} from 'src/app/model/user';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {ApiResponse} from '../model/apiResponse';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private notificationSource = new BehaviorSubject<Notification[]>([]);
  currentNotifications = this.notificationSource.asObservable();

  private webSocketEndPoint: string = `${environment.API_URL}/ws`;

  private apiUrl = `${environment.API_URL}/api/notification`;
  public stompClient: any;

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar,
    private authService: AuthService
  ) {
    this.initializeWebSocketConnection();
    this.getMissedNotifications();
  }

  initializeWebSocketConnection() {

    this.authService.fetchCurrentUserProfile().subscribe({
      next: (user: User) => {
        const userId = user.id;

        const serverUrl = this.webSocketEndPoint;
        // const ws = new SockJS(serverUrl);
        const ws = new WebSocket('ws://localhost:8080/ws');

        const token = this.authService.getTokenFromLocalStorage();
        const headers = {
          Authorization: `Bearer ${token}`,
          userId: userId,
        };
        this.stompClient = Stomp.over(ws);
        const that = this;
        // tslint:disable-next-line:only-arrow-functions
        this.stompClient.connect(headers, function (frame: Stomp.Frame) {
          // that.stompClient.subscribe(
          //   '/topic/notification',
          //   (message: Stomp.Message) => {
          //   }
          // );
          that.stompClient.subscribe(
            `/user/${userId}/msg`,
            (response: Stomp.Message) => {
              const notification = JSON.parse(response.body);
              that.notificationSource.next([notification]);
            }
          );
        });
      },
    });
  }

  disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
  }

  // sendMessage(notification: Notification) {
  //   this.stompClient.send('/app/notify', {}, JSON.stringify(notification));
  // }

  getMissedNotifications(): void {
    this.authService.fetchCurrentUserProfile().subscribe({
      next: (user: User) => {
        const userId = user.id;
        return this.http
          .get<ApiResponse>(`${this.apiUrl}/notify/${userId}`)
          .subscribe({
            next: (res: ApiResponse) => {
              const notifications: Notification[] = res.data as Notification[];
              this.notificationSource.next(notifications);
            },
            error: (err) => {
              return [];
            },
          });
      },
    });
  }

  //TODO: replace with api call
  // addNotification(notification: Notification) {
  //   const currentNotifications = this.notificationsMock.value;
  //   this.notificationsMock.next([...currentNotifications, notification]);
  // }
  //TODO: replace with api call
  // deleteAllNotifications() {
  //   this.notificationsMock.next([]);
  // }

  showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      panelClass: ['success-snackbar'],
    });
  }

  showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      panelClass: ['error-snackbar'],
    });
  }
}
