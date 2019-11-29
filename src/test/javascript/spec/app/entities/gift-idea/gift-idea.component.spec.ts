import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SwinGiftsTestModule } from '../../../test.module';
import { GiftIdeaComponent } from 'app/entities/gift-idea/gift-idea.component';
import { GiftIdeaService } from 'app/entities/gift-idea/gift-idea.service';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { GiftIdea } from 'app/shared/model/gift-idea.model';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

describe('Component Tests', () => {
  describe('GiftIdea Management Component', () => {
    let comp: GiftIdeaComponent;
    let fixture: ComponentFixture<GiftIdeaComponent>;
    let service: GiftIdeaService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [GiftIdeaComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { paramMap: of(convertToParamMap({ participationId: 1 })) } // mock participationId paramMap thanks to https://stackoverflow.com/a/46830024/535203
          }
        ]
      })
        .overrideTemplate(GiftIdeaComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GiftIdeaComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(GiftIdeaService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'findByRecipientId').and.returnValue(
        of(
          new HttpResponse({
            body: [new GiftIdea(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.findByRecipientId).toHaveBeenCalled();
      expect(comp.giftIdeas[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
