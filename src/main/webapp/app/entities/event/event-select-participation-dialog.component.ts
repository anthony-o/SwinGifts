import { Component } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { EventService } from './event.service';
import { IParticipation } from 'app/shared/model/participation.model';

@Component({
  selector: 'swg-event-select-participation-dialog',
  templateUrl: './event-select-participation-dialog.component.html'
})
export class EventSelectParticipationDialogComponent {
  participation: IParticipation;

  constructor(protected eventService: EventService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmParticipationSelection() {
    this.activeModal.close(true);
  }
}
