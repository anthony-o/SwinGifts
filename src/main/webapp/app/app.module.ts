import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { SwinGiftsSharedModule } from 'app/shared/shared.module';
import { SwinGiftsCoreModule } from 'app/core/core.module';
import { SwinGiftsAppRoutingModule } from './app-routing.module';
import { SwinGiftsHomeModule } from './home/home.module';
import { SwinGiftsEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { SwgMainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    SwinGiftsSharedModule,
    SwinGiftsCoreModule,
    SwinGiftsHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    SwinGiftsEntityModule,
    SwinGiftsAppRoutingModule
  ],
  declarations: [SwgMainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
  bootstrap: [SwgMainComponent]
})
export class SwinGiftsAppModule {}
