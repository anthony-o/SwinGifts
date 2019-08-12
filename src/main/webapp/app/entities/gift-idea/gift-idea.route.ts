import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { GiftIdea } from 'app/shared/model/gift-idea.model';
import { GiftIdeaService } from './gift-idea.service';
import { GiftIdeaComponent } from './gift-idea.component';
import { GiftIdeaDetailComponent } from './gift-idea-detail.component';
import { GiftIdeaUpdateComponent } from './gift-idea-update.component';
import { GiftIdeaDeletePopupComponent } from './gift-idea-delete-dialog.component';
import { IGiftIdea } from 'app/shared/model/gift-idea.model';

@Injectable({ providedIn: 'root' })
export class GiftIdeaResolve implements Resolve<IGiftIdea> {
  constructor(private service: GiftIdeaService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IGiftIdea> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<GiftIdea>) => response.ok),
        map((giftIdea: HttpResponse<GiftIdea>) => giftIdea.body)
      );
    }
    return of(new GiftIdea());
  }
}

export const giftIdeaRoute: Routes = [
  {
    path: '',
    component: GiftIdeaComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.giftIdea.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: GiftIdeaDetailComponent,
    resolve: {
      giftIdea: GiftIdeaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.giftIdea.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: GiftIdeaUpdateComponent,
    resolve: {
      giftIdea: GiftIdeaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.giftIdea.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: GiftIdeaUpdateComponent,
    resolve: {
      giftIdea: GiftIdeaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.giftIdea.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const giftIdeaPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: GiftIdeaDeletePopupComponent,
    resolve: {
      giftIdea: GiftIdeaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.giftIdea.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
