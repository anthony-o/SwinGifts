import { IParticipation } from 'app/shared/model/participation.model';
import { IEvent } from 'app/shared/model/event.model';

export interface IDrawingExclusionGroup {
  id?: number;
  participations?: IParticipation[];
  event?: IEvent;
}

export class DrawingExclusionGroup implements IDrawingExclusionGroup {
  constructor(public id?: number, public participations?: IParticipation[], public event?: IEvent) {}
}
