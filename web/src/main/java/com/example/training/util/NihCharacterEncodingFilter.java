package com.example.training.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Sets request CharacterEncoding to UTF-8 so parameters sent with POST are read
 * correctly.
 *
 * <p>
 * Should be replaced by
 * <a href="http://tomcat.apache.org/tomcat-8.5-doc/config/filter.html#Add_Default_Character_Set_Filter">org.apache.catalina.filters.AddDefaultCharsetFilter</a></p>
 *
 * @author kristof
 */
public class NihCharacterEncodingFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		chain.doFilter(request, response);
	}

}
