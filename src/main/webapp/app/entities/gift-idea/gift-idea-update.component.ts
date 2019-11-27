import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';
import { GiftIdea, IGiftIdea } from 'app/shared/model/gift-idea.model';
import { GiftIdeaService } from './gift-idea.service';
import { IParticipation } from 'app/shared/model/participation.model';
import { ParticipationService } from 'app/entities/participation/participation.service';

@Component({
  selector: 'swg-gift-idea-update',
  templateUrl: './gift-idea-update.component.html'
})
export class GiftIdeaUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    description: [null, [Validators.required, Validators.maxLength(2048)]],
    url: [null, [Validators.maxLength(2048)]],
    creationDate: [],
    modificationDate: [],
    creator: [],
    recipient: [null, Validators.required]
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected giftIdeaService: GiftIdeaService,
    protected participationService: ParticipationService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ giftIdea }) => {
      this.updateForm(giftIdea);
    });
  }

  updateForm(giftIdea: IGiftIdea) {
    this.editForm.patchValue({
      id: giftIdea.id,
      description: giftIdea.description,
      url: giftIdea.url,
      creationDate: giftIdea.creationDate != null ? giftIdea.creationDate.format(DATE_TIME_FORMAT) : null,
      modificationDate: giftIdea.modificationDate != null ? giftIdea.modificationDate.format(DATE_TIME_FORMAT) : null,
      creator: giftIdea.creator,
      recipient: giftIdea.recipient
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const giftIdea = this.createFromForm();
    if (giftIdea.id !== undefined) {
      this.subscribeToSaveResponse(this.giftIdeaService.update(giftIdea));
    } else {
      this.subscribeToSaveResponse(this.giftIdeaService.create(giftIdea));
    }
  }

  private createFromForm(): IGiftIdea {
    return {
      ...new GiftIdea(),
      id: this.editForm.get(['id']).value,
      description: this.editForm.get(['description']).value,
      url: this.editForm.get(['url']).value,
      creationDate:
        this.editForm.get(['creationDate']).value != null ? moment(this.editForm.get(['creationDate']).value, DATE_TIME_FORMAT) : undefined,
      modificationDate:
        this.editForm.get(['modificationDate']).value != null
          ? moment(this.editForm.get(['modificationDate']).value, DATE_TIME_FORMAT)
          : undefined,
      creator: this.editForm.get(['creator']).value,
      recipient: this.editForm.get(['recipient']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGiftIdea>>) {
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

  trackParticipationById(index: number, item: IParticipation) {
    return item.id;
  }
}
