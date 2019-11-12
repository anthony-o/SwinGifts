import { IParticipation } from 'app/shared/model/participation.model';
import { IEvent } from 'app/shared/model/event.model';

export interface IGiftDrawing {
  id?: number;
  recipient?: IParticipation;
  donor?: IParticipation;
  event?: IEvent;
}

export class GiftDrawing implements IGiftDrawing {
  constructor(public id?: number, public recipient?: IParticipation, public donor?: IParticipation, public event?: IEvent) {}
}
