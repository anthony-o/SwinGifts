import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Participation } from 'app/shared/model/participation.model';
import { ParticipationService } from './participation.service';
import { ParticipationComponent } from './participation.component';
import { ParticipationDetailComponent } from './participation-detail.component';
import { ParticipationUpdateComponent } from './participation-update.component';
import { ParticipationDeletePopupComponent } from './participation-delete-dialog.component';
import { IParticipation } from 'app/shared/model/participation.model';

@Injectable({ providedIn: 'root' })
export class ParticipationResolve implements Resolve<IParticipation> {
  constructor(private service: ParticipationService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IParticipation> {
    const participationId = route.params['participationId'];
    if (participationId) {
      return this.service.find(participationId).pipe(
        filter((response: HttpResponse<Participation>) => response.ok),
        map((participation: HttpResponse<Participation>) => participation.body)
      );
    }
    return of(new Participation());
  }
}

export const participationRoute: Routes = [
  {
    path: '',
    component: ParticipationComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.participation.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':participationId/view',
    component: ParticipationDetailComponent,
    resolve: {
      participation: ParticipationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.participation.home.title'
    },
    canActivate: [UserRouteAccessService],
    children: [
      {
        path: '',
        loadChildren: () => import('../gift-idea/gift-idea.module').then(m => m.SwinGiftsGiftIdeaModule)
      }
    ]
  },
  {
    path: 'new',
    component: ParticipationUpdateComponent,
    resolve: {
      participation: ParticipationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.participation.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':participationId/edit',
    component: ParticipationUpdateComponent,
    resolve: {
      participation: ParticipationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.participation.home.title'
    },
    canActivate: [UserRouteAccessService],
    children: [
      {
        path: '',
        loadChildren: () => import('../gift-idea/gift-idea.module').then(m => m.SwinGiftsGiftIdeaModule)
      }
    ]
  }
];

export const participationPopupRoute: Routes = [
  {
    path: ':participationId/delete',
    component: ParticipationDeletePopupComponent,
    resolve: {
      participation: ParticipationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.participation.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
