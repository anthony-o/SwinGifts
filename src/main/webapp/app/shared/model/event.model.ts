import { IParticipation } from 'app/shared/model/participation.model';
import { IGiftDrawing } from 'app/shared/model/gift-drawing.model';
import { IDrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';
import { IUser } from 'app/core/user/user.model';

export interface IEvent {
  id?: number;
  name?: string;
  participations?: IParticipation[];
  giftDrawings?: IGiftDrawing[];
  drawingExclusionGroups?: IDrawingExclusionGroup[];
  admin?: IUser;
}

export class Event implements IEvent {
  constructor(
    public id?: number,
    public name?: string,
    public participations?: IParticipation[],
    public giftDrawings?: IGiftDrawing[],
    public drawingExclusionGroups?: IDrawingExclusionGroup[],
    public admin?: IUser
  ) {}
}
