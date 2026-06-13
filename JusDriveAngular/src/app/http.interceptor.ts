import {HttpRequest,HttpResponse,HttpInterceptorFn,HttpHandlerFn,} from '@angular/common/http';
import { tap } from 'rxjs/operators';

export const httpInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>,next: HttpHandlerFn) => {
    const started = Date.now();

    const token = localStorage.getItem('jwtToken')
    
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    return next(cloned).pipe(
      tap((event) => {
          if (event instanceof HttpResponse) {
              const elapsed = Date.now() - started;
              console.log(`Request for ${req.urlWithParams} took ${elapsed} ms.`);
          }
      })
  );

};