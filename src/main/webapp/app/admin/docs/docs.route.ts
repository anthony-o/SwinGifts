import { Route } from '@angular/router';

import { SwgDocsComponent } from './docs.component';

export const docsRoute: Route = {
  path: 'docs',
  component: SwgDocsComponent,
  data: {
    pageTitle: 'global.menu.admin.apidocs'
  }
};
