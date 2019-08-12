import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { IDrawingExclusionGroup, DrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';
import { DrawingExclusionGroupService } from './drawing-exclusion-group.service';
import { IParticipation } from 'app/shared/model/participation.model';
import { ParticipationService } from 'app/entities/participation';
import { IEvent } from 'app/shared/model/event.model';
import { EventService } from 'app/entities/event';

@Component({
  selector: 'swg-drawing-exclusion-group-update',
  templateUrl: './drawing-exclusion-group-update.component.html'
})
export class DrawingExclusionGroupUpdateComponent implements OnInit {
  isSaving: boolean;

  participations: IParticipation[];

  events: IEvent[];

  editForm = this.fb.group({
    id: [],
    participations: [],
    event: [null, Validators.required]
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected drawingExclusionGroupService: DrawingExclusionGroupService,
    protected participationService: ParticipationService,
    protected eventService: EventService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ drawingExclusionGroup }) => {
      this.updateForm(drawingExclusionGroup);
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

  updateForm(drawingExclusionGroup: IDrawingExclusionGroup) {
    this.editForm.patchValue({
      id: drawingExclusionGroup.id,
      participations: drawingExclusionGroup.participations,
      event: drawingExclusionGroup.event
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const drawingExclusionGroup = this.createFromForm();
    if (drawingExclusionGroup.id !== undefined) {
      this.subscribeToSaveResponse(this.drawingExclusionGroupService.update(drawingExclusionGroup));
    } else {
      this.subscribeToSaveResponse(this.drawingExclusionGroupService.create(drawingExclusionGroup));
    }
  }

  private createFromForm(): IDrawingExclusionGroup {
    return {
      ...new DrawingExclusionGroup(),
      id: this.editForm.get(['id']).value,
      participations: this.editForm.get(['participations']).value,
      event: this.editForm.get(['event']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDrawingExclusionGroup>>) {
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

  getSelected(selectedVals: Array<any>, option: any) {
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
