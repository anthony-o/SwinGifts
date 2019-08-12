import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IGiftIdea } from 'app/shared/model/gift-idea.model';
import { AccountService } from 'app/core';
import { GiftIdeaService } from './gift-idea.service';

@Component({
  selector: 'swg-gift-idea',
  templateUrl: './gift-idea.component.html'
})
export class GiftIdeaComponent implements OnInit, OnDestroy {
  giftIdeas: IGiftIdea[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected giftIdeaService: GiftIdeaService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.giftIdeaService
      .query()
      .pipe(
        filter((res: HttpResponse<IGiftIdea[]>) => res.ok),
        map((res: HttpResponse<IGiftIdea[]>) => res.body)
      )
      .subscribe(
        (res: IGiftIdea[]) => {
          this.giftIdeas = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInGiftIdeas();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IGiftIdea) {
    return item.id;
  }

  registerChangeInGiftIdeas() {
    this.eventSubscriber = this.eventManager.subscribe('giftIdeaListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
