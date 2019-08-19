package info.smart_tools.smartactors.email.email_actor.email;

import me.normanmaurer.niosmtp.SMTPMessage;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SMTPMessageAdaptor implements SMTPMessage {
    private MimeMessage mimeMessage;
    private MimeMultipart mimeMessageContent;

    private static Properties sessionProperties = new Properties();

    // Constructor

    public SMTPMessageAdaptor(final MimeMessage message)
        throws MessagingException {
        mimeMessage = message;
        mimeMessageContent = new MimeMultipart();
        mimeMessage.setContent(mimeMessageContent);
    }

    public static MimeMessage createMimeMessage() {
        return new MimeMessage(Session.getDefaultInstance(sessionProperties, null));
    }

    // Methods of niosmtp.SMTPMessage

    @Override
    public InputStream get7bit() {
        return getContentStream();
    }

    @Override
    public InputStream get8Bit() {
        return getContentStream();
    }

    // Own methods

    public void addPart(final MimeBodyPart part)
            throws MessagingException {
        mimeMessageContent.addBodyPart(part);
    }

    public MimeMessage getMimeMessage() {
        return mimeMessage;
    }

    // Private area

    private InputStream getContentStream() {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            mimeMessage.writeTo(os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException | MessagingException e) {
            return null;
        }
    }
}
