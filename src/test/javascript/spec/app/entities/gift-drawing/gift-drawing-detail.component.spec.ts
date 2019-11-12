import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SwinGiftsTestModule } from '../../../test.module';
import { GiftDrawingDetailComponent } from 'app/entities/gift-drawing/gift-drawing-detail.component';
import { GiftDrawing } from 'app/shared/model/gift-drawing.model';

describe('Component Tests', () => {
  describe('GiftDrawing Management Detail Component', () => {
    let comp: GiftDrawingDetailComponent;
    let fixture: ComponentFixture<GiftDrawingDetailComponent>;
    const route = ({ data: of({ giftDrawing: new GiftDrawing(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [GiftDrawingDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(GiftDrawingDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(GiftDrawingDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.giftDrawing).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
