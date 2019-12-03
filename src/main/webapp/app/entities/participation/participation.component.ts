import { Component, OnDestroy, OnInit } from '@angular/core';
import { of, Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { IParticipation } from 'app/shared/model/participation.model';
import { AccountService } from 'app/core/auth/account.service';
import { ActivatedRoute } from '@angular/router';
import { ParticipationService } from 'app/entities/participation/participation.service';
import { filter, map, switchMap } from 'rxjs/operators';
import { Account } from 'app/core/user/account.model';

@Component({
  selector: 'swg-participation',
  templateUrl: './participation.component.html'
})
export class ParticipationComponent implements OnInit, OnDestroy {
  participations: IParticipation[];
  currentAccount: Account;
  eventSubscriber: Subscription;

  constructor(
    protected participationService: ParticipationService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService,
    protected activatedRoute: ActivatedRoute
  ) {}

  loadAll() {
    this.activatedRoute.paramMap
      .pipe(
        switchMap(paramMap => {
          const eventId = paramMap.get('eventId');
          if (eventId) {
            return this.participationService.findByEventId(Number(eventId)).pipe(
              filter(res => res.ok),
              map(res => res.body)
            );
          } else {
            of([]);
          }
        })
      )
      .subscribe((res: IParticipation[]) => {
        this.participations = res.map(participation => {
          participation.nbOfCreatedGiftIdeas = participation.giftIdeas.filter(giftIdea => giftIdea.creator.id === participation.id).length;
          participation.nbOfReservedGiftIdeas = participation.giftIdeas.filter(
            giftIdea => giftIdea.giftIdeaReservations && giftIdea.giftIdeaReservations.length > 0
          ).length;
          return participation;
        });
      });
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().subscribe(account => {
      this.currentAccount = account;
    });
    this.registerChangeInParticipations();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IParticipation) {
    return item.id;
  }

  registerChangeInParticipations() {
    this.eventSubscriber = this.eventManager.subscribe('participationListModification', response => this.loadAll());
  }

  isParticipationIsMe(participation: IParticipation) {
    return (participation.user && participation.user.id) === this.currentAccount.id;
  }
}
