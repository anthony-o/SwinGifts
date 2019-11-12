import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IGiftDrawing } from 'app/shared/model/gift-drawing.model';
import { GiftDrawingService } from './gift-drawing.service';

@Component({
  selector: 'swg-gift-drawing-delete-dialog',
  templateUrl: './gift-drawing-delete-dialog.component.html'
})
export class GiftDrawingDeleteDialogComponent {
  giftDrawing: IGiftDrawing;

  constructor(
    protected giftDrawingService: GiftDrawingService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.giftDrawingService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'giftDrawingListModification',
        content: 'Deleted an giftDrawing'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'swg-gift-drawing-delete-popup',
  template: ''
})
export class GiftDrawingDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ giftDrawing }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(GiftDrawingDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.giftDrawing = giftDrawing;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/gift-drawing', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/gift-drawing', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
