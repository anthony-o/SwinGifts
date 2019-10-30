import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SwinGiftsSharedModule } from 'app/shared/shared.module';

import { SwgDocsComponent } from './docs.component';

import { docsRoute } from './docs.route';

@NgModule({
  imports: [SwinGiftsSharedModule, RouterModule.forChild([docsRoute])],
  declarations: [SwgDocsComponent]
})
export class DocsModule {}
