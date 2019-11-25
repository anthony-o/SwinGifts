import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SwinGiftsSharedModule } from 'app/shared/shared.module';
import { EventComponent } from './event.component';
import { EventDetailComponent } from './event-detail.component';
import { EventUpdateComponent } from './event-update.component';
import { EventDeletePopupComponent, EventDeleteDialogComponent } from './event-delete-dialog.component';
import { eventRoute, eventPopupRoute } from './event.route';
import { EventDetailPublicComponent } from './event-detail-public.component';
import { EventSelectParticipationDialogComponent } from './event-select-participation-dialog.component';

const ENTITY_STATES = [...eventRoute, ...eventPopupRoute];

@NgModule({
  imports: [SwinGiftsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    EventComponent,
    EventDetailComponent,
    EventUpdateComponent,
    EventDeleteDialogComponent,
    EventDeletePopupComponent,
    EventDetailPublicComponent,
    EventSelectParticipationDialogComponent
  ],
  entryComponents: [EventDeleteDialogComponent, EventSelectParticipationDialogComponent]
})
export class SwinGiftsEventModule {}
