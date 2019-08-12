import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { SwinGiftsSharedCommonModule, SwgLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
  imports: [SwinGiftsSharedCommonModule],
  declarations: [SwgLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [SwgLoginModalComponent],
  exports: [SwinGiftsSharedCommonModule, SwgLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SwinGiftsSharedModule {
  static forRoot() {
    return {
      ngModule: SwinGiftsSharedModule
    };
  }
}
