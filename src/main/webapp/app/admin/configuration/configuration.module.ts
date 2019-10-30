import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SwinGiftsSharedModule } from 'app/shared/shared.module';

import { SwgConfigurationComponent } from './configuration.component';

import { configurationRoute } from './configuration.route';

@NgModule({
  imports: [SwinGiftsSharedModule, RouterModule.forChild([configurationRoute])],
  declarations: [SwgConfigurationComponent]
})
export class ConfigurationModule {}
