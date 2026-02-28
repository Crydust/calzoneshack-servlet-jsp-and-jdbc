package com.example.training.util;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Add security related headers to each response.
 *
 * <p>Headers:</p>
 * <ul>
 * <li>X-Frame-Options:SAMEORIGIN</li>
 * <li>X-XSS-Protection: 1; mode=block</li>
 * <li>X-Content-Type-Options: nosniff</li>
 * </ul>
 *
 * <p>Should be replaced by <a href="http://tomcat.apache.org/tomcat-8.5-doc/config/filter.html#HTTP_Header_Security_Filter">org.apache.catalina.filters.HttpHeaderSecurityFilter</a>.</p>
 *
 * @author kristof
 */
public class NihSecurityHeadersFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (response instanceof final HttpServletResponse httpResponse) {
			httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
			// support for the X-XSS-Protection has been removed from all major browsers
			httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
			httpResponse.setHeader("X-Content-Type-Options", "nosniff");
			httpResponse.setHeader("Content-Security-Policy",
					"default-src 'self'; script-src 'none'; frame-ancestors 'none'; form-action 'self'");
		}
		chain.doFilter(request, response);
	}

}
