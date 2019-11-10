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
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Participation>) => response.ok),
        map((participation: HttpResponse<Participation>) => participation.body)
      );
    }
    return of(new Participation());
  }
}

@Injectable({ providedIn: 'root' })
export class ParticipationsByEventIdResolve implements Resolve<IParticipation[]> {
  constructor(private service: ParticipationService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IParticipation[]> {
    const eventId = route.params['eventId'];
    if (eventId) {
      return this.service.findByEventId(eventId).pipe(
        filter(response => response.ok),
        map(participations => participations.body)
      );
    }
    return of([]);
  }
}

export const participationRoute: Routes = [
  {
    path: '',
    component: ParticipationComponent,
    resolve: {
      participations: ParticipationsByEventIdResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.participation.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ParticipationDetailComponent,
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
    path: ':id/edit',
    component: ParticipationUpdateComponent,
    resolve: {
      participation: ParticipationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.participation.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const participationPopupRoute: Routes = [
  {
    path: ':id/delete',
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
