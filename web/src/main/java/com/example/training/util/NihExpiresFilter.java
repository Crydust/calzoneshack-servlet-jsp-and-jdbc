package com.example.training.util;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Add cache related headers to each response.
 *
 * <p>
 * Headers:</p>
 * <ul>
 * <li>Cache-Control: no-cache, no-store, must-revalidate</li>
 * <li>Pragma: no-cache</li>
 * <li>Expires: Thu, 01 Jan 1970 00:00:00 GMT</li>
 *
 * </ul>
 *
 * <p>
 * Should be replaced by
 * <a href="http://tomcat.apache.org/tomcat-8.5-doc/config/filter.html#Expires_Filter">org.apache.catalina.filters.ExpiresFilter</a>.</p>
 *
 * @author kristof
 */
public class NihExpiresFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (response instanceof final HttpServletResponse httpResponse) {
			// Http 1.1
			httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			// Http 1.0
			httpResponse.setHeader("Pragma", "no-cache");
			// Proxies
			httpResponse.setDateHeader("Expires", 0L);
		}
		chain.doFilter(request, response);
	}

}
