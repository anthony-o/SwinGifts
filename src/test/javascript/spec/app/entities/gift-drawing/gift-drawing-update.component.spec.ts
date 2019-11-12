import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { SwinGiftsTestModule } from '../../../test.module';
import { GiftDrawingUpdateComponent } from 'app/entities/gift-drawing/gift-drawing-update.component';
import { GiftDrawingService } from 'app/entities/gift-drawing/gift-drawing.service';
import { GiftDrawing } from 'app/shared/model/gift-drawing.model';

describe('Component Tests', () => {
  describe('GiftDrawing Management Update Component', () => {
    let comp: GiftDrawingUpdateComponent;
    let fixture: ComponentFixture<GiftDrawingUpdateComponent>;
    let service: GiftDrawingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [GiftDrawingUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(GiftDrawingUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GiftDrawingUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(GiftDrawingService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new GiftDrawing(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new GiftDrawing();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
