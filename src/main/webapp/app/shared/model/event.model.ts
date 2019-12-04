import { IParticipation } from 'app/shared/model/participation.model';
import { IGiftDrawing } from 'app/shared/model/gift-drawing.model';
import { IDrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';
import { IUser } from 'app/core/user/user.model';

export interface IEvent {
  id?: number;
  name?: string;
  description?: string;
  publicKey?: string;
  publicKeyEnabled?: boolean;
  participations?: IParticipation[];
  giftDrawings?: IGiftDrawing[];
  drawingExclusionGroups?: IDrawingExclusionGroup[];
  admin?: IUser;
  myGiftDrawings?: IGiftDrawing[];
}

export class Event implements IEvent {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string,
    public publicKey?: string,
    public publicKeyEnabled?: boolean,
    public participations?: IParticipation[],
    public giftDrawings?: IGiftDrawing[],
    public drawingExclusionGroups?: IDrawingExclusionGroup[],
    public admin?: IUser,
    public myGiftDrawings?: IGiftDrawing[]
  ) {
    this.publicKeyEnabled = this.publicKeyEnabled || false;
  }
}
