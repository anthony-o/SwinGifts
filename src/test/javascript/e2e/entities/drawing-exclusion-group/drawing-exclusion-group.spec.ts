// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import {
  DrawingExclusionGroupComponentsPage,
  /* DrawingExclusionGroupDeleteDialog,
   */ DrawingExclusionGroupUpdatePage
} from './drawing-exclusion-group.page-object';

const expect = chai.expect;

describe('DrawingExclusionGroup e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let drawingExclusionGroupComponentsPage: DrawingExclusionGroupComponentsPage;
  let drawingExclusionGroupUpdatePage: DrawingExclusionGroupUpdatePage;
  /* let drawingExclusionGroupDeleteDialog: DrawingExclusionGroupDeleteDialog; */

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load DrawingExclusionGroups', async () => {
    await navBarPage.goToEntity('drawing-exclusion-group');
    drawingExclusionGroupComponentsPage = new DrawingExclusionGroupComponentsPage();
    await browser.wait(ec.visibilityOf(drawingExclusionGroupComponentsPage.title), 5000);
    expect(await drawingExclusionGroupComponentsPage.getTitle()).to.eq('swinGiftsApp.drawingExclusionGroup.home.title');
  });

  it('should load create DrawingExclusionGroup page', async () => {
    await drawingExclusionGroupComponentsPage.clickOnCreateButton();
    drawingExclusionGroupUpdatePage = new DrawingExclusionGroupUpdatePage();
    expect(await drawingExclusionGroupUpdatePage.getPageTitle()).to.eq('swinGiftsApp.drawingExclusionGroup.home.createOrEditLabel');
    await drawingExclusionGroupUpdatePage.cancel();
  });

  /*  it('should create and save DrawingExclusionGroups', async () => {
        const nbButtonsBeforeCreate = await drawingExclusionGroupComponentsPage.countDeleteButtons();

        await drawingExclusionGroupComponentsPage.clickOnCreateButton();
        await promise.all([
            // drawingExclusionGroupUpdatePage.participationSelectLastOption(),
            drawingExclusionGroupUpdatePage.eventSelectLastOption(),
        ]);
        await drawingExclusionGroupUpdatePage.save();
        expect(await drawingExclusionGroupUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

        expect(await drawingExclusionGroupComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
    }); */

  /*  it('should delete last DrawingExclusionGroup', async () => {
        const nbButtonsBeforeDelete = await drawingExclusionGroupComponentsPage.countDeleteButtons();
        await drawingExclusionGroupComponentsPage.clickOnLastDeleteButton();

        drawingExclusionGroupDeleteDialog = new DrawingExclusionGroupDeleteDialog();
        expect(await drawingExclusionGroupDeleteDialog.getDialogTitle())
            .to.eq('swinGiftsApp.drawingExclusionGroup.delete.question');
        await drawingExclusionGroupDeleteDialog.clickOnConfirmButton();

        expect(await drawingExclusionGroupComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    }); */

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
