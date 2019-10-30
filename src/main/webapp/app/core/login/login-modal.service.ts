import { Injectable } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import { SwgLoginModalComponent } from 'app/shared/login/login.component';

@Injectable({ providedIn: 'root' })
export class LoginModalService {
  private isOpen = false;
  constructor(private modalService: NgbModal) {}

  open(): NgbModalRef {
    if (this.isOpen) {
      return;
    }
    this.isOpen = true;
    const modalRef = this.modalService.open(SwgLoginModalComponent);
    modalRef.result.finally(() => (this.isOpen = false));
    return modalRef;
  }
}
