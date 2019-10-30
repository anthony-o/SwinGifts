import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { SwinGiftsTestModule } from '../../../test.module';
import { DrawingExclusionGroupDeleteDialogComponent } from 'app/entities/drawing-exclusion-group/drawing-exclusion-group-delete-dialog.component';
import { DrawingExclusionGroupService } from 'app/entities/drawing-exclusion-group/drawing-exclusion-group.service';

describe('Component Tests', () => {
  describe('DrawingExclusionGroup Management Delete Component', () => {
    let comp: DrawingExclusionGroupDeleteDialogComponent;
    let fixture: ComponentFixture<DrawingExclusionGroupDeleteDialogComponent>;
    let service: DrawingExclusionGroupService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [DrawingExclusionGroupDeleteDialogComponent]
      })
        .overrideTemplate(DrawingExclusionGroupDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(DrawingExclusionGroupDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(DrawingExclusionGroupService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
