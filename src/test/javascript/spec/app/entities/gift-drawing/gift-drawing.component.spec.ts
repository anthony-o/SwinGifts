import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { SwinGiftsTestModule } from '../../../test.module';
import { GiftDrawingComponent } from 'app/entities/gift-drawing/gift-drawing.component';
import { GiftDrawingService } from 'app/entities/gift-drawing/gift-drawing.service';
import { GiftDrawing } from 'app/shared/model/gift-drawing.model';

describe('Component Tests', () => {
  describe('GiftDrawing Management Component', () => {
    let comp: GiftDrawingComponent;
    let fixture: ComponentFixture<GiftDrawingComponent>;
    let service: GiftDrawingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [GiftDrawingComponent],
        providers: []
      })
        .overrideTemplate(GiftDrawingComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GiftDrawingComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(GiftDrawingService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new GiftDrawing(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.giftDrawings[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
