package info.smart_tools.smartactors.endpoints_netty.netty_base_endpoint;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Some static helper methods related to socket addresses.
 */
public enum  AddressUtils { ;

    /**
     * Parses local socket address from string in format {@code "host:port"}.
     *
     * <p>
     *  String may contain only port number prefixed with a colon: {@code ":8080"}. Such a address will refer to a
     *  loopback interface.
     * </p>
     *
     * <p>
     *  String may contain only host address followed by a colon: {@code "192.168.1.1:"}. In this case a ephemeral port
     *  will be chosen.
     * </p>
     *
     * <p>
     *  String may contain only a colon character: {@code ":"}. In this case a ephemeral port will be chosen on loopback
     *  interface.
     * </p>
     *
     * @param addressString address string
     * @return parsed address
     * @throws InvalidArgumentException if address is not valid
     */
    public static InetSocketAddress parseLocalAddress(final String addressString)
            throws InvalidArgumentException {
        int colonIdx = addressString.lastIndexOf(':');

        if (colonIdx < 0) {
            throw new InvalidArgumentException("Local address contains no ':' character.");
        }

        String host = addressString.substring(0, colonIdx);
        String portS = addressString.substring(colonIdx + 1);

        int portI = portS.length() == 0 ? 0 : Integer.parseInt(portS);

        if (host.length() == 0) {
            return new InetSocketAddress(portI);
        }

        try {
            return new InetSocketAddress(InetAddress.getByName(host), portI);
        } catch (UnknownHostException e) {
            throw new InvalidArgumentException("Invalid host name.", e);
        }
    }
}
