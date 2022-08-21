package com.example.training.demo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.UUID;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

@ExtendWith(HtmlUnitSeleniumJupiter.class)
class HelloServletIT {

	@TestTemplate
	void shouldEchoName(WebDriver driver) {
		String expected = UUID.randomUUID().toString();
		String actual = HelloServletPage.go(driver).submitName(expected);
		assertThat(actual, is(expected));
	}

	@TestTemplate
	void shouldEscapeDangerousCharacters(WebDriver driver) {
		String expected = "<plaintext>";
		String actual = HelloServletPage.go(driver).submitName(expected);
		assertThat(actual, is(expected));
	}

	@TestTemplate
	void shouldHandleUnicode(WebDriver driver) {
		assumeFalse(driver instanceof EdgeDriver || driver instanceof ChromeDriver,
				"driver only supports characters in the BMP");
		String expected = "\uD83E\uDD84";
		String actual = HelloServletPage.go(driver).submitName(expected);
		assertThat(actual, is(expected));
	}

}
