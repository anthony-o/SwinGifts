import { element, by, ElementFinder } from 'protractor';

export class GiftIdeaComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('swg-gift-idea div table .btn-danger'));
  title = element.all(by.css('swg-gift-idea div h2#page-heading span')).first();

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

export class GiftIdeaUpdatePage {
  pageTitle = element(by.id('swg-gift-idea-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  descriptionInput = element(by.id('field_description'));
  urlInput = element(by.id('field_url'));
  creationDateInput = element(by.id('field_creationDate'));
  modificationDateInput = element(by.id('field_modificationDate'));
  creatorSelect = element(by.id('field_creator'));
  recipientSelect = element(by.id('field_recipient'));

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setDescriptionInput(description) {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput() {
    return await this.descriptionInput.getAttribute('value');
  }

  async setUrlInput(url) {
    await this.urlInput.sendKeys(url);
  }

  async getUrlInput() {
    return await this.urlInput.getAttribute('value');
  }

  async setCreationDateInput(creationDate) {
    await this.creationDateInput.sendKeys(creationDate);
  }

  async getCreationDateInput() {
    return await this.creationDateInput.getAttribute('value');
  }

  async setModificationDateInput(modificationDate) {
    await this.modificationDateInput.sendKeys(modificationDate);
  }

  async getModificationDateInput() {
    return await this.modificationDateInput.getAttribute('value');
  }

  async creatorSelectLastOption(timeout?: number) {
    await this.creatorSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async creatorSelectOption(option) {
    await this.creatorSelect.sendKeys(option);
  }

  getCreatorSelect(): ElementFinder {
    return this.creatorSelect;
  }

  async getCreatorSelectedOption() {
    return await this.creatorSelect.element(by.css('option:checked')).getText();
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

export class GiftIdeaDeleteDialog {
  private dialogTitle = element(by.id('swg-delete-giftIdea-heading'));
  private confirmButton = element(by.id('swg-confirm-delete-giftIdea'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
