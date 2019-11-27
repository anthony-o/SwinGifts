import { Moment } from 'moment';
import { IGiftIdeaReservation } from 'app/shared/model/gift-idea-reservation.model';
import { IParticipation } from 'app/shared/model/participation.model';

export interface IGiftIdea {
  id?: number;
  description?: string;
  url?: string;
  creationDate?: Moment;
  modificationDate?: Moment;
  giftIdeaReservations?: IGiftIdeaReservation[];
  creator?: IParticipation;
  recipient?: IParticipation;
}

export class GiftIdea implements IGiftIdea {
  constructor(
    public id?: number,
    public description?: string,
    public url?: string,
    public creationDate?: Moment,
    public modificationDate?: Moment,
    public giftIdeaReservations?: IGiftIdeaReservation[],
    public creator?: IParticipation,
    public recipient?: IParticipation
  ) {}
}
