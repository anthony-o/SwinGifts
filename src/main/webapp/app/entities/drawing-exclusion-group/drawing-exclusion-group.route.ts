import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { DrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';
import { DrawingExclusionGroupService } from './drawing-exclusion-group.service';
import { DrawingExclusionGroupComponent } from './drawing-exclusion-group.component';
import { DrawingExclusionGroupDetailComponent } from './drawing-exclusion-group-detail.component';
import { DrawingExclusionGroupUpdateComponent } from './drawing-exclusion-group-update.component';
import { DrawingExclusionGroupDeletePopupComponent } from './drawing-exclusion-group-delete-dialog.component';
import { IDrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';

@Injectable({ providedIn: 'root' })
export class DrawingExclusionGroupResolve implements Resolve<IDrawingExclusionGroup> {
  constructor(private service: DrawingExclusionGroupService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IDrawingExclusionGroup> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<DrawingExclusionGroup>) => response.ok),
        map((drawingExclusionGroup: HttpResponse<DrawingExclusionGroup>) => drawingExclusionGroup.body)
      );
    }
    return of(new DrawingExclusionGroup());
  }
}

export const drawingExclusionGroupRoute: Routes = [
  {
    path: '',
    component: DrawingExclusionGroupComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.drawingExclusionGroup.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: DrawingExclusionGroupDetailComponent,
    resolve: {
      drawingExclusionGroup: DrawingExclusionGroupResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.drawingExclusionGroup.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: DrawingExclusionGroupUpdateComponent,
    resolve: {
      drawingExclusionGroup: DrawingExclusionGroupResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.drawingExclusionGroup.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: DrawingExclusionGroupUpdateComponent,
    resolve: {
      drawingExclusionGroup: DrawingExclusionGroupResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.drawingExclusionGroup.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const drawingExclusionGroupPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: DrawingExclusionGroupDeletePopupComponent,
    resolve: {
      drawingExclusionGroup: DrawingExclusionGroupResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'swinGiftsApp.drawingExclusionGroup.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
