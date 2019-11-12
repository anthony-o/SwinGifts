import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { GiftDrawing } from 'app/shared/model/gift-drawing.model';
import { GiftDrawingService } from './gift-drawing.service';
import { GiftDrawingComponent } from './gift-drawing.component';
import { GiftDrawingDetailComponent } from './gift-drawing-detail.component';
import { GiftDrawingUpdateComponent } from './gift-drawing-update.component';
import { GiftDrawingDeletePopupComponent } from './gift-drawing-delete-dialog.component';
import { IGiftDrawing } from 'app/shared/model/gift-drawing.model';

@Injectable({ providedIn: 'root' })
export class GiftDrawingResolve implements Resolve<IGiftDrawing> {
  constructor(private service: GiftDrawingService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IGiftDrawing> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<GiftDrawing>) => response.ok),
        map((giftDrawing: HttpResponse<GiftDrawing>) => giftDrawing.body)
      );
    }
    return of(new GiftDrawing());
  }
}

export const giftDrawingRoute: Routes = [
  {
    path: '',
    component: GiftDrawingComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.giftDrawing.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: GiftDrawingDetailComponent,
    resolve: {
      giftDrawing: GiftDrawingResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.giftDrawing.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: GiftDrawingUpdateComponent,
    resolve: {
      giftDrawing: GiftDrawingResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.giftDrawing.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: GiftDrawingUpdateComponent,
    resolve: {
      giftDrawing: GiftDrawingResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.giftDrawing.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const giftDrawingPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: GiftDrawingDeletePopupComponent,
    resolve: {
      giftDrawing: GiftDrawingResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.giftDrawing.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
