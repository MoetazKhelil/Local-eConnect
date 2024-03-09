import {Component} from '@angular/core';
import { Router, NavigationEnd, Event as RouterEvent } from '@angular/router';
import {filter} from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'LocaleConnect';

  shouldShowDefaultHeader = true;

  constructor(private router: Router) {
    this.router.events.pipe(
      filter((event: RouterEvent): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      // Update the flag based on the current URL
      this.shouldShowDefaultHeader = !event.urlAfterRedirects.startsWith('/pages');
    });
  }
}
