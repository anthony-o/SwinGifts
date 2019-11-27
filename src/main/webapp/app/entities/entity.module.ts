import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'event',
        loadChildren: () => import('./event/event.module').then(m => m.SwinGiftsEventModule)
      },
      {
        path: 'participation',
        loadChildren: () => import('./participation/participation.module').then(m => m.SwinGiftsParticipationModule)
      },
      {
        path: 'gift-idea',
        loadChildren: () => import('./gift-idea/gift-idea.module').then(m => m.SwinGiftsGiftIdeaModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class SwinGiftsEntityModule {}
