package info.smart_tools.smartactors.http_endpoint.strategy.get_cookie_from_request;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.stream.Stream;

/**
 * Rule that extract cookie from request
 */
public class GetCookieFromRequestRule implements IStrategy {

    /**
     * Extracts cookie from given HTTP request.
     *
     * @param args array of needed parameters for resolve dependency,
     *             first parameter the FullHttpRequest, second the cookie name
     * @param <T>  type String
     * @return String the cookie value
     */
    @Override
    public <T> T resolve(final Object... args) {
        String cookieString = ((FullHttpRequest) args[0]).headers().get(HttpHeaders.Names.COOKIE);

        if (cookieString == null) {
            return null;
        }

        String[] cookie = Stream.of(cookieString.split(";"))
                .map(x -> x.split("="))
                .filter(x -> x[0].trim().equals(((String) args[1])))
                .findFirst().orElse(null);

        if (cookie == null || cookie.length == 1) {
            return null;
        }

        return (T) cookie[1];
    }
}
