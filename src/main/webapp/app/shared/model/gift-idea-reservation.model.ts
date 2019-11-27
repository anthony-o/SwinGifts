import { Moment } from 'moment';
import { IParticipation } from 'app/shared/model/participation.model';
import { IGiftIdea } from 'app/shared/model/gift-idea.model';

export interface IGiftIdeaReservation {
  id?: number;
  creationDate?: Moment;
  participation?: IParticipation;
  giftIdea?: IGiftIdea;
}

export class GiftIdeaReservation implements IGiftIdeaReservation {
  constructor(public id?: number, public creationDate?: Moment, public participation?: IParticipation, public giftIdea?: IGiftIdea) {}
}
