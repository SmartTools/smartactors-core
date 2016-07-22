import com.google.common.util.concurrent.Uninterruptibles;
import info.smart_tools.smartactors.core.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.imessage.IMessage;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.netty_client.NettyClient;
import info.smart_tools.smartactors.core.netty_server.NettyServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import org.junit.Test;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class NettyEndpointTest<TRequest, TResponse> {
    protected IMessageMapper<byte[]> mapperStub;
    protected NettyServer endpoint;
    protected NettyClient<TRequest> client;
    protected BiConsumer<ChannelHandlerContext, TResponse> handlerStub;
    protected IMessageReceiver receiver;

    protected abstract NettyServer createEndpoint(IMessageReceiver receiver, IMessageMapper<byte[]> mapper);

    protected abstract String getResponseContent(TResponse response);

    protected abstract Class<? extends TResponse> getResponseClass();

    protected abstract TRequest createTestRequest();

    protected int getTestingPort() {
        return 9001;
    }

    @BeforeMethod
    public void setUp() throws ExecutionException, InterruptedException, URISyntaxException {
        mapperStub = mock(IMessageMapper.class);
        receiver = mock(IMessageReceiver.class);
        NettyEndpointTest<TRequest, TResponse> me = this;
        ChannelInboundHandler handler = new SimpleChannelInboundHandler<TResponse>(getResponseClass()) {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, TResponse msg) throws Exception {
                me.handlerStub.accept(ctx, msg);
            }
        };
        endpoint = createEndpoint(receiver, mapperStub);
        endpoint.start();
    }

    @AfterMethod
    public void tearDown() throws ExecutionException, InterruptedException {
        CompletableFuture.allOf(endpoint.stop()).get();
    }

    @Test
    public void whenEndpointReceivesRequest_ItShouldPlaceExchangeObjectIntoMessage_WhichCanBeUsedToSendResponse() throws Exception {
        IMessage stubMessage = IOC.resolve(Keys.getOrAdd(IMessage.class.toString()));
        when(mapperStub.deserialize(any(byte[].class))).thenReturn(stubMessage);
        when(mapperStub.serialize(any(IMessage.class))).thenReturn("response".getBytes());
        stubClient((ctx, msg) -> {
            try {
                String actualResponse = getResponseContent(msg);
                stubMessage.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString())), actualResponse);
            } catch (ChangeValueException e) {
                fail("Failed to change value in IObject", e);
            } catch (InvalidArgumentException | ResolutionException e) {
                e.printStackTrace();
            }
        });

        TRequest request = createTestRequest();
       /* String actualResponse = receiveActualResponse(stubMessage, request);

        verifyMessageWasReceivedBySystem();
        assertThat(actualResponse).isEqualTo("response");*/
    }

    /*protected String receiveActualResponse(IMessage stubMessage, TRequest request) throws InterruptedException, ExecutionException {
        return sendRequest(request)
                .thenApplyAsync(x -> this.<IResponseSender>getWithRetries(stubMessage, "exchange").send(stubMessage))
                .thenApplyAsync(x -> this.<String>getWithRetries(stubMessage, "actualResponse"))
                .get();
    }*/

    protected void verifyMessageWasReceivedBySystem() throws MessageReceiveException, AsynchronousOperationException {
        verify(receiver, times(1)).receive(any(IMessageProcessor.class));
    }

    protected CompletableFuture<Void> sendRequest(TRequest request) {
        return client.send(request);
    }

    protected void stubClient(BiConsumer<ChannelHandlerContext, TResponse> stubFunction) {
        handlerStub = stubFunction;
    }

    protected <T> T getWithRetries(IObject obj, String key) {
        int retries = 10;
        int waitMilliseconds = 50;
        do {
            retries--;
            T result = null;
            try {
                result = (T) obj.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.toString()), key));
            } catch (ReadValueException e) {
                throw new AssertionError("Failed to read value from IObject");
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            } catch (ResolutionException e) {
                e.printStackTrace();
            }
            if (result != null) {
                return result;
            }
            if (retries != 0) {
                Uninterruptibles.sleepUninterruptibly(waitMilliseconds, TimeUnit.MILLISECONDS);
            }
        } while (retries != 0);

        throw new AssertionError("Value in IObject was not presented after 10 retries");
    }
}
