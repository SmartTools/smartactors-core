package info.smart_tools.smartactors.http_endpoint.strategy.get_query_parameter;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

/**
 * Rule for getting query parameters from request
 */
public class GetQueryParameterRule implements IResolveDependencyStrategy {

    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {
            URL url = new URL("http:" + ((FullHttpRequest) args[0]).getUri());
            String query = url.getQuery();

            String param = Stream.of(query.split("&"))
                    .map(x -> x.split("="))
                    .filter(x -> x[0].equals(args[1]))
                    .findFirst().get()[1];

            return (T) param;
        } catch (MalformedURLException e) {
            throw new ResolveDependencyStrategyException("Failed to parse url", e);
        }
    }
}
