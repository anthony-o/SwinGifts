import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map } from 'rxjs/operators';
import { JhiEventManager } from 'ng-jhipster';

import { IGiftDrawing } from 'app/shared/model/gift-drawing.model';
import { AccountService } from 'app/core/auth/account.service';
import { GiftDrawingService } from './gift-drawing.service';

@Component({
  selector: 'swg-gift-drawing',
  templateUrl: './gift-drawing.component.html'
})
export class GiftDrawingComponent implements OnInit, OnDestroy {
  giftDrawings: IGiftDrawing[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected giftDrawingService: GiftDrawingService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.giftDrawingService
      .query()
      .pipe(
        filter((res: HttpResponse<IGiftDrawing[]>) => res.ok),
        map((res: HttpResponse<IGiftDrawing[]>) => res.body)
      )
      .subscribe((res: IGiftDrawing[]) => {
        this.giftDrawings = res;
      });
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().subscribe(account => {
      this.currentAccount = account;
    });
    this.registerChangeInGiftDrawings();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IGiftDrawing) {
    return item.id;
  }

  registerChangeInGiftDrawings() {
    this.eventSubscriber = this.eventManager.subscribe('giftDrawingListModification', response => this.loadAll());
  }
}
