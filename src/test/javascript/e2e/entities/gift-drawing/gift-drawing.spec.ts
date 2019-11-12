// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { GiftDrawingComponentsPage, GiftDrawingDeleteDialog, GiftDrawingUpdatePage } from './gift-drawing.page-object';

const expect = chai.expect;

describe('GiftDrawing e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let giftDrawingComponentsPage: GiftDrawingComponentsPage;
  let giftDrawingUpdatePage: GiftDrawingUpdatePage;
  let giftDrawingDeleteDialog: GiftDrawingDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load GiftDrawings', async () => {
    await navBarPage.goToEntity('gift-drawing');
    giftDrawingComponentsPage = new GiftDrawingComponentsPage();
    await browser.wait(ec.visibilityOf(giftDrawingComponentsPage.title), 5000);
    expect(await giftDrawingComponentsPage.getTitle()).to.eq('swinGiftsApp.giftDrawing.home.title');
  });

  it('should load create GiftDrawing page', async () => {
    await giftDrawingComponentsPage.clickOnCreateButton();
    giftDrawingUpdatePage = new GiftDrawingUpdatePage();
    expect(await giftDrawingUpdatePage.getPageTitle()).to.eq('swinGiftsApp.giftDrawing.home.createOrEditLabel');
    await giftDrawingUpdatePage.cancel();
  });

  it('should create and save GiftDrawings', async () => {
    const nbButtonsBeforeCreate = await giftDrawingComponentsPage.countDeleteButtons();

    await giftDrawingComponentsPage.clickOnCreateButton();
    await promise.all([giftDrawingUpdatePage.recipientSelectLastOption(), giftDrawingUpdatePage.donorSelectLastOption()]);
    await giftDrawingUpdatePage.save();
    expect(await giftDrawingUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await giftDrawingComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last GiftDrawing', async () => {
    const nbButtonsBeforeDelete = await giftDrawingComponentsPage.countDeleteButtons();
    await giftDrawingComponentsPage.clickOnLastDeleteButton();

    giftDrawingDeleteDialog = new GiftDrawingDeleteDialog();
    expect(await giftDrawingDeleteDialog.getDialogTitle()).to.eq('swinGiftsApp.giftDrawing.delete.question');
    await giftDrawingDeleteDialog.clickOnConfirmButton();

    expect(await giftDrawingComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
