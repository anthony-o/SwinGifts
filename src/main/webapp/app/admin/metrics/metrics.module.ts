import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SwinGiftsSharedModule } from 'app/shared/shared.module';

import { SwgMetricsMonitoringComponent } from './metrics.component';

import { metricsRoute } from './metrics.route';

@NgModule({
  imports: [SwinGiftsSharedModule, RouterModule.forChild([metricsRoute])],
  declarations: [SwgMetricsMonitoringComponent]
})
export class MetricsModule {}
