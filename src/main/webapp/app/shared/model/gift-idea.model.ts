import { Moment } from 'moment';
import { IParticipation } from 'app/shared/model/participation.model';

export interface IGiftIdea {
  id?: number;
  description?: string;
  url?: string;
  creationDate?: Moment;
  modificationDate?: Moment;
  creator?: IParticipation;
  taker?: IParticipation;
  recipient?: IParticipation;
}

export class GiftIdea implements IGiftIdea {
  constructor(
    public id?: number,
    public description?: string,
    public url?: string,
    public creationDate?: Moment,
    public modificationDate?: Moment,
    public creator?: IParticipation,
    public taker?: IParticipation,
    public recipient?: IParticipation
  ) {}
}
