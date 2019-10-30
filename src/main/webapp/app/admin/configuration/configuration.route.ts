import { Route } from '@angular/router';

import { SwgConfigurationComponent } from './configuration.component';

export const configurationRoute: Route = {
  path: '',
  component: SwgConfigurationComponent,
  data: {
    pageTitle: 'configuration.title'
  }
};
