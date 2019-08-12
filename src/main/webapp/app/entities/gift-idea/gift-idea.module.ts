import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { SwinGiftsSharedModule } from 'app/shared';
import {
  GiftIdeaComponent,
  GiftIdeaDetailComponent,
  GiftIdeaUpdateComponent,
  GiftIdeaDeletePopupComponent,
  GiftIdeaDeleteDialogComponent,
  giftIdeaRoute,
  giftIdeaPopupRoute
} from './';

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
  entryComponents: [GiftIdeaComponent, GiftIdeaUpdateComponent, GiftIdeaDeleteDialogComponent, GiftIdeaDeletePopupComponent],
  providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SwinGiftsGiftIdeaModule {
  constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
    this.languageHelper.language.subscribe((languageKey: string) => {
      if (languageKey) {
        this.languageService.changeLanguage(languageKey);
      }
    });
  }
}
