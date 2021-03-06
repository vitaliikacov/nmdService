package nmd.orb.services;

import com.google.appengine.api.utils.SystemProperty;
import com.google.gson.Gson;
import nmd.orb.error.ServiceException;
import nmd.orb.http.responses.ExportReportResponse;
import nmd.orb.services.change.Event;
import nmd.orb.services.mail.EventToHtmlConverter;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.List;
import java.util.Properties;

import static nmd.orb.error.ServiceError.mailServiceError;
import static nmd.orb.util.Assert.guard;
import static nmd.orb.util.Parameter.notNull;

/**
 * Created by igor on 18.01.2015.
 */
public class MailService {

    private static final Gson GSON = new Gson();

    public void sendChangeNotification(final List<Event> events, final ExportReportResponse report) throws ServiceException {
        guard(notNull(events));
        guard(notNull(report));

        try {
            final Properties props = new Properties();
            final Session session = Session.getDefaultInstance(props, null);

            final String exportEmail = System.getProperty("export.email");

            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(exportEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(exportEmail));

            final String applicationId = SystemProperty.applicationId.get();
            message.setSubject(String.format("%s : Exported feeds and categories", applicationId));

            final Multipart multipart = new MimeMultipart();

            final MimeBodyPart htmlPart = new MimeBodyPart();

            final String eventsAsHtml = EventToHtmlConverter.convert(events);
            htmlPart.setContent(String.format("<p>Please find exported feeds and categories in the attachment</p>%s", eventsAsHtml), "text/html");
            multipart.addBodyPart(htmlPart);

            final MimeBodyPart attachment = new MimeBodyPart();

            attachment.setFileName("export.json");
            final String reportAsJson = GSON.toJson(report);
            DataSource dataSource = new ByteArrayDataSource(reportAsJson.getBytes("UTF-8"), "application/octet-stream");
            attachment.setDataHandler(new DataHandler(dataSource));
            multipart.addBodyPart(attachment);

            message.setContent(multipart);

            Transport.send(message);
        } catch (Exception exception) {
            throw new ServiceException(mailServiceError(), exception);
        }
    }

}
