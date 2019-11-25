import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { IEvent } from 'app/shared/model/event.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { IParticipation } from 'app/shared/model/participation.model';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventSelectParticipationDialogComponent } from 'app/entities/event/event-select-participation-dialog.component';
import { FormBuilder, Validators } from '@angular/forms';
import { ParticipationService } from 'app/entities/participation/participation.service';

@Component({
  selector: 'swg-event-detail-public',
  templateUrl: './event-detail-public.component.html'
})
export class EventDetailPublicComponent implements OnInit, OnDestroy {
  event: IEvent;
  currentAccount: Account;
  protected ngbModalRef: NgbModalRef;

  newParticipationForm = this.fb.group({
    userAlias: [null, [Validators.required]]
  });
  isSavingNewParticipation: boolean;

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected accountService: AccountService,
    protected modalService: NgbModal,
    private fb: FormBuilder,
    protected participationService: ParticipationService,
    private router: Router
  ) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ event }) => {
      this.event = event;
    });
    this.accountService.identity().subscribe(account => (this.currentAccount = account));
  }

  previousState() {
    window.history.back();
  }

  private getParticipationEvent(): IEvent {
    return {
      publicKey: this.activatedRoute.snapshot.params['eventPublicKey']
    };
  }

  confirmParticipationSelection(participation: IParticipation) {
    this.ngbModalRef = this.modalService.open(EventSelectParticipationDialogComponent as Component, { size: 'lg', backdrop: 'static' });
    this.ngbModalRef.componentInstance.participation = participation;
    this.ngbModalRef.result.then(
      result => {
        const participationToUpdate = {
          id: participation.id,
          event: this.getParticipationEvent()
        };
        this.participationService.updateUserWithCurrentUser(participationToUpdate).subscribe(
          res => {
            this.router.navigate(['/event', res.body.event.id, 'view']);
          },
          () => {
            // Do nothing
          }
        );
        this.ngbModalRef = null;
      },
      reason => {
        this.ngbModalRef = null;
      }
    );
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }

  saveNewParticipation() {
    this.isSavingNewParticipation = true;
    const participation = {
      userAlias: this.newParticipationForm.get(['userAlias']).value,
      event: this.getParticipationEvent()
    };
    this.participationService.createPublic(participation).subscribe(
      res => {
        this.isSavingNewParticipation = false;
        this.router.navigate(['/event', res.body.event.id, 'view']);
      },
      () => (this.isSavingNewParticipation = false)
    );
  }
}
