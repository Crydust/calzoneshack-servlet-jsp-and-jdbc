package com.example.training.demo;

import static org.openqa.selenium.support.ui.ExpectedConditions.and;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HelloServletPage {

	private static final String BASE_URL_KEY = "training.baseUrl";
	private static final String DEFAULT_BASE_URL = "http://localhost:8080/training";
	private final WebDriver driver;
	private final WebDriverWait wait;

	@FindBy(id = "nickname")
	public WebElement input;

	@FindBy(id = "helloButton")
	public WebElement button;

	@FindBy(id = "nicknameOutput")
	public WebElement output;

	public HelloServletPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		PageFactory.initElements(this.driver, this);
	}

	private static String baseUrl() {
		return System.getProperty(BASE_URL_KEY, DEFAULT_BASE_URL);
	}

	public static HelloServletPage go(WebDriver driver) {
		driver.get(baseUrl() + "/HelloServlet");
		return new HelloServletPage(driver);
	}

	public String submitName(String name) {
		wait.until(and(titleIs("HelloServlet"), elementToBeClickable(this.input)));
		this.input.clear();
		this.input.sendKeys(name);
		this.button.submit();
		wait.until(elementToBeClickable(this.output));
		return this.output.getText();
	}

}
