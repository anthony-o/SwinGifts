import { element, by, ElementFinder } from 'protractor';

export class GiftDrawingComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('swg-gift-drawing div table .btn-danger'));
  title = element.all(by.css('swg-gift-drawing div h2#page-heading span')).first();

  async clickOnCreateButton(timeout?: number) {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(timeout?: number) {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons() {
    return this.deleteButtons.count();
  }

  async getTitle() {
    return this.title.getAttribute('jhiTranslate');
  }
}

export class GiftDrawingUpdatePage {
  pageTitle = element(by.id('swg-gift-drawing-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  recipientSelect = element(by.id('field_recipient'));
  donorSelect = element(by.id('field_donor'));

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async recipientSelectLastOption(timeout?: number) {
    await this.recipientSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async recipientSelectOption(option) {
    await this.recipientSelect.sendKeys(option);
  }

  getRecipientSelect(): ElementFinder {
    return this.recipientSelect;
  }

  async getRecipientSelectedOption() {
    return await this.recipientSelect.element(by.css('option:checked')).getText();
  }

  async donorSelectLastOption(timeout?: number) {
    await this.donorSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async donorSelectOption(option) {
    await this.donorSelect.sendKeys(option);
  }

  getDonorSelect(): ElementFinder {
    return this.donorSelect;
  }

  async getDonorSelectedOption() {
    return await this.donorSelect.element(by.css('option:checked')).getText();
  }

  async save(timeout?: number) {
    await this.saveButton.click();
  }

  async cancel(timeout?: number) {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class GiftDrawingDeleteDialog {
  private dialogTitle = element(by.id('swg-delete-giftDrawing-heading'));
  private confirmButton = element(by.id('swg-confirm-delete-giftDrawing'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
