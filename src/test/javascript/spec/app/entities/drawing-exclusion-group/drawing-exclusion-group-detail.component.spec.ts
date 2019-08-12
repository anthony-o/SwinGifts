/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SwinGiftsTestModule } from '../../../test.module';
import { DrawingExclusionGroupDetailComponent } from 'app/entities/drawing-exclusion-group/drawing-exclusion-group-detail.component';
import { DrawingExclusionGroup } from 'app/shared/model/drawing-exclusion-group.model';

describe('Component Tests', () => {
  describe('DrawingExclusionGroup Management Detail Component', () => {
    let comp: DrawingExclusionGroupDetailComponent;
    let fixture: ComponentFixture<DrawingExclusionGroupDetailComponent>;
    const route = ({ data: of({ drawingExclusionGroup: new DrawingExclusionGroup(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [DrawingExclusionGroupDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(DrawingExclusionGroupDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(DrawingExclusionGroupDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.drawingExclusionGroup).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
