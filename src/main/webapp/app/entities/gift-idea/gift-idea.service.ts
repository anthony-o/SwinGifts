import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { IGiftIdea } from 'app/shared/model/gift-idea.model';

type EntityResponseType = HttpResponse<IGiftIdea>;
type EntityArrayResponseType = HttpResponse<IGiftIdea[]>;

@Injectable({ providedIn: 'root' })
export class GiftIdeaService {
  public resourceUrl = SERVER_API_URL + 'api/gift-ideas';

  constructor(protected http: HttpClient) {}

  create(giftIdea: IGiftIdea): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(giftIdea);
    return this.http
      .post<IGiftIdea>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(giftIdea: IGiftIdea): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(giftIdea);
    return this.http
      .put<IGiftIdea>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IGiftIdea>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  findByRecipientId(participationId: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<IGiftIdea[]>(`${this.resourceUrl}/by-recipient-id/${participationId}`, { observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(giftIdea: IGiftIdea): IGiftIdea {
    const copy: IGiftIdea = Object.assign({}, giftIdea, {
      creationDate: giftIdea.creationDate != null && giftIdea.creationDate.isValid() ? giftIdea.creationDate.toJSON() : null,
      modificationDate: giftIdea.modificationDate != null && giftIdea.modificationDate.isValid() ? giftIdea.modificationDate.toJSON() : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.creationDate = res.body.creationDate != null ? moment(res.body.creationDate) : null;
      res.body.modificationDate = res.body.modificationDate != null ? moment(res.body.modificationDate) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((giftIdea: IGiftIdea) => {
        giftIdea.creationDate = giftIdea.creationDate != null ? moment(giftIdea.creationDate) : null;
        giftIdea.modificationDate = giftIdea.modificationDate != null ? moment(giftIdea.modificationDate) : null;
      });
    }
    return res;
  }

  reserve(id: number) {
    return this.http
      .post<IGiftIdea>(`${this.resourceUrl}/${id}/reserve`, null, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  release(id: number) {
    return this.http
      .post<IGiftIdea>(`${this.resourceUrl}/${id}/release`, null, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }
}
