import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';

@Component({
  selector: 'swg-drawing-exclusion-group-detail',
  templateUrl: './drawing-exclusion-group-detail.component.html'
})
export class DrawingExclusionGroupDetailComponent implements OnInit {
  drawingExclusionGroup: IDrawingExclusionGroup;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ drawingExclusionGroup }) => {
      this.drawingExclusionGroup = drawingExclusionGroup;
    });
  }

  previousState() {
    window.history.back();
  }
}
