package info.smart_tools.smartactors.email.email_actor.email;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.email.email_actor.exception.PartCreatorException;
import info.smart_tools.smartactors.field.field.Field;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MessagePartCreators {
    private static Map<String, MessagePartCreator> creatorsMap = new HashMap<>();
    private static Field partType_Part_F;
    private static Field partText_TextPart_F;
    private static Field partMime_Part_F;
    private static Field sourcePath_FilePart_F;
    private static Field attachmentName_FilePart_F;
    private static IField sourceF;

    private MessagePartCreators() {
    }

    public static void addAllPartsTo(final SMTPMessageAdaptor smtpMessage, final IObject context, final List<IObject> parts) throws Exception {
        for (IObject part : parts) {
            try {
                creatorsMap
                        .get(partType_Part_F.in(part))
                        .addPartTo(smtpMessage, context, part);
            } catch (NullPointerException e) {
                throw new PartCreatorException("Part type not found.", e);
            } catch (ChangeValueException | ReadValueException | MessagingException e) {
                throw new PartCreatorException("Failed to create message part.", e);
            } catch (InvalidArgumentException e) {
                throw new Exception("Failed to extract type", e);
            }
        }
    }

    public static void add(final String name, final MessagePartCreator creator) {
        creatorsMap.put(name, creator);
    }

    static {
        try {
            partType_Part_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "type");
            partText_TextPart_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "text");
            partMime_Part_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "mime");
            sourcePath_FilePart_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "sourceFile");
            attachmentName_FilePart_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "attachmentName");
            sourceF = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "source");
        } catch (ResolutionException e) {
            throw new RuntimeException("Failed to initialize fields", e);
        }

        add("text", (smtpMessage, context, partDescription) -> {
            try {
                MimeBodyPart part = new MimeBodyPart();
                part.setContent(
                        partText_TextPart_F.in(partDescription),
                        partMime_Part_F.in(partDescription));
                smtpMessage.addPart(part);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set part of email.", e);
            }
        });
        add("file", (smtpMessage, context, partDescription) -> {
            try {
                MimeBodyPart part = new MimeBodyPart();

                DataSource dataSource = new FileDataSource(
                        (String) sourcePath_FilePart_F.in(partDescription));

                part.setDataHandler(new DataHandler(dataSource));
                part.setFileName(
                        attachmentName_FilePart_F.in(partDescription));
                smtpMessage.addPart(part);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set file to mail", e);
            }
        });
        add("bytes-array", (smtpMessage, context, partDescription) -> {
            try {
                MimeBodyPart part = new MimeBodyPart();

                DataSource dataSource = new ByteArrayDataSource(
                        sourceF.in(partDescription) instanceof String ?
                                Base64.getDecoder().decode((String) sourceF.in(partDescription)) :
                                (byte[]) sourceF.in(partDescription),
                        partMime_Part_F.in(partDescription)
                );

                part.setDataHandler(new DataHandler(dataSource));
                part.setFileName(
                        attachmentName_FilePart_F.in(partDescription));
                smtpMessage.addPart(part);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set file from byte array to mail", e);
            }
        });
    }
}
