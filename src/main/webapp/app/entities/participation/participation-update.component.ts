import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';
import { IParticipation, Participation } from 'app/shared/model/participation.model';
import { ParticipationService } from './participation.service';
import { IUser } from 'app/core/user/user.model';
import { IEvent } from 'app/shared/model/event.model';
import { EventService } from 'app/entities/event/event.service';
import { IDrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';

@Component({
  selector: 'swg-participation-update',
  templateUrl: './participation-update.component.html'
})
export class ParticipationUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    nbOfGiftToReceive: [null, [Validators.min(0)]],
    nbOfGiftToDonate: [null, [Validators.min(0)]],
    userAlias: [null, [Validators.required]],
    user: this.fb.group({
      id: [],
      login: [],
      email: []
    }),
    event: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected participationService: ParticipationService,
    protected eventService: EventService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ participation }) => {
      this.updateForm(participation);
    });
  }

  updateForm(participation: IParticipation) {
    this.editForm.patchValue({
      id: participation.id,
      nbOfGiftToReceive: participation.nbOfGiftToReceive,
      nbOfGiftToDonate: participation.nbOfGiftToDonate,
      userAlias: participation.userAlias,
      user: participation.user,
      event: participation.event
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const participation = this.createFromForm();
    if (participation.id !== undefined) {
      this.subscribeToSaveResponse(this.participationService.update(participation));
    } else {
      this.subscribeToSaveResponse(this.participationService.create(participation));
    }
  }

  isCreating(): boolean {
    return this.editForm.get('id').value === undefined;
  }

  private createFromForm(): IParticipation {
    return {
      ...new Participation(),
      id: this.editForm.get(['id']).value,
      nbOfGiftToReceive: this.editForm.get(['nbOfGiftToReceive']).value,
      nbOfGiftToDonate: this.editForm.get(['nbOfGiftToDonate']).value,
      userAlias: this.editForm.get(['userAlias']).value,
      user: this.editForm.get(['user']).value,
      event: this.editForm.get(['event']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IParticipation>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackUserById(index: number, item: IUser) {
    return item.id;
  }

  trackEventById(index: number, item: IEvent) {
    return item.id;
  }

  trackDrawingExclusionGroupById(index: number, item: IDrawingExclusionGroup) {
    return item.id;
  }

  getSelected(selectedVals: any[], option: any) {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
