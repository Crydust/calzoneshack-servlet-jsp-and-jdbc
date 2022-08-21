package com.example.training.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class NihStringUtilTest {

	@Test
	public void escapeHtml() {
		assertThat(NihStringUtil.escapeHtml4(null), is(nullValue()));
		assertThat(NihStringUtil.escapeHtml4(""), is(""));
		assertThat(NihStringUtil.escapeHtml4("&"), is("&amp;"));
		assertThat(NihStringUtil.escapeHtml4("a"), is("a"));
		assertThat(NihStringUtil.escapeHtml4("a&"), is("a&amp;"));
		assertThat(NihStringUtil.escapeHtml4("&a"), is("&amp;a"));
		assertThat(NihStringUtil.escapeHtml4("a&a"), is("a&amp;a"));
		assertThat(NihStringUtil.escapeHtml4("&<>\"'` !@$%()=+{}[]/"),
				is("&amp;&lt;&gt;&quot;&#x27;&#x60;&#x20;&#x21;&#x40;&#x24;&#x25;&#x28;&#x29;&#x3D;&#x2B;&#x7B;&#x7D;&#x5B;&#x5D;&#x2F;"));
		assertThat(NihStringUtil.escapeHtml4("If I had a dollar for every HTML escaper that only escapes &, <, >, and \", I'd have $0. Because my account would've been pwned via XSS."),
				is("If&#x20;I&#x20;had&#x20;a&#x20;dollar&#x20;for&#x20;every&#x20;HTML&#x20;escaper&#x20;that&#x20;only&#x20;escapes&#x20;&amp;&#x2C;&#x20;&lt;&#x2C;&#x20;&gt;&#x2C;&#x20;and&#x20;&quot;&#x2C;&#x20;I&#x27;d&#x20;have&#x20;&#x24;0&#x2E;&#x20;Because&#x20;my&#x20;account&#x20;would&#x27;ve&#x20;been&#x20;pwned&#x20;via&#x20;XSS&#x2E;"));
		// see https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet#RULE_.231_-_HTML_Escape_Before_Inserting_Untrusted_Data_into_HTML_Element_Content
		// element content
		assertThat(NihStringUtil.escapeHtml4("&<>\"'/"),
				is("&amp;&lt;&gt;&quot;&#x27;&#x2F;"));
		// attribute
		assertThat(NihStringUtil.escapeHtml4("&<>\"'/ %*+,-/;<=>^|"),
				is("&amp;&lt;&gt;&quot;&#x27;&#x2F;&#x20;&#x25;&#x2A;&#x2B;&#x2C;&#x2D;&#x2F;&#x3B;&lt;&#x3D;&gt;&#x5E;&#x7C;"));
	}

	@Test
	public void addAttributeToQuery() {
		assertThat(NihStringUtil.addAttributeToQuery(null, "key", "value"), is(nullValue()));
		assertThat(NihStringUtil.addAttributeToQuery("", "key", "value"), is("?key=value"));
		assertThat(NihStringUtil.addAttributeToQuery("url", "key", "value"), is("url?key=value"));
		assertThat(NihStringUtil.addAttributeToQuery("url", "é?&", "á?&"), is("url?%C3%A9%3F%26=%C3%A1%3F%26"));
		assertThat(NihStringUtil.addAttributeToQuery("url?", "key", "value"), is("url?key=value"));
		assertThat(NihStringUtil.addAttributeToQuery("url#", "key", "value"), is("url?key=value#"));
		assertThat(NihStringUtil.addAttributeToQuery("url#fragment", "key", "value"), is("url?key=value#fragment"));
		assertThat(NihStringUtil.addAttributeToQuery("url?#", "key", "value"), is("url?key=value#"));
		assertThat(NihStringUtil.addAttributeToQuery("url?#fragment", "key", "value"), is("url?key=value#fragment"));
		assertThat(NihStringUtil.addAttributeToQuery("url?query", "key", "value"), is("url?query&key=value"));
		assertThat(NihStringUtil.addAttributeToQuery("url?query#", "key", "value"), is("url?query&key=value#"));
		assertThat(NihStringUtil.addAttributeToQuery("url?query#fragment", "key", "value"), is("url?query&key=value#fragment"));
		assertThat(NihStringUtil.addAttributeToQuery("url#fragment?notaquery", "key", "value"), is("url?key=value#fragment?notaquery"));
		assertThat(NihStringUtil.addAttributeToQuery("", "é?&", "á?&"), is("?%C3%A9%3F%26=%C3%A1%3F%26"));
		assertThat(NihStringUtil.addAttributeToQuery("?", "key", "value"), is("?key=value"));
		assertThat(NihStringUtil.addAttributeToQuery("#", "key", "value"), is("?key=value#"));
		assertThat(NihStringUtil.addAttributeToQuery("#fragment", "key", "value"), is("?key=value#fragment"));
		assertThat(NihStringUtil.addAttributeToQuery("?#", "key", "value"), is("?key=value#"));
		assertThat(NihStringUtil.addAttributeToQuery("?#fragment", "key", "value"), is("?key=value#fragment"));
		assertThat(NihStringUtil.addAttributeToQuery("?query", "key", "value"), is("?query&key=value"));
		assertThat(NihStringUtil.addAttributeToQuery("?query#", "key", "value"), is("?query&key=value#"));
		assertThat(NihStringUtil.addAttributeToQuery("?query#fragment", "key", "value"), is("?query&key=value#fragment"));
		assertThat(NihStringUtil.addAttributeToQuery("#fragment?notaquery", "key", "value"), is("?key=value#fragment?notaquery"));
	}
}
