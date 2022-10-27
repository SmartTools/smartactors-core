package info.smart_tools.smartactors.email.email_actor;


import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.email.email_actor.email.MessageAttributeSetters;
import info.smart_tools.smartactors.email.email_actor.email.MessagePartCreators;
import info.smart_tools.smartactors.email.email_actor.email.SMTPMessageAdaptor;
import info.smart_tools.smartactors.email.email_actor.exception.MailingActorException;
import info.smart_tools.smartactors.email.email_actor.exception.SendFailureException;
import info.smart_tools.smartactors.email.email_actor.wrapper.MailingMessage;
import info.smart_tools.smartactors.field.field.Field;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import me.normanmaurer.niosmtp.delivery.Authentication;
import me.normanmaurer.niosmtp.delivery.DeliveryRecipientStatus;
import me.normanmaurer.niosmtp.delivery.SMTPDeliveryAgent;
import me.normanmaurer.niosmtp.delivery.SMTPDeliveryEnvelope;
import me.normanmaurer.niosmtp.delivery.impl.AuthenticationImpl;
import me.normanmaurer.niosmtp.delivery.impl.SMTPDeliveryAgentConfigImpl;
import me.normanmaurer.niosmtp.delivery.impl.SMTPDeliveryEnvelopeImpl;
import me.normanmaurer.niosmtp.transport.FutureResult;
import me.normanmaurer.niosmtp.transport.SMTPClientTransport;
import me.normanmaurer.niosmtp.transport.netty.NettyLMTPClientTransportFactory;
import me.normanmaurer.niosmtp.transport.netty.NettySMTPClientTransportFactory;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

/**
 * Actor for sending emails
 */
public class MailingActor {
    private SMTPDeliveryAgent deliveryAgent;
    private SMTPDeliveryAgentConfigImpl deliveryAgentConfig = new SMTPDeliveryAgentConfigImpl();
    private InetSocketAddress serverHost;
    private URI serverUri;
    private IObject mailingContext;

    private static Field serverURI_ActorParams_F;
    private static Field senderAddress_ActorParams_F;
    private static Field userName_ActorParams_F;
    private static Field password_ActorParams_F;
    private static Field authenticationMode_ActorParams_F;
    private static Field SSLProtocol_ActorParams_F;
    private static Field senderAddress_Context_F;
    private static IField recipientF;
    private static IField typeF;


    // Functions creating client transport, depending on server URI scheme
    private static Map<String, Function<IObject, SMTPClientTransport>> transportCreators
            = new HashMap<String, Function<IObject, SMTPClientTransport>>() {{
        put("smtp", params -> NettySMTPClientTransportFactory.createNio().createPlain());
        put("smtps", params -> NettySMTPClientTransportFactory.createNio().createSMTPS(createSSLContext(params)));
        put("lmtp", params -> NettyLMTPClientTransportFactory.createNio().createPlain());
    }};

    private static Map<String, Message.RecipientType> recipientTypeMap
            = new HashMap<String, Message.RecipientType>() {{
        put("To", Message.RecipientType.TO);
        put("Cc", Message.RecipientType.CC);
        put("Bcc", Message.RecipientType.BCC);
    }};

    /**
     * Constructor.
     *
     * @param params actor parameters. Expected following fields:
     *      <ul>
     *               <li>"username" - username used for authentication on SMTP server.</li>
     *               <li>"password" - password used for authentication on SMTP server.</li>
     *               <li>"authenticationMode" - use "Login" for login/password authentication.</li>
     *               <li>"server" - URI of SMTP/LMTP server. Supported schemes are: "smtp", "smtps" and "lmtp".</li>
     *               <li>"sslProtocol" - name of SSL protocol for SMTPS transport.</li>
     *               <li>"senderAddress" - e-mail address used to send mail from.</li>
     *      </ul>
     */
    public MailingActor(final IObject params) throws MailingActorException {
        try {
            //Fields initialize
            serverURI_ActorParams_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "server");
            senderAddress_Context_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "senderAddress");
            senderAddress_ActorParams_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "senderAddress");
            userName_ActorParams_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "username");
            password_ActorParams_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "password");
            authenticationMode_ActorParams_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "authenticationMode");
            SSLProtocol_ActorParams_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "sslProtocol");
            senderAddress_Context_F = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "senderAddress");
            recipientF = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "recipient");
            typeF = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "type");

            mailingContext = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

            serverUri = new URI(serverURI_ActorParams_F.in(params, String.class));
            serverHost = new InetSocketAddress(serverUri.getHost(), serverUri.getPort());

            senderAddress_Context_F.out(
                    mailingContext,
                    senderAddress_ActorParams_F.in(params, String.class));

            deliveryAgent = createAgent(params);

            String authenticationMode = authenticationMode_ActorParams_F.in(params, String.class);
            if (!authenticationMode.equals("None")) {
                Authentication authentication = new AuthenticationImpl(
                        userName_ActorParams_F.in(params, String.class),
                        password_ActorParams_F.in(params, String.class),
                        Authentication.AuthMode.valueOf(authenticationMode));
                deliveryAgentConfig.setAuthentication(authentication);
            }
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new MailingActorException("Params object is not correct", e);
        } catch (URISyntaxException e) {
            throw new MailingActorException("Failed to create URI", e);
        } catch (ResolutionException e) {
            throw new MailingActorException("Failed to resolve fields", e);
        }
    }

    /**
     * Creates SSL context for encrypted client transport
     *
     * @param actorParams parameters of mailing actor. Expected all fields of actor's constructor parameters related to
     *                    creation of SSL context (for now only "sslProtocol").
     * @return created SSL context
     */
    private static SSLContext createSSLContext(final IObject actorParams) {
        try {
            SSLContext sslContext = SSLContext.getInstance(SSLProtocol_ActorParams_F.in(actorParams, String.class));
            sslContext.init(null, null, null);
            return sslContext;
        } catch (ReadValueException | NoSuchAlgorithmException | KeyManagementException | InvalidArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates delivery agent with transport type specified by server URI scheme.
     *
     * @param actorParams parameters of mailing actor
     * @return created delivery agent
     */
    private SMTPDeliveryAgent createAgent(final IObject actorParams) {
        return new SMTPDeliveryAgent(transportCreators.get(serverUri.getScheme()).apply(actorParams));
    }

    /**
     * Handler for sending emails
     * @param message the wrapper for message
     */
    public void sendMailHandler(final MailingMessage message) {
        try {
            List<IObject> recipients = message.getSendToMessage();
            SMTPMessageAdaptor smtpMessage = new SMTPMessageAdaptor(SMTPMessageAdaptor.createMimeMessage());

            setMessageAttributes(
                    smtpMessage,
                    message.getMessageAttributesMessage(),
                    recipients);

            MessagePartCreators.addAllPartsTo(
                    smtpMessage,
                    mailingContext,
                    message.getMessagePartsMessage()
            );

            List<String> recipientList = new ArrayList<>();
            for (IObject recipient : recipients) {
                recipientList.add(recipientF.in(recipient));
            }

            SMTPDeliveryEnvelope deliveryEnvelope = new SMTPDeliveryEnvelopeImpl(
                    senderAddress_Context_F.in(mailingContext, String.class), recipientList, smtpMessage);

            // Bug fix: can't find handler of MIME type of data
            // in javax.mail.internet.MimeMultipart#writeTo(java.io.OutputStream).
            // JavaMail depends on some configuration files to map MIME types to Java classes
            // (e.g., "maultipart/mixed" to "javax.mail.internet.MimeMultipart").
            // These configuration files are loaded using the ClassLoader for the application.
            // If the ClassLoader doesn't function properly, these configuration files won't be found.
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            Collection<FutureResult<Iterator<DeliveryRecipientStatus>>> cfr = deliveryAgent.deliver(serverHost, deliveryAgentConfig, deliveryEnvelope).get();

            final SendFailureException exception = new SendFailureException();
            for (FutureResult<Iterator<DeliveryRecipientStatus>> fr : cfr) {
                if (!fr.isSuccess()) {
                    exception.addSuppressed(fr.getException());
                    fr.getResult().forEachRemaining(status -> exception.addEmail(status.getAddress()));
                }
            }

            if (!exception.getEmails().isEmpty() || !(exception.getSuppressed().length == 0)) {
                throw exception;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setMessageAttributes(final SMTPMessageAdaptor smtpMessage, final IObject attributes, final List<IObject> recipients)
            throws Exception {
        smtpMessage.getMimeMessage().setFrom(
                new InternetAddress(senderAddress_Context_F.in(mailingContext, String.class)));
        for (IObject recipient : recipients) {
            smtpMessage.getMimeMessage().addRecipient(recipientTypeMap.get(typeF.in(recipient)), new InternetAddress(recipientF.in(recipient)));
        }

        MessageAttributeSetters.applyAll(
                attributes,
                mailingContext, smtpMessage);
    }
}
