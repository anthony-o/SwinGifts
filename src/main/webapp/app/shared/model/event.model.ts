import { IParticipation } from 'app/shared/model/participation.model';
import { IDrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';
import { IUser } from 'app/core/user/user.model';

export interface IEvent {
  id?: number;
  name?: string;
  participations?: IParticipation[];
  drawingExclusionGroups?: IDrawingExclusionGroup[];
  admin?: IUser;
}

export class Event implements IEvent {
  constructor(
    public id?: number,
    public name?: string,
    public participations?: IParticipation[],
    public drawingExclusionGroups?: IDrawingExclusionGroup[],
    public admin?: IUser
  ) {}
}
