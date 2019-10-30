import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SwinGiftsTestModule } from '../../../test.module';
import { GiftIdeaDetailComponent } from 'app/entities/gift-idea/gift-idea-detail.component';
import { GiftIdea } from 'app/shared/model/gift-idea.model';

describe('Component Tests', () => {
  describe('GiftIdea Management Detail Component', () => {
    let comp: GiftIdeaDetailComponent;
    let fixture: ComponentFixture<GiftIdeaDetailComponent>;
    const route = ({ data: of({ giftIdea: new GiftIdea(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [GiftIdeaDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(GiftIdeaDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(GiftIdeaDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.giftIdea).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
