import { element, by, ElementFinder } from 'protractor';

export class EventComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('swg-event div table .btn-danger'));
  title = element.all(by.css('swg-event div h2#page-heading span')).first();

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

export class EventUpdatePage {
  pageTitle = element(by.id('swg-event-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  nameInput = element(by.id('field_name'));
  descriptionInput = element(by.id('field_description'));
  publicKeyInput = element(by.id('field_publicKey'));
  adminSelect = element(by.id('field_admin'));

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setNameInput(name) {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput() {
    return await this.nameInput.getAttribute('value');
  }

  async setDescriptionInput(description) {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput() {
    return await this.descriptionInput.getAttribute('value');
  }

  async setPublicKeyInput(publicKey) {
    await this.publicKeyInput.sendKeys(publicKey);
  }

  async getPublicKeyInput() {
    return await this.publicKeyInput.getAttribute('value');
  }

  async adminSelectLastOption(timeout?: number) {
    await this.adminSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async adminSelectOption(option) {
    await this.adminSelect.sendKeys(option);
  }

  getAdminSelect(): ElementFinder {
    return this.adminSelect;
  }

  async getAdminSelectedOption() {
    return await this.adminSelect.element(by.css('option:checked')).getText();
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

export class EventDeleteDialog {
  private dialogTitle = element(by.id('swg-delete-event-heading'));
  private confirmButton = element(by.id('swg-confirm-delete-event'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
