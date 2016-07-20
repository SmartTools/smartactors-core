package info.smart_tools.smartactors.transformation_rules.get_cookie_from_request;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import java.util.stream.Stream;

/**
 * Rule that extract cookie from request
 */
public class GetCookieFromRequestRule implements IResolveDependencyStrategy {

    /**
     * Extracts cookie from given HTTP request.
     * @param args array of needed parameters for resolve dependency,
     *             first parameter the FullHttpRequest, second the cookie name
     * @param <T> type String
     * @return String the cookie value
     */
    @Override
    public <T> T resolve(final Object... args) {
        String cookieString = ((FullHttpRequest) args[0]).headers().get(HttpHeaders.Names.COOKIE);

        String cookie = Stream.of(cookieString.split(";"))
                .map(x -> x.split("="))
                .filter(x -> x[0].equals((String) args[1]))
                .findFirst().get()[1];

        return (T) cookie;
    }
}
