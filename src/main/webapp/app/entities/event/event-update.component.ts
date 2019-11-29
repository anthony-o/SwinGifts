import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';
import { Event, IEvent } from 'app/shared/model/event.model';
import { EventService } from './event.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'swg-event-update',
  templateUrl: './event-update.component.html'
})
export class EventUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    description: [null, [Validators.maxLength(8192)]],
    publicKey: [null, [Validators.maxLength(32)]],
    publicKeyEnabled: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected eventService: EventService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ event }) => {
      this.updateForm(event);
    });
  }

  updateForm(event: IEvent) {
    this.editForm.patchValue({
      id: event.id,
      name: event.name,
      description: event.description,
      publicKey: event.publicKey,
      publicKeyEnabled: event.publicKeyEnabled
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const event = this.createFromForm();
    if (event.id !== undefined) {
      this.subscribeToSaveResponse(this.eventService.update(event));
    } else {
      this.subscribeToSaveResponse(this.eventService.create(event));
    }
  }

  private createFromForm(): IEvent {
    return {
      ...new Event(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      description: this.editForm.get(['description']).value,
      publicKey: this.editForm.get(['publicKey']).value,
      publicKeyEnabled: this.editForm.get(['publicKeyEnabled']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvent>>) {
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
}
