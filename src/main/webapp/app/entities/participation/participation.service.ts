import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IParticipation } from 'app/shared/model/participation.model';

type EntityResponseType = HttpResponse<IParticipation>;
type EntityArrayResponseType = HttpResponse<IParticipation[]>;

@Injectable({ providedIn: 'root' })
export class ParticipationService {
  public resourceUrl = SERVER_API_URL + 'api/participations';

  constructor(protected http: HttpClient) {}

  create(participation: IParticipation): Observable<EntityResponseType> {
    return this.http.post<IParticipation>(this.resourceUrl, participation, { observe: 'response' });
  }

  update(participation: IParticipation): Observable<EntityResponseType> {
    return this.http.put<IParticipation>(this.resourceUrl, participation, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IParticipation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IParticipation[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  findByEventId(eventId: number): Observable<EntityArrayResponseType> {
    return this.http.get<IParticipation[]>(`${this.resourceUrl}/by-event-id/${eventId}`, { observe: 'response' });
  }

  createPublic(participation: IParticipation): Observable<EntityResponseType> {
    return this.http.post<IParticipation>(`${SERVER_API_URL}/api/public/participations`, participation, { observe: 'response' });
  }

  updateUserWithCurrentUser(participation: IParticipation): Observable<EntityResponseType> {
    return this.http.post<IParticipation>(
      `${this.resourceUrl}/${participation.id}/set-user-with-current-user`,
      participation.event.publicKey,
      { observe: 'response' }
    );
  }
}
