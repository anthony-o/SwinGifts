import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { SwinGiftsTestModule } from '../../../test.module';
import { GiftIdeaComponent } from 'app/entities/gift-idea/gift-idea.component';
import { GiftIdeaService } from 'app/entities/gift-idea/gift-idea.service';
import { GiftIdea } from 'app/shared/model/gift-idea.model';

describe('Component Tests', () => {
  describe('GiftIdea Management Component', () => {
    let comp: GiftIdeaComponent;
    let fixture: ComponentFixture<GiftIdeaComponent>;
    let service: GiftIdeaService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [GiftIdeaComponent],
        providers: []
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
      spyOn(service, 'query').and.returnValue(
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
      expect(service.query).toHaveBeenCalled();
      expect(comp.giftIdeas[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
