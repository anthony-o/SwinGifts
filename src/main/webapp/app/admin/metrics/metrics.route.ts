import { Route } from '@angular/router';

import { SwgMetricsMonitoringComponent } from './metrics.component';

export const metricsRoute: Route = {
  path: 'swg-metrics',
  component: SwgMetricsMonitoringComponent,
  data: {
    pageTitle: 'metrics.title'
  }
};
