import { browser, ExpectedConditions, element, by, ElementFinder } from 'protractor';

export class DrawingExclusionGroupComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('swg-drawing-exclusion-group div table .btn-danger'));
  title = element.all(by.css('swg-drawing-exclusion-group div h2#page-heading span')).first();

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

export class DrawingExclusionGroupUpdatePage {
  pageTitle = element(by.id('swg-drawing-exclusion-group-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  participationSelect = element(by.id('field_participation'));
  eventSelect = element(by.id('field_event'));

  async getPageTitle() {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async participationSelectLastOption(timeout?: number) {
    await this.participationSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async participationSelectOption(option) {
    await this.participationSelect.sendKeys(option);
  }

  getParticipationSelect(): ElementFinder {
    return this.participationSelect;
  }

  async getParticipationSelectedOption() {
    return await this.participationSelect.element(by.css('option:checked')).getText();
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

export class DrawingExclusionGroupDeleteDialog {
  private dialogTitle = element(by.id('swg-delete-drawingExclusionGroup-heading'));
  private confirmButton = element(by.id('swg-confirm-delete-drawingExclusionGroup'));

  async getDialogTitle() {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
