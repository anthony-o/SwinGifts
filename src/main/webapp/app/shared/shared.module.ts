import { NgModule } from '@angular/core';
import { SwinGiftsSharedLibsModule } from './shared-libs.module';
import { FindLanguageFromKeyPipe } from './language/find-language-from-key.pipe';
import { SwgAlertComponent } from './alert/alert.component';
import { SwgAlertErrorComponent } from './alert/alert-error.component';
import { SwgLoginModalComponent } from './login/login.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { ShorterPipe } from './util/shorter.pipe';

@NgModule({
  imports: [SwinGiftsSharedLibsModule],
  declarations: [
    FindLanguageFromKeyPipe,
    SwgAlertComponent,
    SwgAlertErrorComponent,
    SwgLoginModalComponent,
    HasAnyAuthorityDirective,
    ShorterPipe
  ],
  entryComponents: [SwgLoginModalComponent],
  exports: [
    SwinGiftsSharedLibsModule,
    FindLanguageFromKeyPipe,
    SwgAlertComponent,
    SwgAlertErrorComponent,
    SwgLoginModalComponent,
    HasAnyAuthorityDirective,
    ShorterPipe
  ]
})
export class SwinGiftsSharedModule {}
