import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IParticipation } from 'app/shared/model/participation.model';
import { AccountService } from 'app/core';
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
    protected jhiAlertService: JhiAlertService,
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
      .subscribe(
        (res: IParticipation[]) => {
          this.participations = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
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

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
