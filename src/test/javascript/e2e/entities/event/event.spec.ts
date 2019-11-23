// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import {
  EventComponentsPage,
  /* EventDeleteDialog,
   */ EventUpdatePage
} from './event.page-object';

const expect = chai.expect;

describe('Event e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let eventComponentsPage: EventComponentsPage;
  let eventUpdatePage: EventUpdatePage;
  /* let eventDeleteDialog: EventDeleteDialog; */

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Events', async () => {
    await navBarPage.goToEntity('event');
    eventComponentsPage = new EventComponentsPage();
    await browser.wait(ec.visibilityOf(eventComponentsPage.title), 5000);
    expect(await eventComponentsPage.getTitle()).to.eq('swinGiftsApp.event.home.title');
  });

  it('should load create Event page', async () => {
    await eventComponentsPage.clickOnCreateButton();
    eventUpdatePage = new EventUpdatePage();
    expect(await eventUpdatePage.getPageTitle()).to.eq('swinGiftsApp.event.home.createOrEditLabel');
    await eventUpdatePage.cancel();
  });

  /*  it('should create and save Events', async () => {
        const nbButtonsBeforeCreate = await eventComponentsPage.countDeleteButtons();

        await eventComponentsPage.clickOnCreateButton();
        await promise.all([
            eventUpdatePage.setNameInput('name'),
            eventUpdatePage.setDescriptionInput('description'),
            eventUpdatePage.setPublicKeyInput('publicKey'),
            eventUpdatePage.adminSelectLastOption(),
        ]);
        expect(await eventUpdatePage.getNameInput()).to.eq('name', 'Expected Name value to be equals to name');
        expect(await eventUpdatePage.getDescriptionInput()).to.eq('description', 'Expected Description value to be equals to description');
        expect(await eventUpdatePage.getPublicKeyInput()).to.eq('publicKey', 'Expected PublicKey value to be equals to publicKey');
        const selectedPublicKeyEnabled = eventUpdatePage.getPublicKeyEnabledInput();
        if (await selectedPublicKeyEnabled.isSelected()) {
            await eventUpdatePage.getPublicKeyEnabledInput().click();
            expect(await eventUpdatePage.getPublicKeyEnabledInput().isSelected(), 'Expected publicKeyEnabled not to be selected').to.be.false;
        } else {
            await eventUpdatePage.getPublicKeyEnabledInput().click();
            expect(await eventUpdatePage.getPublicKeyEnabledInput().isSelected(), 'Expected publicKeyEnabled to be selected').to.be.true;
        }
        await eventUpdatePage.save();
        expect(await eventUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

        expect(await eventComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
    }); */

  /*  it('should delete last Event', async () => {
        const nbButtonsBeforeDelete = await eventComponentsPage.countDeleteButtons();
        await eventComponentsPage.clickOnLastDeleteButton();

        eventDeleteDialog = new EventDeleteDialog();
        expect(await eventDeleteDialog.getDialogTitle())
            .to.eq('swinGiftsApp.event.delete.question');
        await eventDeleteDialog.clickOnConfirmButton();

        expect(await eventComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    }); */

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
