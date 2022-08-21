package com.example.training.util;

import static java.lang.Integer.toHexString;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.Normalizer.Form.NFC;
import static java.util.Objects.requireNonNull;

import java.net.URLEncoder;
import java.text.Normalizer;

public final class NihStringUtil {

	private NihStringUtil() {
		// NOOP
	}

	/**
	 * Escapes all non-alphanumeric characters in a String into html entities.
	 * <p>
	 * You should probably use commons-text
	 * <a href="https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/StringEscapeUtils.html">
	 * StringEscapeUtils</a> instead.
	 *
	 * @param text Original text, with potentially dangerous html characters in it.
	 * @return the same text, but with dangerous characters escaped
	 */
	public static String escapeHtml4(String text) {
		if (text == null) {
			return null;
		}
		StringBuilder out = new StringBuilder();
		Normalizer.normalize(text, NFC).codePoints().forEach(c -> {
			if (c == 34) {
				out.append("&quot;");
			} else if (c == 38) {
				out.append("&amp;");
			} else if (c == 39) {
				out.append("&#x27;");
			} else if (c == 60) {
				out.append("&lt;");
			} else if (c == 62) {
				out.append("&gt;");
			} else if (c == 96) {
				out.append("&#x60;");
			} else if (c > 127 || !Character.isLetterOrDigit(c)) {
				out.append("&#x").append(toHexString(c).toUpperCase()).append(';');
			} else {
				out.appendCodePoint(c);
			}
		});
		return out.toString();
	}

	/**
	 * This method adds an attribute to the querystring of a url.
	 * <p>
	 * I'm obviously reinventing the wheel here. See
	 * <a href="https://stackoverflow.com/questions/26177749/how-can-i-append-a-query-parameter-to-an-existing-url">How
	 * can I append a query parameter to an existing URL?</a> for better
	 * approaches to this problem</p>
	 *
	 * @param url
	 * @param key
	 * @param value
	 * @return
	 */
	public static String addAttributeToQuery(String url, String key, String value) {
		if (url == null) {
			return null;
		}
		requireNonNull(key, "key is null");
		requireNonNull(value, "value is null");
		int queryStart = url.indexOf('?');
		final int fragmentStart = url.indexOf('#');
		final String encodedKeyValuePair = URLEncoder.encode(key, UTF_8) + '=' + URLEncoder.encode(value, UTF_8);
		if (queryStart == -1 && fragmentStart == -1) {
			return url + '?' + encodedKeyValuePair;
		}
		if (fragmentStart != -1 && queryStart > fragmentStart) {
			queryStart = -1;
		}
		final String prefix;
		final String query;
		final String fragment;
		if (queryStart != -1) {
			prefix = url.substring(0, queryStart);
			if (fragmentStart != -1) {
				query = url.substring(queryStart + 1, fragmentStart);
				fragment = url.substring(fragmentStart + 1);
			} else {
				query = url.substring(queryStart + 1);
				fragment = null;
			}
		} else {
			prefix = url.substring(0, fragmentStart);
			query = null;
			fragment = url.substring(fragmentStart + 1);
		}
		final int length = url.length() + encodedKeyValuePair.length() + (query == null || !query.isEmpty() ? 1 : 0);
		final StringBuilder sb = new StringBuilder(length);
		sb.append(prefix);
		sb.append('?');
		if (query != null && !query.isEmpty()) {
			sb.append(query);
			sb.append('&');
		}
		sb.append(encodedKeyValuePair);
		if (fragment != null) {
			sb.append('#');
			sb.append(fragment);
		}
		return sb.toString();
	}

}
