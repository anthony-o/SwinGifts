// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, protractor, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import {
  GiftIdeaComponentsPage,
  /* GiftIdeaDeleteDialog,
   */ GiftIdeaUpdatePage
} from './gift-idea.page-object';

const expect = chai.expect;

describe('GiftIdea e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let giftIdeaComponentsPage: GiftIdeaComponentsPage;
  let giftIdeaUpdatePage: GiftIdeaUpdatePage;
  /* let giftIdeaDeleteDialog: GiftIdeaDeleteDialog; */

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load GiftIdeas', async () => {
    await navBarPage.goToEntity('gift-idea');
    giftIdeaComponentsPage = new GiftIdeaComponentsPage();
    await browser.wait(ec.visibilityOf(giftIdeaComponentsPage.title), 5000);
    expect(await giftIdeaComponentsPage.getTitle()).to.eq('swinGiftsApp.giftIdea.home.title');
  });

  it('should load create GiftIdea page', async () => {
    await giftIdeaComponentsPage.clickOnCreateButton();
    giftIdeaUpdatePage = new GiftIdeaUpdatePage();
    expect(await giftIdeaUpdatePage.getPageTitle()).to.eq('swinGiftsApp.giftIdea.home.createOrEditLabel');
    await giftIdeaUpdatePage.cancel();
  });

  /*  it('should create and save GiftIdeas', async () => {
        const nbButtonsBeforeCreate = await giftIdeaComponentsPage.countDeleteButtons();

        await giftIdeaComponentsPage.clickOnCreateButton();
        await promise.all([
            giftIdeaUpdatePage.setDescriptionInput('description'),
            giftIdeaUpdatePage.setUrlInput('url'),
            giftIdeaUpdatePage.setCreationDateInput('01/01/2001' + protractor.Key.TAB + '02:30AM'),
            giftIdeaUpdatePage.setModificationDateInput('01/01/2001' + protractor.Key.TAB + '02:30AM'),
            giftIdeaUpdatePage.creatorSelectLastOption(),
            giftIdeaUpdatePage.takerSelectLastOption(),
            giftIdeaUpdatePage.recipientSelectLastOption(),
        ]);
        expect(await giftIdeaUpdatePage.getDescriptionInput()).to.eq('description', 'Expected Description value to be equals to description');
        expect(await giftIdeaUpdatePage.getUrlInput()).to.eq('url', 'Expected Url value to be equals to url');
        expect(await giftIdeaUpdatePage.getCreationDateInput()).to.contain('2001-01-01T02:30', 'Expected creationDate value to be equals to 2000-12-31');
        expect(await giftIdeaUpdatePage.getModificationDateInput()).to.contain('2001-01-01T02:30', 'Expected modificationDate value to be equals to 2000-12-31');
        await giftIdeaUpdatePage.save();
        expect(await giftIdeaUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

        expect(await giftIdeaComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
    }); */

  /*  it('should delete last GiftIdea', async () => {
        const nbButtonsBeforeDelete = await giftIdeaComponentsPage.countDeleteButtons();
        await giftIdeaComponentsPage.clickOnLastDeleteButton();

        giftIdeaDeleteDialog = new GiftIdeaDeleteDialog();
        expect(await giftIdeaDeleteDialog.getDialogTitle())
            .to.eq('swinGiftsApp.giftIdea.delete.question');
        await giftIdeaDeleteDialog.clickOnConfirmButton();

        expect(await giftIdeaComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    }); */

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
