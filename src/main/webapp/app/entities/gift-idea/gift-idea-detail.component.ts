import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGiftIdea } from 'app/shared/model/gift-idea.model';

@Component({
  selector: 'swg-gift-idea-detail',
  templateUrl: './gift-idea-detail.component.html'
})
export class GiftIdeaDetailComponent implements OnInit {
  giftIdea: IGiftIdea;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ giftIdea }) => {
      this.giftIdea = giftIdea;
    });
  }

  previousState() {
    window.history.back();
  }
}
