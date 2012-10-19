/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.intalio.tempo.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.servlet.filters.sso.cas.CASFilter;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.util.servlet.filters.DynamicFilterConfig;

/**
 * <a href="CASFilter.java.html"><b><i>View Source</i></b></a>
 *
 * @author Michael Young
 * @author Brian Wing Shun Chan
 * @author Raymond Aug�
 *
 */
public class CASFilter611 extends BaseFilter {

    public static void reload(long companyId) {
        _casFilters.remove(companyId);
    }

    protected Filter getCASFilter(long companyId) throws Exception {
        edu.yale.its.tp.cas.client.filter.CASFilter casFilter =
            _casFilters.get(companyId);

        if (casFilter == null) {
            casFilter = new edu.yale.its.tp.cas.client.filter.CASFilter();

            DynamicFilterConfig config = new DynamicFilterConfig(
                _filterName, _servletContext);

            String serverName = PrefsPropsUtil.getString(
                companyId, PropsKeys.CAS_SERVER_NAME,
                PropsValues.CAS_SERVER_NAME);
            String serviceUrl = PrefsPropsUtil.getString(
                companyId, PropsKeys.CAS_SERVICE_URL,
                PropsValues.CAS_SERVICE_URL);

            config.addInitParameter(
                edu.yale.its.tp.cas.client.filter.CASFilter.LOGIN_INIT_PARAM,
                PrefsPropsUtil.getString(
                    companyId, PropsKeys.CAS_LOGIN_URL,
                    PropsValues.CAS_LOGIN_URL));

            if (Validator.isNotNull(serviceUrl)) {
                config.addInitParameter(
                    edu.yale.its.tp.cas.client.filter.CASFilter.
                        SERVICE_INIT_PARAM,
                    serviceUrl);
            }
            else {
                config.addInitParameter(
                    edu.yale.its.tp.cas.client.filter.CASFilter.
                        SERVERNAME_INIT_PARAM,
                    serverName);
            }

            config.addInitParameter(
                edu.yale.its.tp.cas.client.filter.CASFilter.VALIDATE_INIT_PARAM,
                PrefsPropsUtil.getString(
                    companyId, PropsKeys.AUTH_LOGIN_SITE_URL,
                    PropsValues.AUTH_LOGIN_SITE_URL));

          //Add proxy call back url
            config.addInitParameter(edu.yale.its.tp.cas.client.filter.CASFilter.PROXY_CALLBACK_INIT_PARAM,
                    PrefsPropsUtil.getString(
                            companyId, "cas.proxycallback.url"));
            

            casFilter.init(config);

            _casFilters.put(companyId, casFilter);
        }

        return casFilter;
    }

    protected Log getLog() {
        return _log;
    }

    protected void processFilter(
        HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) {

        try {
            long companyId = PortalUtil.getCompanyId(request);

            if (PrefsPropsUtil.getBoolean(
                    companyId, PropsKeys.CAS_AUTH_ENABLED,
                    PropsValues.CAS_AUTH_ENABLED)) {

                String pathInfo = request.getPathInfo();

                if (pathInfo.indexOf("/portal/logout") != -1) {
                    HttpSession session = request.getSession();

                    session.invalidate();

                    String logoutUrl = PrefsPropsUtil.getString(
                        companyId, PropsKeys.CAS_LOGOUT_URL,
                        PropsValues.CAS_LOGOUT_URL);

                    response.sendRedirect(logoutUrl);
                }
                else {
                    Filter casFilter = getCASFilter(companyId);

                    casFilter.doFilter(request, response, filterChain);
                }
            }
            else {
                processFilter(CASFilter.class, request, response, filterChain);
            }
        }
        catch (Exception e) {
            _log.error(e, e);
        }
    }

    private static Log _log = LogFactoryUtil.getLog(CASFilter.class);

    private static Map<Long, edu.yale.its.tp.cas.client.filter.CASFilter>
        _casFilters = new ConcurrentHashMap
            <Long, edu.yale.its.tp.cas.client.filter.CASFilter>();

    private String _filterName;
    private ServletContext _servletContext;

}