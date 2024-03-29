import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ProfileService } from './profile.service';

@Component({
  selector: 'swg-page-ribbon',
  template: `
    <div class="ribbon" *ngIf="ribbonEnv$ | async as ribbonEnv">
      <a href="" jhiTranslate="global.ribbon.{{ ribbonEnv }}">{{ ribbonEnv }}</a>
    </div>
  `,
  styleUrls: ['page-ribbon.scss']
})
export class PageRibbonComponent implements OnInit {
  ribbonEnv$: Observable<string>;

  constructor(private profileService: ProfileService) {}

  ngOnInit() {
    this.ribbonEnv$ = this.profileService.getProfileInfo().pipe(map(profileInfo => profileInfo.ribbonEnv));
  }
}
