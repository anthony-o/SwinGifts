import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { IGiftIdeaReservation } from 'app/shared/model/gift-idea-reservation.model';

type EntityResponseType = HttpResponse<IGiftIdeaReservation>;
type EntityArrayResponseType = HttpResponse<IGiftIdeaReservation[]>;

@Injectable({ providedIn: 'root' })
export class GiftIdeaReservationService {
  public resourceUrl = SERVER_API_URL + 'api/gift-idea-reservations';

  constructor(protected http: HttpClient) {}

  create(giftIdeaReservation: IGiftIdeaReservation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(giftIdeaReservation);
    return this.http
      .post<IGiftIdeaReservation>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(giftIdeaReservation: IGiftIdeaReservation): IGiftIdeaReservation {
    const copy: IGiftIdeaReservation = Object.assign({}, giftIdeaReservation, {
      creationDate:
        giftIdeaReservation.creationDate != null && giftIdeaReservation.creationDate.isValid()
          ? giftIdeaReservation.creationDate.toJSON()
          : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.creationDate = res.body.creationDate != null ? moment(res.body.creationDate) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((giftIdeaReservation: IGiftIdeaReservation) => {
        giftIdeaReservation.creationDate = giftIdeaReservation.creationDate != null ? moment(giftIdeaReservation.creationDate) : null;
      });
    }
    return res;
  }

  reserve(id: number) {
    return this.http
      .post<IGiftIdeaReservation>(`${this.resourceUrl}/${id}/reserve`, null, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  release(id: number) {
    return this.http
      .post<IGiftIdeaReservation>(`${this.resourceUrl}/${id}/release`, null, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }
}
