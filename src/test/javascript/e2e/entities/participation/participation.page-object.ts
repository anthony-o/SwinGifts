import { element, by, ElementFinder } from 'protractor';

export class ParticipationComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('swg-participation div table .btn-danger'));
  title = element.all(by.css('swg-participation div h2#page-heading span')).first();

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

export class ParticipationUpdatePage {
  pageTitle = element(by.id('swg-participation-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  nbOfGiftToReceiveInput = element(by.id('field_nbOfGiftToReceive'));
  nbOfGiftToDonateInput = element(by.id('field_nbOfGiftToDonate'));
  userAliasInput = element(by.id('field_userAlias'));
  userSelect = element(by.id('field_user'));
  eventSelect = element(by.id('field_event'));

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setNbOfGiftToReceiveInput(nbOfGiftToReceive) {
    await this.nbOfGiftToReceiveInput.sendKeys(nbOfGiftToReceive);
  }

  async getNbOfGiftToReceiveInput() {
    return await this.nbOfGiftToReceiveInput.getAttribute('value');
  }

  async setNbOfGiftToDonateInput(nbOfGiftToDonate) {
    await this.nbOfGiftToDonateInput.sendKeys(nbOfGiftToDonate);
  }

  async getNbOfGiftToDonateInput() {
    return await this.nbOfGiftToDonateInput.getAttribute('value');
  }

  async setUserAliasInput(userAlias) {
    await this.userAliasInput.sendKeys(userAlias);
  }

  async getUserAliasInput() {
    return await this.userAliasInput.getAttribute('value');
  }

  async userSelectLastOption(timeout?: number) {
    await this.userSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async userSelectOption(option) {
    await this.userSelect.sendKeys(option);
  }

  getUserSelect(): ElementFinder {
    return this.userSelect;
  }

  async getUserSelectedOption() {
    return await this.userSelect.element(by.css('option:checked')).getText();
  }

  async eventSelectLastOption(timeout?: number) {
    await this.eventSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async eventSelectOption(option) {
    await this.eventSelect.sendKeys(option);
  }

  getEventSelect(): ElementFinder {
    return this.eventSelect;
  }

  async getEventSelectedOption() {
    return await this.eventSelect.element(by.css('option:checked')).getText();
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

export class ParticipationDeleteDialog {
  private dialogTitle = element(by.id('swg-delete-participation-heading'));
  private confirmButton = element(by.id('swg-confirm-delete-participation'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
