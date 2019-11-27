import { Component, OnDestroy, OnInit } from '@angular/core';
import { of, Subscription } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { JhiEventManager } from 'ng-jhipster';

import { IGiftIdea } from 'app/shared/model/gift-idea.model';
import { AccountService } from 'app/core/auth/account.service';
import { GiftIdeaService } from './gift-idea.service';
import { ActivatedRoute } from '@angular/router';
import { GiftIdeaReservationService } from 'app/entities/gift-idea-reservation/gift-idea-reservation.service';
import { GiftIdeaReservation } from 'app/shared/model/gift-idea-reservation.model';

@Component({
  selector: 'swg-gift-idea',
  templateUrl: './gift-idea.component.html',
  styleUrls: ['./gift-idea.component.scss']
})
export class GiftIdeaComponent implements OnInit, OnDestroy {
  giftIdeas: IGiftIdea[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected giftIdeaService: GiftIdeaService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService,
    protected activatedRoute: ActivatedRoute,
    protected giftIdeaReservationService: GiftIdeaReservationService
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

  isReservedByMe(giftIdea: IGiftIdea) {
    return this.getMyGiftIdeaReservation(giftIdea);
  }

  private getMyGiftIdeaReservation(giftIdea: IGiftIdea) {
    const reservations = giftIdea.giftIdeaReservations;
    const reservationIndex = this.getMyGiftIdeaReservationIndex(reservations);
    if (reservationIndex !== -1) {
      return reservations[reservationIndex];
    } else {
      return null;
    }
  }

  private getMyGiftIdeaReservationIndex(reservations) {
    return reservations.findIndex(
      giftIdeaReservation => giftIdeaReservation.participation.user.id === (this.currentAccount && this.currentAccount.id)
    );
  }

  isRecipientIsMe(giftIdea: IGiftIdea) {
    return (
      (giftIdea.recipient && giftIdea.recipient.user && giftIdea.recipient.user.id) === (this.currentAccount && this.currentAccount.id)
    );
  }

  reserve(giftIdea: IGiftIdea) {
    this.giftIdeaReservationService
      .create(new GiftIdeaReservation(null, null, null, giftIdea))
      .pipe(
        filter(res => res.ok),
        map(res => res.body)
      )
      .subscribe(giftIdeaReservation => giftIdea.giftIdeaReservations.push(giftIdeaReservation));
  }

  release(giftIdea: IGiftIdea) {
    const reservations = giftIdea.giftIdeaReservations;
    const reservationIndex = this.getMyGiftIdeaReservationIndex(reservations);
    this.giftIdeaReservationService
      .delete(reservations[reservationIndex].id)
      .pipe(
        filter(res => res.ok),
        map(res => res.body)
      )
      .subscribe(() => reservations.splice(reservationIndex, 1));
  }
}
