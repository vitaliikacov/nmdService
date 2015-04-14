package unit.feed.controller.export;

import nmd.orb.error.ServiceException;
import nmd.orb.http.responses.ExportReportResponse;
import nmd.orb.services.AutoExportService;
import nmd.orb.services.export.Change;
import org.junit.Test;
import unit.feed.controller.AbstractControllerTestBase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by igor on 18.01.2015.
 */
public class AutoExportServiceTest extends AbstractControllerTestBase {

    @Test
    public void whenPeriodGreaterThanDefinedAndNotificationIsNotSentThenItIsSent() throws ServiceException {
        final long start = System.currentTimeMillis();
        this.changeRegistrationService.registerChange();

        final boolean sent = this.autoExportService.export(start + AutoExportService.TWO_MINUTES * 2);

        assertTrue(sent);
        verify(this.mailServiceMock, times(1)).sendChangeNotification(any(ExportReportResponse.class));
    }

    @Test
    public void whenPeriodGreaterThanDefinedAndNotificationIsSentThenItIsNotSent() throws ServiceException {
        final long start = System.currentTimeMillis();
        this.changeRegistrationService.registerChange();

        final Change change = this.changeRepositoryStub.load();
        this.changeRepositoryStub.store(change.markAsSent());

        final boolean sent = this.autoExportService.export(start + AutoExportService.TWO_MINUTES * 2);

        assertFalse(sent);
        verify(this.mailServiceMock, never()).sendChangeNotification(any(ExportReportResponse.class));
    }

    @Test
    public void whenPeriodLesserThanDefinedAndNotificationIsNotSentThenItIsNotSent() throws ServiceException {
        final long start = System.currentTimeMillis();
        this.changeRegistrationService.registerChange();

        final boolean sent = this.autoExportService.export(start);

        assertFalse(sent);
        verify(this.mailServiceMock, never()).sendChangeNotification(any(ExportReportResponse.class));
    }

    @Test
    public void whenPeriodLesserThanDefinedAndNotificationIsSentThenItIsNotSent() throws ServiceException {
        final long start = System.currentTimeMillis();
        this.changeRegistrationService.registerChange();

        final Change change = this.changeRepositoryStub.load();
        this.changeRepositoryStub.store(change.markAsSent());

        final boolean sent = this.autoExportService.export(start);

        assertFalse(sent);
        verify(this.mailServiceMock, never()).sendChangeNotification(any(ExportReportResponse.class));
    }

}
