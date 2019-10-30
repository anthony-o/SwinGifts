import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IDrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';

type EntityResponseType = HttpResponse<IDrawingExclusionGroup>;
type EntityArrayResponseType = HttpResponse<IDrawingExclusionGroup[]>;

@Injectable({ providedIn: 'root' })
export class DrawingExclusionGroupService {
  public resourceUrl = SERVER_API_URL + 'api/drawing-exclusion-groups';

  constructor(protected http: HttpClient) {}

  create(drawingExclusionGroup: IDrawingExclusionGroup): Observable<EntityResponseType> {
    return this.http.post<IDrawingExclusionGroup>(this.resourceUrl, drawingExclusionGroup, { observe: 'response' });
  }

  update(drawingExclusionGroup: IDrawingExclusionGroup): Observable<EntityResponseType> {
    return this.http.put<IDrawingExclusionGroup>(this.resourceUrl, drawingExclusionGroup, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDrawingExclusionGroup>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDrawingExclusionGroup[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
