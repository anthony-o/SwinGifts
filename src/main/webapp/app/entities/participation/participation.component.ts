import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map } from 'rxjs/operators';
import { JhiEventManager } from 'ng-jhipster';

import { IParticipation } from 'app/shared/model/participation.model';
import { AccountService } from 'app/core/auth/account.service';
import { ParticipationService } from './participation.service';

@Component({
  selector: 'swg-participation',
  templateUrl: './participation.component.html'
})
export class ParticipationComponent implements OnInit, OnDestroy {
  participations: IParticipation[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected participationService: ParticipationService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.participationService
      .query()
      .pipe(
        filter((res: HttpResponse<IParticipation[]>) => res.ok),
        map((res: HttpResponse<IParticipation[]>) => res.body)
      )
      .subscribe((res: IParticipation[]) => {
        this.participations = res;
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
}
