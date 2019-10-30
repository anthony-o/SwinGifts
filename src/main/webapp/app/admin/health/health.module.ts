import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SwinGiftsSharedModule } from 'app/shared/shared.module';

import { SwgHealthCheckComponent } from './health.component';
import { SwgHealthModalComponent } from './health-modal.component';

import { healthRoute } from './health.route';

@NgModule({
  imports: [SwinGiftsSharedModule, RouterModule.forChild([healthRoute])],
  declarations: [SwgHealthCheckComponent, SwgHealthModalComponent],
  entryComponents: [SwgHealthModalComponent]
})
export class HealthModule {}
