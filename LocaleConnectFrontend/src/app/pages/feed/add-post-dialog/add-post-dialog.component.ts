import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ImagesService } from '../../../service/image.service';
import { FeedService } from '../../../service/feed.service';
import { Post } from '../../../model/feed';
import { PostType } from '../../../model/post-type';
import { AuthService } from '../../../service/auth.service';
import { getFormattedDateAndTime } from '../../../helper/DateHelper';

@Component({
  selector: 'app-add-post-dialog',
  templateUrl: './add-post-dialog.component.html',
  styleUrls: ['./add-post-dialog.component.scss'],
})
export class AddPostDialogComponent {
  postForm: FormGroup;
  images: string[] = [];
  showEmojiPicker = false;

  constructor(
    private cdr: ChangeDetectorRef,
    private feedService: FeedService,
    public dialogRef: MatDialogRef<AddPostDialogComponent>,
    private formBuilder: FormBuilder,
    private imageService: ImagesService,
    private authService: AuthService
  ) {
    this.postForm = this.formBuilder.group({
      content: ['', Validators.required],
      images: [''],
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    const currentDate = getFormattedDateAndTime(new Date());

    if (this.postForm.valid) {
      const postData: Post = {
        authorID: this.authService.getUserIdFromLocalStorage(),
        content: this.postForm.value.content,
        images: this.images,
        postType: PostType.REGULAR,
        date: currentDate,
        likedByUser: false,
      };

      this.feedService.createRegularPost(postData).subscribe({
        next: (newPost) => {
          this.dialogRef.close(newPost);
        },
        error: (error) => {
          console.error('Failed to create post:', error);
        },
      });
    }
  }

  toggleEmojiPicker() {
    this.showEmojiPicker = !this.showEmojiPicker;
  }

  addEmoji(event: any) {
    const emoji = event.emoji.native;
    const currentContent: string = this.postForm.value.content;
    this.postForm.controls['content'].setValue(currentContent + emoji);
    this.cdr.detectChanges();
  }

  //TODO: add gcp
  onFileSelected(event: any) {
    const files = event.target.files;
    if (files) {
      Array.from(files).forEach((file) => {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          this.images.push(e.target.result);
          if (this.images.length === files.length) {
            this.imageService.updateImages(this.images);
          }
        };
        reader.readAsDataURL(<Blob>file);
      });
    }
  }
}
