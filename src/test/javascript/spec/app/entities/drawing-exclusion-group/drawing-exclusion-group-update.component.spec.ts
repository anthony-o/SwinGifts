/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { SwinGiftsTestModule } from '../../../test.module';
import { DrawingExclusionGroupUpdateComponent } from 'app/entities/drawing-exclusion-group/drawing-exclusion-group-update.component';
import { DrawingExclusionGroupService } from 'app/entities/drawing-exclusion-group/drawing-exclusion-group.service';
import { DrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';

describe('Component Tests', () => {
  describe('DrawingExclusionGroup Management Update Component', () => {
    let comp: DrawingExclusionGroupUpdateComponent;
    let fixture: ComponentFixture<DrawingExclusionGroupUpdateComponent>;
    let service: DrawingExclusionGroupService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [DrawingExclusionGroupUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(DrawingExclusionGroupUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(DrawingExclusionGroupUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(DrawingExclusionGroupService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new DrawingExclusionGroup(123);
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
        const entity = new DrawingExclusionGroup();
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
