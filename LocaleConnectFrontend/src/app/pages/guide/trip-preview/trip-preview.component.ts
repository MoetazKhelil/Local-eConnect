import {Component, Input} from '@angular/core';
import {TripPreview} from "../../../model/trip";

@Component({
  selector: 'app-trip-preview',
  templateUrl: './trip-preview.component.html',
  styleUrls: ['./trip-preview.component.scss']
})

export class TripPreviewComponent {
  @Input() trip!: TripPreview;

  //TODO: link to trip page
  navigateToTrip(link: string): void {
    window.location.href = link;
  }
}
