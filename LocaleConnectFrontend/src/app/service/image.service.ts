import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ApiResponse } from '../model/apiResponse';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class ImagesService {
  private imagesSource = new BehaviorSubject<string[]>([]);
  currentImages = this.imagesSource.asObservable();
  private apiUrl = 'http://localhost:8080/api/gcp';

  constructor(private http: HttpClient, private authService: AuthService) {}

  updateImages(images: string[]) {
    this.imagesSource.next(images);
  }

  getImage(filename: string): Observable<ApiResponse> {
    const httpHeaders = this.authService.getHttpHeaders();
    return this.http.get<ApiResponse>(`${this.apiUrl}/?filename=${filename}`, {
      headers: httpHeaders,
    });
  }
}
