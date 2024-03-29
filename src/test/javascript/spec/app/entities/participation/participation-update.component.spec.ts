import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { SwinGiftsTestModule } from '../../../test.module';
import { ParticipationUpdateComponent } from 'app/entities/participation/participation-update.component';
import { ParticipationService } from 'app/entities/participation/participation.service';
import { Participation } from 'app/shared/model/participation.model';

describe('Component Tests', () => {
  describe('Participation Management Update Component', () => {
    let comp: ParticipationUpdateComponent;
    let fixture: ComponentFixture<ParticipationUpdateComponent>;
    let service: ParticipationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [ParticipationUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ParticipationUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ParticipationUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ParticipationService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Participation(123);
        entity.user = { email: null, id: null, login: null };
        entity.event = {};
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
        const entity = new Participation();
        entity.user = { email: null, id: null, login: null };
        entity.event = {};
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
