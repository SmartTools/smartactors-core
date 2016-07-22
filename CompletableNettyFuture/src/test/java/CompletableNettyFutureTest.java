import info.smart_tools.smartactors.core.CompletableNettyFuture;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;


public class CompletableNettyFutureTest {
    private static LocalEventLoopGroup group;
    private static EventExecutor executor;

    @BeforeClass
    public static void setUp() {
        group = new LocalEventLoopGroup(1);
        executor = group.next();
    }

    @AfterClass
    public static void tearDown() {
        group.shutdownGracefully().getNow();
    }


    @Test
    public void whenWrappedFutureIsAlreadySucceed_ShouldReturnCompletedFuture() throws ExecutionException, InterruptedException {
        Future<String> nettyFuture = executor.newSucceededFuture("test");

        CompletableFuture<String> future = CompletableNettyFuture.from(nettyFuture);

        assertThat(future.isDone()).isTrue();
        assertThat(future.isCancelled()).isFalse();
        assertThat(future.get()).isEqualTo("test");
    }

    @Test
    public void whenWrappedFutureIsAlreadyFailed_ShouldReturnFailedFuture() throws InterruptedException {
        Exception expectedException = new Exception("exception");
        Future<String> nettyFuture = executor.newFailedFuture(expectedException);

        CompletableFuture<String> future = CompletableNettyFuture.from(nettyFuture);

        assertThat(future.isDone()).isTrue();
        assertThat(future.isCancelled()).isFalse();
        assertThat(future.isCompletedExceptionally()).isTrue();
        try {
            future.get();
        } catch (ExecutionException e) {
            assertThat(e).hasCause(expectedException);
        }
    }

    @Test
    public void whenWrappedFutureIsNotCompletedYet_ShouldCompleteAfterIt() throws ExecutionException, InterruptedException {
        Promise<String> promise = executor.newPromise();

        CompletableFuture<String> future = CompletableNettyFuture.from(promise);
        assertThat(future.isDone()).isFalse();

        promise.setSuccess("test");

        assertThat(future.isDone()).isTrue();
        assertThat(future.get()).isEqualTo("test");
    }

    @Test
    public void whenWrappedFutureCanceled_ShouldBeCanceledToo() {
        Promise<String> promise = executor.newPromise();

        CompletableFuture<String> future = CompletableNettyFuture.from(promise);
        assertThat(future.isCancelled()).isFalse();

        promise.cancel(true);

        assertThat(future.isDone()).isTrue();
        assertThat(future.isCancelled()).isTrue();
    }

    @Test
    public void cancel_ShouldCancelWrappedFuture() {
        Promise<String> promise = executor.newPromise();

        CompletableFuture<String> future = CompletableNettyFuture.from(promise);

        future.cancel(true);

        assertThat(promise.isCancelled()).isTrue();
    }
}

