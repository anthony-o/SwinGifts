import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { SwinGiftsTestModule } from '../../../test.module';
import { DrawingExclusionGroupComponent } from 'app/entities/drawing-exclusion-group/drawing-exclusion-group.component';
import { DrawingExclusionGroupService } from 'app/entities/drawing-exclusion-group/drawing-exclusion-group.service';
import { DrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';

describe('Component Tests', () => {
  describe('DrawingExclusionGroup Management Component', () => {
    let comp: DrawingExclusionGroupComponent;
    let fixture: ComponentFixture<DrawingExclusionGroupComponent>;
    let service: DrawingExclusionGroupService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [DrawingExclusionGroupComponent],
        providers: []
      })
        .overrideTemplate(DrawingExclusionGroupComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(DrawingExclusionGroupComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(DrawingExclusionGroupService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new DrawingExclusionGroup(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.drawingExclusionGroups[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
