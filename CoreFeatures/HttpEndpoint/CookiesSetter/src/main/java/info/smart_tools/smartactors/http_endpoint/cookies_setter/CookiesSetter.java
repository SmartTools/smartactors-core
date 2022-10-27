package info.smart_tools.smartactors.http_endpoint.cookies_setter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.http_endpoint.interfaces.icookies_extractor.ICookiesSetter;
import info.smart_tools.smartactors.http_endpoint.interfaces.icookies_extractor.exceptions.CookieSettingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.util.*;

/**
 * Cookies setter for {@link FullHttpResponse}
 * This implementation extract cookies from context of the environment and set them into response
 */
public class CookiesSetter implements ICookiesSetter {

// Fields ------------------------------------------------------------------------------------------------------------

    /**
     * Common error message template
     */
    private static final String COMMON_ERROR_MSG = "Failed to set cookie: %s";

    /**
     * {@code Strict} encoder that validates that name and value chars are in the valid scope
     * defined in RFC6265, and (for methods that accept multiple cookies) that only
     * one cookie is encoded with any given name. (If multiple cookies have the same
     * name, the last one is the one that is encoded.)
     */
    private static final ServerCookieEncoder STRICT_COOKIE_ENCODER = ServerCookieEncoder.STRICT;

    /**
     * {@code Lax} encoder that doesn't validate name and value, and that allows multiple
     * cookies with the same name
     */
    private static final ServerCookieEncoder LAX_COOKIE_ENCODER = ServerCookieEncoder.LAX;

    /**
     * Default cookie's parameters
     */
    private static final String DEFAULT_COOKIE_PATH = "";
    private static final String DEFAULT_COOKIE_DOMAIN = "";
    private static final Boolean DEFAULT_COOKIE_SECURE = false;
    private static final Boolean DEFAULT_COOKIE_HTTP_ONLY = false;
    private static final Long DEFAULT_COOKIE_MAX_AGE = Long.MIN_VALUE;

    /**
     * Field names to getting cookie parameters from {@link IObject}
     */
    private final IFieldName contextFN;
    private final IFieldName endpointNameFN;
    private final IFieldName cookieEncoderFN;
    private final IFieldName cookiesFN;
    private final IFieldName cookieNameFN;
    private final IFieldName cookieValueFN;
    private final IFieldName cookiePathFN;
    private final IFieldName cookieDomainFN;
    private final IFieldName cookieSecureFN;
    private final IFieldName cookieHttpOnlyFN;
    private final IFieldName cookieMaxAgeFN;

    /**
     * Empty cookie config
     */
    private final IObject emptyCookieConfig;

    private final Map<String, ServerCookieEncoder> encoders = new HashMap<String, ServerCookieEncoder>() {{
       put("STRICT", STRICT_COOKIE_ENCODER);
       put("LAX", LAX_COOKIE_ENCODER);
    }};

    /**
     * Default cookie encoder
     */
    private final String defaultCookieEncoder = "STRICT";

// Constructors ------------------------------------------------------------------------------------------------------

    /**
     * Default constructor of {@link CookiesSetter}
     *
     * @throws ResolutionException When occur field name resolution error
     */
    public CookiesSetter() throws ResolutionException {
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

        this.emptyCookieConfig = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()));
    }

// Methods -----------------------------------------------------------------------------------------------------------

    /**
     * Set cookie to {@param response} use parameters from
     *              environment context(more priority) or endpoint config(cookies section, low priority).
     * <p>Cookies should presents as {@link List<IObject>}</p>
     * <pre>
     *      "environment": {
     *          "context": {
     *              "cookies": [
     *                  {
     *                      "name": "nameOfTheCookie",
     *                      "value": "valueOfTheCookie",
     *                      "path": "/",
     *                      "domain": "example.com",
     *                      "maxAge": "10",
     *                      "secure": true,
     *                      "httpOnly": true
     *                  },
     *                  ...
     *              ],
     *              ...
     *          },
     *          ...
     *      }
     * </pre>
     *
     * <p>{@code name} -     cookie name</p>
     * <p>{@code value} -    cookie value</p>
     * <p>{@code path} -     cookie path</p>
     * <p>{@code domain} -   cookie domain</p>
     * <p>{@code maxAge} -   maximum age of cookie in seconds
     *                       If an age of {@code 0} is specified, cookie will be
     *                       automatically removed by browser because it will expire immediately.
     *                       If {@link Long#MIN_VALUE} is specified, cookie will be removed when the
     *                       browser is closed.</p>
     * <p>{@code secure} -   cookie's secure flag - {@code true} if cookie is to be secure, otherwise {@code false}</p>
     * <p>{@code httpOnly} - cookie's HttpOnly flag - {@code true} if cookie is HTTP-only or {@code false} if it isn't.
     *                       Determines if cookie is HTTP only. If set to true, cookie cannot be accessed by a client
     *                       side script. However, this works only if the browser supports it.
     *                       For for information, please look
     *                       <a href="http://www.owasp.org/index.php/HTTPOnly">here</a></p>
     *
     *
     * @param response    Response object in which cookies will add
     * @param environment Environment of the message processor
     *
     * @throws CookieSettingException When occur cookie setting error
     */
    @Override
    public void set(final Object response, final IObject environment) throws CookieSettingException {
        try {
            final HttpResponse httpResponse =   this.getHttpResponse(response);
            final IObject context =             this.getContext(environment);
            final IObject cookieConfig =        this.getCookieConfig(context);
            final List<IObject> cookiesParams = this.getCookiesParams(context);

            if (cookiesParams == null || cookiesParams.isEmpty()) {
                return;
            }

            final List<Cookie> cookies = new ArrayList<>();

            for (IObject cookieParams: cookiesParams) {
                cookies.add(this.createCookie(cookieParams, cookieConfig));
            }

            httpResponse.headers().set(
                    HttpHeaderNames.SET_COOKIE,
                    this.getCookieEncoder(cookieConfig).encode(cookies)
            );
        } catch (ReadValueException | InvalidArgumentException exc) {
            throw this.getError(exc.getMessage(), exc);
        }
    }


    private HttpResponse getHttpResponse(final Object response) throws CookieSettingException {

        if (response == null || !HttpResponse.class.isAssignableFrom(response.getClass())) {
            throw this.getError("Invalid response", null);
        }
        return (HttpResponse) response;
    }

    private IObject getContext(final IObject environment)
            throws CookieSettingException, ReadValueException, InvalidArgumentException {

        if (environment == null) {
            throw this.getError("Invalid environment(NULL)", null);
        }
        try {
            return (IObject) environment.getValue(contextFN);
        } catch (ClassCastException exc) {
            throw this.getError("Invalid context", exc);
        }
    }

    private IObject getCookieConfig(final IObject context)
            throws CookieSettingException, ReadValueException, InvalidArgumentException {

        if (context == null) {
            throw this.getError("Invalid context(NULL)", null);
        }
        try {
            String endpointName = String.valueOf(context.getValue(endpointNameFN));
            IObject endpointConfig = IOC.resolve(Keys.getKeyByName(endpointName + "_endpoint-config"));
            IObject cookieConfig = (IObject) endpointConfig.getValue(cookiesFN);

            return  (cookieConfig != null) ? cookieConfig : emptyCookieConfig;
        } catch (ResolutionException exc) {
            return emptyCookieConfig;
        }
    }

    @SuppressWarnings("unchecked")
    private List<IObject> getCookiesParams(final IObject context)
            throws CookieSettingException, ReadValueException, InvalidArgumentException {

        if (context == null) {
            throw this.getError("Invalid context(NULL)", null);
        }
        try {
            return (List<IObject>) context.getValue(cookiesFN);
        } catch (ClassCastException exc) {
            throw this.getError("Invalid cookie parameters", exc);
        }
    }

    private ServerCookieEncoder getCookieEncoder(final IObject config)
            throws CookieSettingException, ReadValueException, InvalidArgumentException {

        String encoderType = Optional
                .ofNullable(config.getValue(cookieEncoderFN))
                .orElse(defaultCookieEncoder)
                .toString()
                .toUpperCase()
                .trim();

        return Optional
                .ofNullable(encoders.get(encoderType))
                .orElseThrow(() -> this.getError(
                        String.format("Unsupported cookie encoder(%s)", encoderType),
                        null
                ));
    }

    private Cookie createCookie(final IObject params, IObject config)
            throws CookieSettingException, ReadValueException, InvalidArgumentException {

        String cookieName = Optional
                .ofNullable(params.getValue(cookieNameFN))
                .orElseThrow(() -> this.getError("Empty cookie name", null))
                .toString();

        String cookieValue = Optional
                .ofNullable(params.getValue(cookieValueFN))
                .orElse("")
                .toString();

        String cookiePath = String.valueOf(this.getFirstNotNull(
                Optional.ofNullable(params.getValue(cookiePathFN)),
                Optional.ofNullable(config.getValue(cookiePathFN)),
                Optional.of(DEFAULT_COOKIE_PATH)
        ));
        String cookieDomain = String.valueOf(this.getFirstNotNull(
                Optional.ofNullable(params.getValue(cookieDomainFN)),
                Optional.ofNullable(config.getValue(cookieDomainFN)),
                Optional.of(DEFAULT_COOKIE_DOMAIN)
        ));
        Boolean cookieSecure = (Boolean) this.getFirstNotNull(
                Optional.ofNullable(params.getValue(cookieSecureFN)),
                Optional.ofNullable(config.getValue(cookieSecureFN)),
                Optional.of(DEFAULT_COOKIE_SECURE)
        );
        Boolean cookieHttpOnly = (Boolean) this.getFirstNotNull(
                Optional.ofNullable(params.getValue(cookieHttpOnlyFN)),
                Optional.ofNullable(config.getValue(cookieHttpOnlyFN)),
                Optional.of(DEFAULT_COOKIE_HTTP_ONLY)
        );
        Number cookieMaxAge = (Number) this.getFirstNotNull(
                Optional.ofNullable(params.getValue(cookieMaxAgeFN)),
                Optional.ofNullable(config.getValue(cookieMaxAgeFN)),
                Optional.of(DEFAULT_COOKIE_MAX_AGE)
        );

        Cookie cookie = new DefaultCookie(cookieName, cookieValue);
        cookie.setPath(cookiePath);
        cookie.setDomain(cookieDomain);
        cookie.setSecure(cookieSecure);
        cookie.setHttpOnly(cookieHttpOnly);
        cookie.setMaxAge(cookieMaxAge.longValue());

        return cookie;
    }

    private Object getFirstNotNull(Optional... optionals) {
        for (Optional optional: optionals) {
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        // Should never happen
        throw new IllegalArgumentException("Value not found");
    }

    private CookieSettingException getError(String message, Throwable cause) {
        String errMsg = String.format(COMMON_ERROR_MSG, message);
        return new CookieSettingException(errMsg, cause);
    }
}