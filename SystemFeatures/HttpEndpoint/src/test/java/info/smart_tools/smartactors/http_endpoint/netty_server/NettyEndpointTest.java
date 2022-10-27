package info.smart_tools.smartactors.http_endpoint.netty_server;

import com.google.common.util.concurrent.Uninterruptibles;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.interfaces.imessage_mapper.IMessageMapper;
import info.smart_tools.smartactors.http_endpoint.netty_client.NettyClient;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
/*
    @Test
    public void whenEndpointReceivesRequest_ItShouldPlaceExchangeObjectIntoMessage_WhichCanBeUsedToSendResponse() throws Exception {
        IMessage stubMessage = IOC.resolve(Keys.getKeyByName(IMessage.class.toString()));
        when(mapperStub.deserialize(any(byte[].class))).thenReturn(stubMessage);
        when(mapperStub.serialize(any(IMessage.class))).thenReturn("response".getBytes());
        stubClient((ctx, msg) -> {
            try {
                String actualResponse = getResponseContent(msg);
                stubMessage.setValue(IOC.resolve(Keys.getKeyByName(IFieldName.class.toString())), actualResponse);
            } catch (ChangeValueException e) {
                fail("Failed to change value in IObject", e);
            } catch (InvalidArgumentException | ResolutionException e) {
                e.printStackTrace();
            }
        });

        TRequest request = createTestRequest();
       /* String actualResponse = receiveActualResponse(stubMessage, request);

        verifyMessageWasReceivedBySystem();
        assertThat(actualResponse).isEqualTo("response");
    }
    */
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
                result = (T) obj.getValue(IOC.resolve(Keys.getKeyByName(IFieldName.class.toString()), key));
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
