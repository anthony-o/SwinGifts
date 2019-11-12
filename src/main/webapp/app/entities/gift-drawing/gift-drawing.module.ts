import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SwinGiftsSharedModule } from 'app/shared/shared.module';
import { GiftDrawingComponent } from './gift-drawing.component';
import { GiftDrawingDetailComponent } from './gift-drawing-detail.component';
import { GiftDrawingUpdateComponent } from './gift-drawing-update.component';
import { GiftDrawingDeletePopupComponent, GiftDrawingDeleteDialogComponent } from './gift-drawing-delete-dialog.component';
import { giftDrawingRoute, giftDrawingPopupRoute } from './gift-drawing.route';

const ENTITY_STATES = [...giftDrawingRoute, ...giftDrawingPopupRoute];

@NgModule({
  imports: [SwinGiftsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    GiftDrawingComponent,
    GiftDrawingDetailComponent,
    GiftDrawingUpdateComponent,
    GiftDrawingDeleteDialogComponent,
    GiftDrawingDeletePopupComponent
  ],
  entryComponents: [GiftDrawingDeleteDialogComponent]
})
export class SwinGiftsGiftDrawingModule {}
