import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { SwinGiftsSharedModule } from 'app/shared';
import {
  ParticipationComponent,
  ParticipationDetailComponent,
  ParticipationUpdateComponent,
  ParticipationDeletePopupComponent,
  ParticipationDeleteDialogComponent,
  participationRoute,
  participationPopupRoute
} from './';

const ENTITY_STATES = [...participationRoute, ...participationPopupRoute];

@NgModule({
  imports: [SwinGiftsSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    ParticipationComponent,
    ParticipationDetailComponent,
    ParticipationUpdateComponent,
    ParticipationDeleteDialogComponent,
    ParticipationDeletePopupComponent
  ],
  entryComponents: [
    ParticipationComponent,
    ParticipationUpdateComponent,
    ParticipationDeleteDialogComponent,
    ParticipationDeletePopupComponent
  ],
  providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SwinGiftsParticipationModule {
  constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
    this.languageHelper.language.subscribe((languageKey: string) => {
      if (languageKey) {
        this.languageService.changeLanguage(languageKey);
      }
    });
  }
}
