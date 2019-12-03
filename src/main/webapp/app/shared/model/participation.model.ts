import { IGiftDrawing } from 'app/shared/model/gift-drawing.model';
import { IGiftIdea } from 'app/shared/model/gift-idea.model';
import { IUser } from 'app/core/user/user.model';
import { IEvent } from 'app/shared/model/event.model';
import { IDrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';

export interface IParticipation {
  id?: number;
  nbOfGiftToReceive?: number;
  nbOfGiftToDonate?: number;
  userAlias?: string;
  giftDrawings?: IGiftDrawing[];
  giftIdeas?: IGiftIdea[];
  user?: IUser;
  event?: IEvent;
  drawingExclusionGroups?: IDrawingExclusionGroup[];
  nbOfCreatedGiftIdeas?: number;
  nbOfReservedGiftIdeas?: number;
}

export class Participation implements IParticipation {
  constructor(
    public id?: number,
    public nbOfGiftToReceive?: number,
    public nbOfGiftToDonate?: number,
    public userAlias?: string,
    public giftDrawings?: IGiftDrawing[],
    public giftIdeas?: IGiftIdea[],
    public user?: IUser,
    public event?: IEvent,
    public drawingExclusionGroups?: IDrawingExclusionGroup[]
  ) {}
}
