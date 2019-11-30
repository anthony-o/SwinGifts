import { Injectable } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { HttpInterceptor, HttpRequest, HttpErrorResponse, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export type ErrorFilter = (err: HttpErrorResponse) => boolean;

@Injectable()
export class ErrorHandlerInterceptor implements HttpInterceptor {
  private static readonly ERROR_FILTERS: ErrorFilter[] = [];

  constructor(private eventManager: JhiEventManager) {}

  static filterNextRequest(errorFilter: ErrorFilter) {
    ErrorHandlerInterceptor.ERROR_FILTERS.push(errorFilter);
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const errorFilter = ErrorHandlerInterceptor.ERROR_FILTERS.length > 0 && ErrorHandlerInterceptor.ERROR_FILTERS.pop();

    return next.handle(request).pipe(
      tap(
        (event: HttpEvent<any>) => {},
        (err: any) => {
          if (err instanceof HttpErrorResponse && (!errorFilter || errorFilter(err))) {
            if (!(err.status === 401 && (err.message === '' || (err.url && err.url.includes('api/account'))))) {
              this.eventManager.broadcast({ name: 'swinGiftsApp.httpError', content: err });
            }
          }
        }
      )
    );
  }
}
