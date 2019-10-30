// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import {
  ParticipationComponentsPage,
  /* ParticipationDeleteDialog,
   */ ParticipationUpdatePage
} from './participation.page-object';

const expect = chai.expect;

describe('Participation e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let participationComponentsPage: ParticipationComponentsPage;
  let participationUpdatePage: ParticipationUpdatePage;
  /* let participationDeleteDialog: ParticipationDeleteDialog; */

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Participations', async () => {
    await navBarPage.goToEntity('participation');
    participationComponentsPage = new ParticipationComponentsPage();
    await browser.wait(ec.visibilityOf(participationComponentsPage.title), 5000);
    expect(await participationComponentsPage.getTitle()).to.eq('swinGiftsApp.participation.home.title');
  });

  it('should load create Participation page', async () => {
    await participationComponentsPage.clickOnCreateButton();
    participationUpdatePage = new ParticipationUpdatePage();
    expect(await participationUpdatePage.getPageTitle()).to.eq('swinGiftsApp.participation.home.createOrEditLabel');
    await participationUpdatePage.cancel();
  });

  /*  it('should create and save Participations', async () => {
        const nbButtonsBeforeCreate = await participationComponentsPage.countDeleteButtons();

        await participationComponentsPage.clickOnCreateButton();
        await promise.all([
            participationUpdatePage.setNbOfGiftToReceiveInput('5'),
            participationUpdatePage.setNbOfGiftToDonateInput('5'),
            participationUpdatePage.setUserAliasInput('userAlias'),
            participationUpdatePage.userSelectLastOption(),
            // participationUpdatePage.recipientSelectLastOption(),
            participationUpdatePage.eventSelectLastOption(),
        ]);
        expect(await participationUpdatePage.getNbOfGiftToReceiveInput()).to.eq('5', 'Expected nbOfGiftToReceive value to be equals to 5');
        expect(await participationUpdatePage.getNbOfGiftToDonateInput()).to.eq('5', 'Expected nbOfGiftToDonate value to be equals to 5');
        expect(await participationUpdatePage.getUserAliasInput()).to.eq('userAlias', 'Expected UserAlias value to be equals to userAlias');
        await participationUpdatePage.save();
        expect(await participationUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

        expect(await participationComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
    }); */

  /*  it('should delete last Participation', async () => {
        const nbButtonsBeforeDelete = await participationComponentsPage.countDeleteButtons();
        await participationComponentsPage.clickOnLastDeleteButton();

        participationDeleteDialog = new ParticipationDeleteDialog();
        expect(await participationDeleteDialog.getDialogTitle())
            .to.eq('swinGiftsApp.participation.delete.question');
        await participationDeleteDialog.clickOnConfirmButton();

        expect(await participationComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    }); */

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
