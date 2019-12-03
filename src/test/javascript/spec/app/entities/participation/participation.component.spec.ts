import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { SwinGiftsTestModule } from '../../../test.module';
import { ParticipationComponent } from 'app/entities/participation/participation.component';
import { ParticipationService } from 'app/entities/participation/participation.service';
import { Participation } from 'app/shared/model/participation.model';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

describe('Component Tests', () => {
  describe('Participation Management Component', () => {
    let comp: ParticipationComponent;
    let fixture: ComponentFixture<ParticipationComponent>;
    let service: ParticipationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [ParticipationComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { paramMap: of(convertToParamMap({ eventId: 1 })) } // mock eventId paramMap thanks to https://stackoverflow.com/a/46830024/535203
          }
        ]
      })
        .overrideTemplate(ParticipationComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ParticipationComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ParticipationService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      const participation = new Participation(123);
      participation.giftIdeas = [];
      spyOn(service, 'findByEventId').and.returnValue(
        of(
          new HttpResponse({
            body: [participation],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.findByEventId).toHaveBeenCalled();
      expect(comp.participations[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
