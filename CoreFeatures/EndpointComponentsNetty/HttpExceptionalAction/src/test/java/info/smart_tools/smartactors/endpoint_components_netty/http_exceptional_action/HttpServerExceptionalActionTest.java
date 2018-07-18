package info.smart_tools.smartactors.endpoint_components_netty.http_exceptional_action;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpServerExceptionalActionTest {
    @Test public void Should_sendResponseWithCode500AndCloseConnection() throws Exception {
        Channel channelMock = mock(Channel.class);
        ChannelFuture channelFutureMock = mock(ChannelFuture.class);
        when(channelMock.writeAndFlush(any())).thenReturn(channelFutureMock);

        ArgumentCaptor<Object> argCaptor = ArgumentCaptor.forClass(Object.class);

        new HttpServerExceptionalAction(HttpResponseStatus.INTERNAL_SERVER_ERROR).execute(channelMock, new Throwable());

        verify(channelMock).writeAndFlush(argCaptor.capture());

        assertTrue(argCaptor.getValue() instanceof HttpResponse);
        assertEquals(500, ((HttpResponse) argCaptor.getValue()).status().code());

        verify(channelFutureMock).addListener(eq(ChannelFutureListener.CLOSE));
    }
}
