import { IGiftIdea } from 'app/shared/model/gift-idea.model';
import { IUser } from 'app/core/user/user.model';
import { IParticipation } from 'app/shared/model/participation.model';
import { IEvent } from 'app/shared/model/event.model';

export interface IParticipation {
  id?: number;
  nbOfGiftToReceive?: number;
  nbOfGiftToDonate?: number;
  userAlias?: string;
  giftIdeas?: IGiftIdea[];
  user?: IUser;
  recipients?: IParticipation[];
  event?: IEvent;
  donors?: IParticipation[];
}

export class Participation implements IParticipation {
  constructor(
    public id?: number,
    public nbOfGiftToReceive?: number,
    public nbOfGiftToDonate?: number,
    public userAlias?: string,
    public giftIdeas?: IGiftIdea[],
    public user?: IUser,
    public recipients?: IParticipation[],
    public event?: IEvent,
    public donors?: IParticipation[]
  ) {}
}
