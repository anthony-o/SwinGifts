import { IParticipation } from 'app/shared/model/participation.model';

export interface IGiftDrawing {
  id?: number;
  recipient?: IParticipation;
  donor?: IParticipation;
}

export class GiftDrawing implements IGiftDrawing {
  constructor(public id?: number, public recipient?: IParticipation, public donor?: IParticipation) {}
}
