import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IDrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';
import { AccountService } from 'app/core';
import { DrawingExclusionGroupService } from './drawing-exclusion-group.service';

@Component({
  selector: 'swg-drawing-exclusion-group',
  templateUrl: './drawing-exclusion-group.component.html'
})
export class DrawingExclusionGroupComponent implements OnInit, OnDestroy {
  drawingExclusionGroups: IDrawingExclusionGroup[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected drawingExclusionGroupService: DrawingExclusionGroupService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.drawingExclusionGroupService
      .query()
      .pipe(
        filter((res: HttpResponse<IDrawingExclusionGroup[]>) => res.ok),
        map((res: HttpResponse<IDrawingExclusionGroup[]>) => res.body)
      )
      .subscribe(
        (res: IDrawingExclusionGroup[]) => {
          this.drawingExclusionGroups = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInDrawingExclusionGroups();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IDrawingExclusionGroup) {
    return item.id;
  }

  registerChangeInDrawingExclusionGroups() {
    this.eventSubscriber = this.eventManager.subscribe('drawingExclusionGroupListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
