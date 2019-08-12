import { NgModule } from '@angular/core';

import { SwinGiftsSharedLibsModule, FindLanguageFromKeyPipe, SwgAlertComponent, SwgAlertErrorComponent } from './';

@NgModule({
  imports: [SwinGiftsSharedLibsModule],
  declarations: [FindLanguageFromKeyPipe, SwgAlertComponent, SwgAlertErrorComponent],
  exports: [SwinGiftsSharedLibsModule, FindLanguageFromKeyPipe, SwgAlertComponent, SwgAlertErrorComponent]
})
export class SwinGiftsSharedCommonModule {}
