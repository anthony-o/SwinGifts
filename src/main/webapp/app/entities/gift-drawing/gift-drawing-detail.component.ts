import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGiftDrawing } from 'app/shared/model/gift-drawing.model';

@Component({
  selector: 'swg-gift-drawing-detail',
  templateUrl: './gift-drawing-detail.component.html'
})
export class GiftDrawingDetailComponent implements OnInit {
  giftDrawing: IGiftDrawing;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ giftDrawing }) => {
      this.giftDrawing = giftDrawing;
    });
  }

  previousState() {
    window.history.back();
  }
}
