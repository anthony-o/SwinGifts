import { Route } from '@angular/router';

import { SwgMetricsMonitoringComponent } from './metrics.component';

export const metricsRoute: Route = {
  path: '',
  component: SwgMetricsMonitoringComponent,
  data: {
    pageTitle: 'metrics.title'
  }
};
