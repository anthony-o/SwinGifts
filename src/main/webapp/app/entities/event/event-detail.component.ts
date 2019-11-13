import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEvent } from 'app/shared/model/event.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { EventService } from 'app/entities/event/event.service';
import { JhiEventManager } from 'ng-jhipster';

@Component({
  selector: 'swg-event-detail',
  templateUrl: './event-detail.component.html'
})
export class EventDetailComponent implements OnInit {
  event: IEvent;
  private account: Account;

  constructor(
    protected activatedRoute: ActivatedRoute,
    private accountService: AccountService,
    private eventService: EventService,
    private eventManager: JhiEventManager
  ) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ event }) => {
      this.event = event;
    });
    this.accountService.identity().subscribe(account => (this.account = account));
  }

  previousState() {
    window.history.back();
  }

  drawGifts() {
    this.eventService.drawGifts(this.event.id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'eventGiftsDrawn',
        content: "Event's gifts have been drawn."
      });
    });
  }

  isEventAdmin() {
    return (this.event && this.event.admin.id) === (this.account && this.account.id);
  }
}
