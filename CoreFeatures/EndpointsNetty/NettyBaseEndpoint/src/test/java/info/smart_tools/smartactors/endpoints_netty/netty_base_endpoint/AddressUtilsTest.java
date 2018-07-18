package info.smart_tools.smartactors.endpoints_netty.netty_base_endpoint;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import org.junit.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import static org.junit.Assert.*;

public class AddressUtilsTest {
    @Test public void Should_parseFullAddress() throws Exception {
        InetSocketAddress address = AddressUtils.parseLocalAddress("10.0.23.143:8093");
        assertEquals(new InetSocketAddress("10.0.23.143", 8093), address);
    }

    @Test public void Should_parsePortOnly() throws Exception {
        InetSocketAddress address = AddressUtils.parseLocalAddress(":8093");
        assertEquals(new InetSocketAddress(8093), address);
    }

    @Test public void Should_parseHostOnly() throws Exception {
        InetSocketAddress address = AddressUtils.parseLocalAddress("10.0.23.143:");
        assertEquals(new InetSocketAddress(InetAddress.getByName("10.0.23.143"), 0), address);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenInputStringContainsNoColon() throws Exception {
        AddressUtils.parseLocalAddress("localhost");
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenInvalidHostNameGiven() throws Exception {
        AddressUtils.parseLocalAddress("invalid.address.smart-tools.info:80");
    }

    @Test public void Should_parseIPv6Addresses() throws Exception {
        assertEquals(
                new InetSocketAddress("::1", 22),
                AddressUtils.parseLocalAddress("::1:22")
        );
    }
}
