package com.example.training.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NihStringUtilTest {

	@ParameterizedTest(name = "[{index}] escapeHtml4({0}) -> {1}")
	@CsvSource(textBlock = """
			<null>,                   <null>
			'',                       ''
			'&',                      '&amp;'
			'a',                      'a'
			'a&',                     'a&amp;'
			'&a',                     '&amp;a'
			'a&a',                    'a&amp;a'
			'&<>"''` !@$%()=+{}[]/', '&amp;&lt;&gt;&quot;&#x27;&#x60;&#x20;&#x21;&#x40;&#x24;&#x25;&#x28;&#x29;&#x3D;&#x2B;&#x7B;&#x7D;&#x5B;&#x5D;&#x2F;'
			'If I had a dollar for every HTML escaper that only escapes &, <, >, and ", I''d have $0. Because my account would''ve been pwned via XSS.', 'If&#x20;I&#x20;had&#x20;a&#x20;dollar&#x20;for&#x20;every&#x20;HTML&#x20;escaper&#x20;that&#x20;only&#x20;escapes&#x20;&amp;&#x2C;&#x20;&lt;&#x2C;&#x20;&gt;&#x2C;&#x20;and&#x20;&quot;&#x2C;&#x20;I&#x27;d&#x20;have&#x20;&#x24;0&#x2E;&#x20;Because&#x20;my&#x20;account&#x20;would&#x27;ve&#x20;been&#x20;pwned&#x20;via&#x20;XSS&#x2E;'
			'&<>"''/',               '&amp;&lt;&gt;&quot;&#x27;&#x2F;'
			'&<>"''/ %*+,-/;<=>^|',  '&amp;&lt;&gt;&quot;&#x27;&#x2F;&#x20;&#x25;&#x2A;&#x2B;&#x2C;&#x2D;&#x2F;&#x3B;&lt;&#x3D;&gt;&#x5E;&#x7C;'
			""",
			nullValues = "<null>"
	)
	void escapeHtml(String input, String expected) {
		String actual = NihStringUtil.escapeHtml4(input);
		assertThat(actual, expected == null ? is(nullValue()) : is(expected));
	}

	@ParameterizedTest(name = "[{index}] addAttributeToQuery({0}, {1}, {2}) -> {3}")
	@CsvSource(textBlock = """
			<null>,                  'key',  'value',  <null>
			'',                      'key',  'value',  '?key=value'
			'url',                   'key',  'value',  'url?key=value'
			'url',                   'é?&',  'á?&',    'url?%C3%A9%3F%26=%C3%A1%3F%26'
			'url?',                  'key',  'value',  'url?key=value'
			'url#',                  'key',  'value',  'url?key=value#'
			'url#fragment',          'key',  'value',  'url?key=value#fragment'
			'url?#',                 'key',  'value',  'url?key=value#'
			'url?#fragment',         'key',  'value',  'url?key=value#fragment'
			'url?query',             'key',  'value',  'url?query&key=value'
			'url?query#',            'key',  'value',  'url?query&key=value#'
			'url?query#fragment',    'key',  'value',  'url?query&key=value#fragment'
			'url#fragment?notaquery','key',  'value',  'url?key=value#fragment?notaquery'
			'',                      'é?&',  'á?&',    '?%C3%A9%3F%26=%C3%A1%3F%26'
			'?',                     'key',  'value',  '?key=value'
			'#',                     'key',  'value',  '?key=value#'
			'#fragment',             'key',  'value',  '?key=value#fragment'
			'?#',                    'key',  'value',  '?key=value#'
			'?#fragment',            'key',  'value',  '?key=value#fragment'
			'?query',                'key',  'value',  '?query&key=value'
			'?query#',               'key',  'value',  '?query&key=value#'
			'?query#fragment',       'key',  'value',  '?query&key=value#fragment'
			'#fragment?notaquery',   'key',  'value',  '?key=value#fragment?notaquery'
			""",
			nullValues = "<null>"
	)
	void addAttributeToQuery(String url, String key, String value, String expected) {
		String actual = NihStringUtil.addAttributeToQuery(url, key, value);
		assertThat(actual, expected == null ? is(nullValue()) : is(expected));
	}
}
