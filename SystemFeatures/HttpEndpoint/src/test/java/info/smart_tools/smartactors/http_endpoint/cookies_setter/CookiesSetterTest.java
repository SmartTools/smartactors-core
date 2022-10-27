package info.smart_tools.smartactors.http_endpoint.cookies_setter;

import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.http_endpoint.interfaces.icookies_extractor.exceptions.CookieSettingException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import io.netty.handler.codec.http.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CookiesSetterTest extends IOCInitializer {

    private IFieldName contextFN;
    private IFieldName endpointNameFN;
    private IFieldName cookieEncoderFN;
    private IFieldName cookiesFN;
    private IFieldName cookieNameFN;
    private IFieldName cookieValueFN;
    private IFieldName cookiePathFN;
    private IFieldName cookieDomainFN;
    private IFieldName cookieSecureFN;
    private IFieldName cookieHttpOnlyFN;
    private IFieldName cookieMaxAgeFN;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        IKey fieldNameKey = Keys.getKeyByName(IFieldName.class.getCanonicalName());
        this.contextFN =        IOC.resolve(fieldNameKey, "context");
        this.endpointNameFN =   IOC.resolve(fieldNameKey, "endpointName");
        this.cookieEncoderFN =  IOC.resolve(fieldNameKey, "encoder");
        this.cookiesFN =        IOC.resolve(fieldNameKey, "cookies");
        this.cookieNameFN =     IOC.resolve(fieldNameKey, "name");
        this.cookieValueFN =    IOC.resolve(fieldNameKey, "value");
        this.cookiePathFN =     IOC.resolve(fieldNameKey, "path");
        this.cookieDomainFN =   IOC.resolve(fieldNameKey, "domain");
        this.cookieSecureFN =   IOC.resolve(fieldNameKey, "secure");
        this.cookieHttpOnlyFN = IOC.resolve(fieldNameKey, "httpOnly");
        this.cookieMaxAgeFN =   IOC.resolve(fieldNameKey, "maxAge");
    }


    @Test
    public void should_SetCookies_WithAllParameters_UseStrictEncoder() throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );

        IObject cookieConfig = new DSObject();
        cookieConfig.setValue(cookieEncoderFN, "strict");

        IObject endpointConfig = new DSObject();
        endpointConfig.setValue(cookiesFN, cookieConfig);

        IOC.register(
                Keys.getKeyByName("http-test_endpoint-config"),
                new SingletonStrategy(endpointConfig)
        );

        IObject context = new DSObject();
        context.setValue(cookiesFN, this.getCookiesWithParameters());

        IObject environment = new DSObject();
        environment.setValue(contextFN, context);
        context.setValue(endpointNameFN, "http-test");

        CookiesSetter setter = new CookiesSetter();
        setter.set(response, environment);

        List<Pattern> expected = new ArrayList<Pattern>() {{
           add(Pattern.compile(
                   "cookie1=value1_1; Max-Age=10; Expires=.*; " +
                           "Path=path1_1; Domain=domain1_1; Secure; HTTPOnly"
           ));
           add(Pattern.compile(
                   "cookie2=value2; Max-Age=20; Expires=.*; " +
                           "Path=path2; Domain=domain2"
           ));
        }};

        this.checkCookies(response, expected);
    }

    @Test
    public void should_SetCookies_WithAllParameters_UseLaxEncoder() throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );

        IObject cookieConfig = new DSObject();
        cookieConfig.setValue(cookieEncoderFN, "lax");

        IObject endpointConfig = new DSObject();
        endpointConfig.setValue(cookiesFN, cookieConfig);

        IOC.register(
                Keys.getKeyByName("http-test_endpoint-config"),
                new SingletonStrategy(endpointConfig)
        );

        IObject context = new DSObject();
        context.setValue(cookiesFN, this.getCookiesWithParameters());
        context.setValue(endpointNameFN, "http-test");

        IObject environment = new DSObject();
        environment.setValue(contextFN, context);

        CookiesSetter setter = new CookiesSetter();
        setter.set(response, environment);

        List<Pattern> expected = new ArrayList<Pattern>() {{
            add(Pattern.compile(
                    "cookie1=value1; Max-Age=0; Expires=.*; " +
                            "Path=path1; Domain=domain1; HTTPOnly"
            ));
            add(Pattern.compile(
                    "cookie1=value1_1; Max-Age=10; Expires=.*; " +
                            "Path=path1_1; Domain=domain1_1; Secure; HTTPOnly"
            ));
            add(Pattern.compile(
                    "cookie2=value2; Max-Age=20; Expires=.*; " +
                            "Path=path2; Domain=domain2"
            ));
        }};

        this.checkCookies(response, expected);
    }

    @Test
    public void should_SetCookies_WithDefaultParameters_UseDefaultEncoder() throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );

        IObject context = new DSObject();
        context.setValue(cookiesFN, this.getCookiesWithoutParameters());

        IObject environment = new DSObject();
        environment.setValue(contextFN, context);

        CookiesSetter setter = new CookiesSetter();
        setter.set(response, environment);

        List<Pattern> expected = new ArrayList<Pattern>() {{
            add(Pattern.compile("cookie1=value1_1"));
            add(Pattern.compile("cookie2=value2"));
        }};

        this.checkCookies(response, expected);
    }

    @Test
    public void should_SetCookies_WithConfigParameters_UseDefaultEncoder() throws Exception {
        final FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );

        final IObject cookiesConfig = new DSObject();
        cookiesConfig.setValue(cookieDomainFN, "example.com");
        cookiesConfig.setValue(cookiePathFN, "/");
        cookiesConfig.setValue(cookieMaxAgeFN, 100);
        cookiesConfig.setValue(cookieSecureFN, true);
        cookiesConfig.setValue(cookieHttpOnlyFN, true);

        final IObject endpointConfig = new DSObject();
        endpointConfig.setValue(cookiesFN, cookiesConfig);

        IOC.register(
                Keys.getKeyByName("http-test_endpoint-config"),
                new SingletonStrategy(endpointConfig)
        );

        final List<IObject> cookies = new ArrayList<IObject>() {{
            addAll(getCookiesWithoutParameters());
            add(createCookie("cookie3", "value3", "path3", "domain3", 10L, false, false));
        }};

        final IObject context = new DSObject();
        context.setValue(cookiesFN, cookies);
        context.setValue(endpointNameFN, "http-test");

        final IObject environment = new DSObject();
        environment.setValue(contextFN, context);

        final CookiesSetter setter = new CookiesSetter();

        setter.set(response, environment);

        final List<Pattern> expected = new ArrayList<Pattern>() {{
            add(Pattern.compile(
                    "cookie1=value1_1; Max-Age=100; Expires=.*; " +
                            "Path=/; Domain=example.com; Secure; HTTPOnly"
            ));
            add(Pattern.compile(
                    "cookie2=value2; Max-Age=100; Expires=.*; " +
                            "Path=/; Domain=example.com; Secure; HTTPOnly"
            ));
            add(Pattern.compile(
                    "cookie3=value3; Max-Age=10; Expires=.*; Path=path3; Domain=domain3"
            ));
        }};

        this.checkCookies(response, expected);
    }

    @Test
    public void should_SetCookie_With2EndpointsAndConfigParameters() throws Exception {
        final FullHttpResponse response1 = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );
        final FullHttpResponse response2 = response1.copy();

        final List<IObject> cookies = new ArrayList<IObject>() {{
            addAll(getCookiesWithoutParameters());
            add(createCookie("cookie3", "value3", "path3", "domain3", 1L, false, false));
        }};

        final IObject cookiesConfig1 = new DSObject();
        cookiesConfig1.setValue(cookieEncoderFN, "strict");
        cookiesConfig1.setValue(cookieDomainFN, "1.example.com");
        cookiesConfig1.setValue(cookiePathFN, "/1/");
        cookiesConfig1.setValue(cookieMaxAgeFN, 10);
        cookiesConfig1.setValue(cookieSecureFN, false);
        cookiesConfig1.setValue(cookieHttpOnlyFN, true);

        final IObject cookiesConfig2 = new DSObject();
        cookiesConfig2.setValue(cookieEncoderFN, "lax");
        cookiesConfig2.setValue(cookieDomainFN, "2.example.com");
        cookiesConfig2.setValue(cookiePathFN, "/2/");
        cookiesConfig2.setValue(cookieMaxAgeFN, 20);
        cookiesConfig2.setValue(cookieSecureFN, true);
        cookiesConfig2.setValue(cookieHttpOnlyFN, false);

        final IObject endpointConfig1 = new DSObject();
        endpointConfig1.setValue(cookiesFN, cookiesConfig1);

        final IObject endpointConfig2 = new DSObject();
        endpointConfig2.setValue(cookiesFN, cookiesConfig2);

        IOC.register(
                Keys.getKeyByName("http-test-1_endpoint-config"),
                new SingletonStrategy(endpointConfig1)
        );

        IOC.register(
                Keys.getKeyByName("http-test-2_endpoint-config"),
                new SingletonStrategy(endpointConfig2)
        );

        final IObject context1 = new DSObject();
        context1.setValue(cookiesFN, cookies);
        context1.setValue(endpointNameFN, "http-test-1");

        final IObject context2 = new DSObject();
        context2.setValue(cookiesFN, cookies);
        context2.setValue(endpointNameFN, "http-test-2");

        final IObject environment1 = new DSObject();
        environment1.setValue(contextFN, context1);

        final IObject environment2 = new DSObject();
        environment2.setValue(contextFN, context2);

        final CookiesSetter setter = new CookiesSetter();

        setter.set(response1, environment1);

        final List<Pattern> expected1 = new ArrayList<Pattern>() {{
            add(Pattern.compile(
                    "cookie1=value1_1; Max-Age=10; Expires=.*; " +
                            "Path=/1/; Domain=1.example.com; HTTPOnly"
            ));
            add(Pattern.compile(
                    "cookie2=value2; Max-Age=10; Expires=.*; " +
                            "Path=/1/; Domain=1.example.com; HTTPOnly"
            ));
            add(Pattern.compile(
                    "cookie3=value3; Max-Age=1; Expires=.*; Path=path3; Domain=domain3"
            ));
        }};

        this.checkCookies(response1, expected1);

        setter.set(response2, environment2);

        final List<Pattern> expected2 = new ArrayList<Pattern>() {{
            add(Pattern.compile(
                    "cookie1=value1; Max-Age=20; Expires=.*; " +
                            "Path=/2/; Domain=2.example.com; Secure"
            ));
            add(Pattern.compile(
                    "cookie1=value1_1; Max-Age=20; Expires=.*; " +
                            "Path=/2/; Domain=2.example.com; Secure"
            ));
            add(Pattern.compile(
                    "cookie2=value2; Max-Age=20; Expires=.*; " +
                            "Path=/2/; Domain=2.example.com; Secure"
            ));
            add(Pattern.compile(
                    "cookie3=value3; Max-Age=1; Expires=.*; Path=path3; Domain=domain3"
            ));
        }};

        this.checkCookies(response2, expected2);
    }

    @Test(expected = CookieSettingException.class)
    public void should_ThrowException_ResponseIsNull() throws Exception {

        CookiesSetter setter = new CookiesSetter();
        setter.set(null, new DSObject());
    }

    @Test(expected = CookieSettingException.class)
    public void should_ThrowException_EnvironmentIsNull()
            throws ResolutionException, CookieSettingException {

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );

        CookiesSetter setter = new CookiesSetter();
        setter.set(response, null);
    }

    @Test(expected = CookieSettingException.class)
    public void should_ThrowException_ContextIsNull() throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );

        IObject environment = new DSObject();
        environment.setValue(contextFN, null);

        CookiesSetter setter = new CookiesSetter();
        setter.set(response, environment);
    }

    @Test
    public void should_DoNothing_CookiesIsEmpty() throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );

        IObject context = new DSObject();
        context.setValue(cookiesFN, Collections.emptyList());

        IObject environment = new DSObject();
        environment.setValue(contextFN, context);

        CookiesSetter setter = new CookiesSetter();
        setter.set(response, environment);

        checkCookies(response, Collections.emptyList());
    }

    @Test(expected = CookieSettingException.class)
    public void should_ThrowException_UnsupportedCookieEncoder() throws Exception {
        final FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );

        final IObject cookiesConfig = new DSObject();
        cookiesConfig.setValue(cookieEncoderFN, "UnsupportedCookieEncoder");
        cookiesConfig.setValue(cookieDomainFN, "example.com");
        cookiesConfig.setValue(cookiePathFN, "/");
        cookiesConfig.setValue(cookieMaxAgeFN, 100);
        cookiesConfig.setValue(cookieSecureFN, true);
        cookiesConfig.setValue(cookieHttpOnlyFN, true);

        final IObject endpointConfig = new DSObject();
        endpointConfig.setValue(cookiesFN, cookiesConfig);

        IOC.register(
                Keys.getKeyByName("http-test_endpoint-config"),
                new SingletonStrategy(endpointConfig)
        );

        final List<IObject> cookies = new ArrayList<IObject>() {{
            addAll(getCookiesWithoutParameters());
            add(createCookie("cookie3", "value3", "path3", "domain3", 10L, false, false));
        }};

        final IObject context = new DSObject();
        context.setValue(cookiesFN, cookies);
        context.setValue(endpointNameFN, "http-test");

        final IObject environment = new DSObject();
        environment.setValue(contextFN, context);

        final CookiesSetter setter = new CookiesSetter();

        setter.set(response, environment);
    }

    @Test(expected = CookieSettingException.class)
    public void should_ThrowException_EmptyCookieName() throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );

        List<IObject> cookies = Collections.singletonList(
                this.createCookie(null, "value1", "path1", "domain1", 10L, true, true)
        );

        IObject context = new DSObject();
        context.setValue(cookiesFN, cookies);

        IObject environment = new DSObject();
        environment.setValue(contextFN, context);

        CookiesSetter setter = new CookiesSetter();
        setter.set(response, environment);
    }


    private List<IObject> getCookiesWithParameters() throws Exception {

        IObject cookie1 = this.createCookie("cookie1", "value1", "path1", "domain1", 0L, false, true);
        IObject cookie1_1 = this.createCookie("cookie1", "value1_1", "path1_1", "domain1_1", 10L, true, true);
        IObject cookie2 = this.createCookie("cookie2", "value2", "path2", "domain2", 20L, false, false);

        return new ArrayList<IObject>() {{
            add(cookie1);
            add(cookie1_1);
            add(cookie2);
        }};
    }

    private List<IObject> getCookiesWithoutParameters() throws Exception {

        IObject cookie1 = this.createCookie("cookie1", "value1", null, null, null, null, null);
        IObject cookie1_1 = this.createCookie("cookie1", "value1_1", null, null, null, null, null);
        IObject cookie2 = this.createCookie("cookie2", "value2", null, null, null, null, null);

        return new ArrayList<IObject>() {{
            add(cookie1);
            add(cookie1_1);
            add(cookie2);
        }};
    }

    private IObject createCookie(
            String name,
            String value,
            String path,
            String domain,
            Long maxAge,
            Boolean secure,
            Boolean httpOnly
    ) throws Exception {

        IObject cookie = new DSObject();
        cookie.setValue(cookieNameFN, name);
        cookie.setValue(cookieValueFN, value);
        cookie.setValue(cookiePathFN, path);
        cookie.setValue(cookieDomainFN, domain);
        cookie.setValue(cookieMaxAgeFN, maxAge);
        cookie.setValue(cookieSecureFN, secure);
        cookie.setValue(cookieHttpOnlyFN, httpOnly);

        return cookie;
    }

    private void checkCookies(HttpResponse response, List<Pattern> expected) {
        final List<String> actual = response.headers().getAll(HttpHeaderNames.SET_COOKIE);
        final int setCookiesSize = actual.size();

        Assert.assertEquals(
                "Actual count of cookies differ from expected",
                expected.size(),
                setCookiesSize
        );

        for (int i = 0; i < setCookiesSize; i++) {
            Pattern validPattern = expected.get(i);
            Assert.assertTrue(
                    "Actual cookies differ from expected",
                    validPattern.matcher(actual.get(i)).matches()
            );
        }
    }
}
