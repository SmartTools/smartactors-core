package info.smart_tools.smartactors.core.icookies_extractor;

import info.smart_tools.smartactors.core.icookies_extractor.exceptions.CookieSettingException;
import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * Interface for extracting cookies from environment
 */
public interface ICookiesSetter {
    /**
     * Method for setting cookies from environment
     * @param response Response object in which cookies will add
     * @param environment Environment of the message processor
     * @throws CookieSettingException if there are some problems on setting cookies
     */
    void set(Object response, IObject environment) throws CookieSettingException;
}
