import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { IEvent, Event } from 'app/shared/model/event.model';
import { EventService } from './event.service';
import { IUser, UserService } from 'app/core';

@Component({
  selector: 'swg-event-update',
  templateUrl: './event-update.component.html'
})
export class EventUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    admin: [null, Validators.required]
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
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(event: IEvent) {
    this.editForm.patchValue({
      id: event.id,
      name: event.name,
      admin: event.admin
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
      admin: this.editForm.get(['admin']).value
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
