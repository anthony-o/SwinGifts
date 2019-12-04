import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEvent } from 'app/shared/model/event.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { EventService } from 'app/entities/event/event.service';
import { JhiEventManager } from 'ng-jhipster';
import { PlatformLocation } from '@angular/common';

@Component({
  selector: 'swg-event-detail',
  templateUrl: './event-detail.component.html'
})
export class EventDetailComponent implements OnInit {
  event: IEvent;
  private account: Account;
  eventPublicUrl: string;

  constructor(
    protected activatedRoute: ActivatedRoute,
    private accountService: AccountService,
    private eventService: EventService,
    private eventManager: JhiEventManager,
    private platformLocation: PlatformLocation
  ) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ event }) => {
      this.setEvent(event);
    });
    this.accountService.identity().subscribe(account => (this.account = account));
  }

  private setEvent(event) {
    this.event = event;
    this.eventPublicUrl = event.publicKeyEnabled
      ? window.location.origin + this.platformLocation.getBaseHrefFromDOM() + 'event/public/' + event.publicKey
      : null;
  }

  previousState() {
    window.history.back();
  }

  drawGifts() {
    this.eventService.drawGifts(this.event.id).subscribe(() => {
      this.eventService.find(this.event.id).subscribe(result => {
        if (result.ok) {
          this.setEvent(result.body);
        }
      });
    });
  }

  isEventAdmin() {
    return (this.event && this.event.admin.id) === (this.account && this.account.id);
  }
}
