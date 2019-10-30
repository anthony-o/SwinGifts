import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { SwinGiftsTestModule } from '../../../test.module';
import { GiftIdeaUpdateComponent } from 'app/entities/gift-idea/gift-idea-update.component';
import { GiftIdeaService } from 'app/entities/gift-idea/gift-idea.service';
import { GiftIdea } from 'app/shared/model/gift-idea.model';

describe('Component Tests', () => {
  describe('GiftIdea Management Update Component', () => {
    let comp: GiftIdeaUpdateComponent;
    let fixture: ComponentFixture<GiftIdeaUpdateComponent>;
    let service: GiftIdeaService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [GiftIdeaUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(GiftIdeaUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GiftIdeaUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(GiftIdeaService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new GiftIdea(123);
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
        const entity = new GiftIdea();
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
