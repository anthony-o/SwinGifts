import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IGiftIdea } from 'app/shared/model/gift-idea.model';
import { GiftIdeaService } from './gift-idea.service';

@Component({
  selector: 'swg-gift-idea-delete-dialog',
  templateUrl: './gift-idea-delete-dialog.component.html'
})
export class GiftIdeaDeleteDialogComponent {
  giftIdea: IGiftIdea;

  constructor(protected giftIdeaService: GiftIdeaService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.giftIdeaService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'giftIdeaListModification',
        content: 'Deleted an giftIdea'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'swg-gift-idea-delete-popup',
  template: ''
})
export class GiftIdeaDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ giftIdea }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(GiftIdeaDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.giftIdea = giftIdea;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/gift-idea', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/gift-idea', { outlets: { popup: null } }]);
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
