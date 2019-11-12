import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { IGiftDrawing, GiftDrawing } from 'app/shared/model/gift-drawing.model';
import { GiftDrawingService } from './gift-drawing.service';
import { IParticipation } from 'app/shared/model/participation.model';
import { ParticipationService } from 'app/entities/participation/participation.service';
import { IEvent } from 'app/shared/model/event.model';
import { EventService } from 'app/entities/event/event.service';

@Component({
  selector: 'swg-gift-drawing-update',
  templateUrl: './gift-drawing-update.component.html'
})
export class GiftDrawingUpdateComponent implements OnInit {
  isSaving: boolean;

  participations: IParticipation[];

  events: IEvent[];

  editForm = this.fb.group({
    id: [],
    recipient: [null, Validators.required],
    donor: [null, Validators.required],
    event: [null, Validators.required]
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected giftDrawingService: GiftDrawingService,
    protected participationService: ParticipationService,
    protected eventService: EventService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ giftDrawing }) => {
      this.updateForm(giftDrawing);
    });
    this.participationService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IParticipation[]>) => mayBeOk.ok),
        map((response: HttpResponse<IParticipation[]>) => response.body)
      )
      .subscribe((res: IParticipation[]) => (this.participations = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.eventService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IEvent[]>) => mayBeOk.ok),
        map((response: HttpResponse<IEvent[]>) => response.body)
      )
      .subscribe((res: IEvent[]) => (this.events = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(giftDrawing: IGiftDrawing) {
    this.editForm.patchValue({
      id: giftDrawing.id,
      recipient: giftDrawing.recipient,
      donor: giftDrawing.donor,
      event: giftDrawing.event
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const giftDrawing = this.createFromForm();
    if (giftDrawing.id !== undefined) {
      this.subscribeToSaveResponse(this.giftDrawingService.update(giftDrawing));
    } else {
      this.subscribeToSaveResponse(this.giftDrawingService.create(giftDrawing));
    }
  }

  private createFromForm(): IGiftDrawing {
    return {
      ...new GiftDrawing(),
      id: this.editForm.get(['id']).value,
      recipient: this.editForm.get(['recipient']).value,
      donor: this.editForm.get(['donor']).value,
      event: this.editForm.get(['event']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGiftDrawing>>) {
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

  trackEventById(index: number, item: IEvent) {
    return item.id;
  }
}
