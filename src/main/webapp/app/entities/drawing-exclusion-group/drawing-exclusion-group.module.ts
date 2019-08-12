import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { SwinGiftsSharedModule } from 'app/shared';
import {
  DrawingExclusionGroupComponent,
  DrawingExclusionGroupDetailComponent,
  DrawingExclusionGroupUpdateComponent,
  DrawingExclusionGroupDeletePopupComponent,
  DrawingExclusionGroupDeleteDialogComponent,
  drawingExclusionGroupRoute,
  drawingExclusionGroupPopupRoute
} from './';

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
  entryComponents: [
    DrawingExclusionGroupComponent,
    DrawingExclusionGroupUpdateComponent,
    DrawingExclusionGroupDeleteDialogComponent,
    DrawingExclusionGroupDeletePopupComponent
  ],
  providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SwinGiftsDrawingExclusionGroupModule {
  constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
    this.languageHelper.language.subscribe((languageKey: string) => {
      if (languageKey) {
        this.languageService.changeLanguage(languageKey);
      }
    });
  }
}
