import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Event } from 'app/shared/model/event.model';
import { EventService } from './event.service';
import { EventComponent } from './event.component';
import { EventDetailComponent } from './event-detail.component';
import { EventUpdateComponent } from './event-update.component';
import { EventDeletePopupComponent } from './event-delete-dialog.component';
import { IEvent } from 'app/shared/model/event.model';
import { EventDetailPublicComponent } from 'app/entities/event/event-detail-public.component';

@Injectable({ providedIn: 'root' })
export class EventResolve implements Resolve<IEvent> {
  constructor(private service: EventService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IEvent> {
    const eventId = route.params['eventId'];
    if (eventId) {
      return this.service.find(eventId).pipe(
        filter((response: HttpResponse<Event>) => response.ok),
        map((event: HttpResponse<Event>) => event.body)
      );
    }
    return of(new Event());
  }
}

@Injectable({ providedIn: 'root' })
export class EventPublicResolve implements Resolve<IEvent> {
  constructor(private service: EventService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IEvent> {
    const eventPublicKey = route.params['eventPublicKey'];
    if (eventPublicKey) {
      return this.service.findByPublicKey(eventPublicKey).pipe(
        filter((response: HttpResponse<Event>) => response.ok),
        map((event: HttpResponse<Event>) => event.body)
      );
    }
    return of(new Event());
  }
}

export const eventRoute: Routes = [
  {
    path: '',
    component: EventComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.event.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':eventId/view',
    component: EventDetailComponent,
    resolve: {
      event: EventResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.event.home.title'
    },
    canActivate: [UserRouteAccessService],
    children: [
      {
        path: '',
        loadChildren: () => import('../participation/participation.module').then(m => m.SwinGiftsParticipationModule)
      }
    ]
  },
  {
    path: 'public/:eventPublicKey',
    component: EventDetailPublicComponent,
    resolve: {
      event: EventPublicResolve
    },
    data: {
      pageTitle: 'swinGiftsApp.event.home.title'
    }
  },
  {
    path: 'new',
    component: EventUpdateComponent,
    resolve: {
      event: EventResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.event.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':eventId/edit',
    component: EventUpdateComponent,
    resolve: {
      event: EventResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.event.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const eventPopupRoute: Routes = [
  {
    path: ':eventId/delete',
    component: EventDeletePopupComponent,
    resolve: {
      event: EventResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.event.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
