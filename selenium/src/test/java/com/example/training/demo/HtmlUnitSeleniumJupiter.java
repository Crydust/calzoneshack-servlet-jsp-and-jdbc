package com.example.training.demo;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import io.github.bonigarcia.seljup.SeleniumJupiter;

public class HtmlUnitSeleniumJupiter extends SeleniumJupiter {
	// TODO cleanup this hack
	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
		return Boolean.getBoolean("htmlunit")
				&& parameterContext.getParameter().getType() == WebDriver.class
				? new HtmlUnitDriver()
				: super.resolveParameter(parameterContext, extensionContext);
	}
}
