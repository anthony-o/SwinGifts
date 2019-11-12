import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IGiftDrawing } from 'app/shared/model/gift-drawing.model';

type EntityResponseType = HttpResponse<IGiftDrawing>;
type EntityArrayResponseType = HttpResponse<IGiftDrawing[]>;

@Injectable({ providedIn: 'root' })
export class GiftDrawingService {
  public resourceUrl = SERVER_API_URL + 'api/gift-drawings';

  constructor(protected http: HttpClient) {}

  create(giftDrawing: IGiftDrawing): Observable<EntityResponseType> {
    return this.http.post<IGiftDrawing>(this.resourceUrl, giftDrawing, { observe: 'response' });
  }

  update(giftDrawing: IGiftDrawing): Observable<EntityResponseType> {
    return this.http.put<IGiftDrawing>(this.resourceUrl, giftDrawing, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGiftDrawing>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGiftDrawing[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
