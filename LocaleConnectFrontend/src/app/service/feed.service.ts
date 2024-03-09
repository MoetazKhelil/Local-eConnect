import { Injectable } from '@angular/core';
import { Comment, Post } from '../model/feed';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../model/apiResponse';
import { AuthService } from './auth.service';

//todo: NOTIFICATIONS
@Injectable({
  providedIn: 'root',
})
export class FeedService {
  private apiUrl = `${environment.API_URL}/api/feed`;
  private postSource = new BehaviorSubject<Post | null>(null);
  currentPost = this.postSource.asObservable();
  httpHeaders: HttpHeaders;

  constructor(private http: HttpClient, private authService: AuthService) {
    this.httpHeaders = authService.getHttpHeaders();
  }

  createRegularPost(regularPost: Post): Observable<Post> {
    return this.http
      .post<ApiResponse>(`${this.apiUrl}/create`, regularPost, {
        headers: this.httpHeaders,
      })
      .pipe(map((response) => response.data as Post));
  }

  changePost(post: any) {
    if (post) {
      this.postSource.next(post);
    }
  }

  deletePostById(postId: number): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(`${this.apiUrl}/delete/${postId}`, {
      headers: this.httpHeaders,
    });
  }

  addComment(postId: number, comment: Comment): Observable<Comment> {
    return this.http
      .post<ApiResponse>(`${this.apiUrl}/${postId}/comment`, comment, {
        headers: this.httpHeaders,
      })
      .pipe(map((response) => response.data as Comment));
  }

  deleteComment(postId: number, commentId: number): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(
      `${this.apiUrl}/${postId}/comment/${commentId}`
    );
  }

  getPostById(postId: number): Observable<Post> {
    return this.http
      .get<ApiResponse>(`${this.apiUrl}/${postId}`, {
        headers: this.httpHeaders,
      })
      .pipe(map((response) => response.data as Post));
  }

  getUserFeed(userId: number): Observable<Post[]> {
    return this.http
      .get<ApiResponse>(`${this.apiUrl}/home/${userId}`, {
        headers: this.httpHeaders,
      })
      .pipe(map((response) => response.data as Post[] | []));
  }

  getPostLikeCount(postId: number): Observable<number> {
    return this.http
      .get<ApiResponse>(`${this.apiUrl}/${postId}/like-count`, {
        headers: this.httpHeaders,
      })
      .pipe(map((response) => response.data as number | 0));
  }

  likePost(postId: number, likerId: number): Observable<Post> {
    const likeDTO = { likerId };
    return this.http
      .post<ApiResponse>(`${this.apiUrl}/${postId}/like`, likeDTO, {
        headers: this.httpHeaders,
      })
      .pipe(map((response) => response.data as Post));
  }

  unlikePost(postId: number, likerId: number): Observable<Post> {
    const likeDTO = { likerId };
    return this.http
      .post<ApiResponse>(`${this.apiUrl}/${postId}/unlike`, likeDTO, {
        headers: this.httpHeaders,
      })
      .pipe(map((response) => response.data as Post));
  }

  getPostLikes(postId: number): Observable<string[]> {
    return this.http
      .get<ApiResponse>(`${this.apiUrl}/${postId}/like-list`, {
        headers: this.httpHeaders,
      })
      .pipe(map((response) => response.data as string[]));
  }
}
