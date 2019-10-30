import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { of } from 'rxjs';

import { SwinGiftsTestModule } from '../../../test.module';
import { SwgMetricsMonitoringComponent } from 'app/admin/metrics/metrics.component';
import { SwgMetricsService } from 'app/admin/metrics/metrics.service';

describe('Component Tests', () => {
  describe('SwgMetricsMonitoringComponent', () => {
    let comp: SwgMetricsMonitoringComponent;
    let fixture: ComponentFixture<SwgMetricsMonitoringComponent>;
    let service: SwgMetricsService;

    beforeEach(async(() => {
      TestBed.configureTestingModule({
        imports: [SwinGiftsTestModule],
        declarations: [SwgMetricsMonitoringComponent]
      })
        .overrideTemplate(SwgMetricsMonitoringComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(SwgMetricsMonitoringComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SwgMetricsService);
    });

    describe('refresh', () => {
      it('should call refresh on init', () => {
        // GIVEN
        const response = {
          timers: {
            service: 'test',
            unrelatedKey: 'test'
          },
          gauges: {
            'jcache.statistics': {
              value: 2
            },
            unrelatedKey: 'test'
          }
        };
        spyOn(service, 'getMetrics').and.returnValue(of(response));

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(service.getMetrics).toHaveBeenCalled();
      });
    });
  });
});
