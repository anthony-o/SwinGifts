import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SwinGiftsSharedModule } from 'app/shared/shared.module';
import { GiftIdeaComponent } from './gift-idea.component';
import { GiftIdeaDetailComponent } from './gift-idea-detail.component';
import { GiftIdeaUpdateComponent } from './gift-idea-update.component';
import { GiftIdeaDeletePopupComponent, GiftIdeaDeleteDialogComponent } from './gift-idea-delete-dialog.component';
import { giftIdeaRoute, giftIdeaPopupRoute } from './gift-idea.route';

const ENTITY_STATES = [...giftIdeaRoute, ...giftIdeaPopupRoute];

@NgModule({
  imports: [SwinGiftsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    GiftIdeaComponent,
    GiftIdeaDetailComponent,
    GiftIdeaUpdateComponent,
    GiftIdeaDeleteDialogComponent,
    GiftIdeaDeletePopupComponent
  ],
  entryComponents: [GiftIdeaDeleteDialogComponent]
})
export class SwinGiftsGiftIdeaModule {}
