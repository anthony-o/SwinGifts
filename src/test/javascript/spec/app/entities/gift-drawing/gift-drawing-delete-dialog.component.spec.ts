import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { SwinGiftsTestModule } from '../../../test.module';
import { GiftDrawingDeleteDialogComponent } from 'app/entities/gift-drawing/gift-drawing-delete-dialog.component';
import { GiftDrawingService } from 'app/entities/gift-drawing/gift-drawing.service';

describe('Component Tests', () => {
  describe('GiftDrawing Management Delete Component', () => {
    let comp: GiftDrawingDeleteDialogComponent;
    let fixture: ComponentFixture<GiftDrawingDeleteDialogComponent>;
    let service: GiftDrawingService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [GiftDrawingDeleteDialogComponent]
      })
        .overrideTemplate(GiftDrawingDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(GiftDrawingDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(GiftDrawingService);
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
