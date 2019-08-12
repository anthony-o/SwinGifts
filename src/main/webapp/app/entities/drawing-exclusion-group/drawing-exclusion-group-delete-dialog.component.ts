import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IDrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';
import { DrawingExclusionGroupService } from './drawing-exclusion-group.service';

@Component({
  selector: 'swg-drawing-exclusion-group-delete-dialog',
  templateUrl: './drawing-exclusion-group-delete-dialog.component.html'
})
export class DrawingExclusionGroupDeleteDialogComponent {
  drawingExclusionGroup: IDrawingExclusionGroup;

  constructor(
    protected drawingExclusionGroupService: DrawingExclusionGroupService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.drawingExclusionGroupService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'drawingExclusionGroupListModification',
        content: 'Deleted an drawingExclusionGroup'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'swg-drawing-exclusion-group-delete-popup',
  template: ''
})
export class DrawingExclusionGroupDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ drawingExclusionGroup }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(DrawingExclusionGroupDeleteDialogComponent as Component, {
          size: 'lg',
          backdrop: 'static'
        });
        this.ngbModalRef.componentInstance.drawingExclusionGroup = drawingExclusionGroup;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/drawing-exclusion-group', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/drawing-exclusion-group', { outlets: { popup: null } }]);
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
