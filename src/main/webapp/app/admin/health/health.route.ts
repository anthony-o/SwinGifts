import { Route } from '@angular/router';

import { SwgHealthCheckComponent } from './health.component';

export const healthRoute: Route = {
  path: '',
  component: SwgHealthCheckComponent,
  data: {
    pageTitle: 'health.title'
  }
};
