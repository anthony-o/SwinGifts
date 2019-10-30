import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SwinGiftsSharedModule } from 'app/shared/shared.module';
import { DrawingExclusionGroupComponent } from './drawing-exclusion-group.component';
import { DrawingExclusionGroupDetailComponent } from './drawing-exclusion-group-detail.component';
import { DrawingExclusionGroupUpdateComponent } from './drawing-exclusion-group-update.component';
import {
  DrawingExclusionGroupDeletePopupComponent,
  DrawingExclusionGroupDeleteDialogComponent
} from './drawing-exclusion-group-delete-dialog.component';
import { drawingExclusionGroupRoute, drawingExclusionGroupPopupRoute } from './drawing-exclusion-group.route';

const ENTITY_STATES = [...drawingExclusionGroupRoute, ...drawingExclusionGroupPopupRoute];

@NgModule({
  imports: [SwinGiftsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    DrawingExclusionGroupComponent,
    DrawingExclusionGroupDetailComponent,
    DrawingExclusionGroupUpdateComponent,
    DrawingExclusionGroupDeleteDialogComponent,
    DrawingExclusionGroupDeletePopupComponent
  ],
  entryComponents: [DrawingExclusionGroupDeleteDialogComponent]
})
export class SwinGiftsDrawingExclusionGroupModule {}
