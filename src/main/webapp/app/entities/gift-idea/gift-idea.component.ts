import { Component, OnDestroy, OnInit } from '@angular/core';
import { of, Subscription } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { JhiEventManager } from 'ng-jhipster';

import { IGiftIdea } from 'app/shared/model/gift-idea.model';
import { AccountService } from 'app/core/auth/account.service';
import { GiftIdeaService } from './gift-idea.service';
import { ActivatedRoute } from '@angular/router';

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
    protected eventManager: JhiEventManager,
    protected accountService: AccountService,
    protected activatedRoute: ActivatedRoute
  ) {}

  loadAll() {
    this.activatedRoute.paramMap
      .pipe(
        switchMap(paramMap => {
          const participationId = paramMap.get('participationId');
          if (participationId) {
            return this.giftIdeaService.findByRecipientId(Number(participationId)).pipe(
              filter(res => res.ok),
              map(res => res.body)
            );
          } else {
            of([]);
          }
        })
      )
      .subscribe((res: IGiftIdea[]) => {
        this.giftIdeas = res;
      });
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().subscribe(account => {
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

  take(giftIdea: IGiftIdea, giftIdeaIndex: number) {
    this.giftIdeaService
      .take(giftIdea.id)
      .pipe(
        filter(res => res.ok),
        map(res => res.body)
      )
      .subscribe(modifiedGiftIdea => (this.giftIdeas[giftIdeaIndex] = modifiedGiftIdea));
  }

  isTakerIsMe(giftIdea: IGiftIdea) {
    return (giftIdea.taker && giftIdea.taker.user.id) === (this.currentAccount && this.currentAccount.id);
  }

  isRecipientIsMe(giftIdea: IGiftIdea) {
    return (
      (giftIdea.recipient && giftIdea.recipient.user && giftIdea.recipient.user.id) === (this.currentAccount && this.currentAccount.id)
    );
  }

  release(giftIdea: IGiftIdea, giftIdeaIndex: number) {
    this.giftIdeaService
      .release(giftIdea.id)
      .pipe(
        filter(res => res.ok),
        map(res => res.body)
      )
      .subscribe(modifiedGiftIdea => (this.giftIdeas[giftIdeaIndex] = modifiedGiftIdea));
  }
}
