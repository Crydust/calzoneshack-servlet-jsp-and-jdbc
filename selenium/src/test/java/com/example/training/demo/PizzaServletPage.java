package com.example.training.demo;

import static java.util.stream.Collectors.toSet;
import static org.openqa.selenium.support.ui.ExpectedConditions.and;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PizzaServletPage {

	private static final String BASE_URL_KEY = "training.baseUrl";
	private static final String DEFAULT_BASE_URL = "http://localhost:8080";
	private final WebDriver driver;
	private final WebDriverWait wait;

	@FindBy(id = "firstname")
	public WebElement firstnameInput;
	@FindBy(id = "lastname")
	public WebElement lastnameInput;
	@FindBy(id = "email")
	public WebElement emailInput;
	@FindBy(name = "size")
	public List<WebElement> sizeInputs;
	@FindBy(name = "crust")
	public List<WebElement> crustInputs;
	@FindBy(name = "sauce")
	public List<WebElement> sauceInputs;
	@FindBy(name = "toppings")
	public List<WebElement> toppingsInputs;
	@FindBy(id = "button_add")
	public WebElement addButton;
	// IntelliJ didn't like this css selector:
	// @FindBy(css = "*[id ^⁼ 'button_remove']")
	@CacheLookup
	@FindBy(xpath = "//*[starts-with(@id, 'button_remove')]")
	public List<WebElement> removeButtons;
	@FindBy(id = "items")
	public WebElement itemsTable;
	@FindBy(id = "button_checkout")
	public WebElement checkoutButton;
	@FindBy(id = "language")
	public WebElement languageSelect;
	@FindBy(id = "button_chooseLanguage")
	public WebElement chooseLanguageButton;

	public PizzaServletPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		PageFactory.initElements(this.driver, this);
	}

	private static String baseUrl() {
		return System.getProperty(BASE_URL_KEY, DEFAULT_BASE_URL);
	}

	public static PizzaServletPage go(WebDriver driver) {
		driver.get(baseUrl() + "/PizzaServlet");
		return new PizzaServletPage(driver);
	}

	public PizzaServletPage checkPageIsLoaded() {
		wait.until(and(titleIs("PizzaServlet"), elementToBeClickable(this.firstnameInput)));
		return this;
	}

	public PizzaServletPage fillCustomerInformation(String firstname, String lastname, String email) {
		firstnameInput.sendKeys(firstname);
		lastnameInput.sendKeys(lastname);
		emailInput.sendKeys(email);
		return this;
	}

	public PizzaServletPage addPizza(PizzaValue pizza) {
		return addPizza(pizza.size(), pizza.crust(), pizza.sauce(), pizza.toppings().toArray(String[]::new));
	}

	public PizzaServletPage addPizza(String size, String crust, String sauce, String[] toppings) {
		InputGroup.ofRadioButtons(sizeInputs).selectByVisibleText(size);
		InputGroup.ofRadioButtons(crustInputs).selectByVisibleText(crust);
		InputGroup.ofRadioButtons(sauceInputs).selectByVisibleText(sauce);
		InputGroup toppingsCheckboxGroup = InputGroup.ofCheckboxes(toppingsInputs);
		toppingsCheckboxGroup.deselectAll();
		for (String topping : toppings) {
			toppingsCheckboxGroup.selectByVisibleText(topping);
		}
		addButton.click();
		return new PizzaServletPage(driver).checkPageIsLoaded();
	}

	public PizzaServletPage removePizza(PizzaValue pizza) {
		List<PizzaValue> pizzas = readPizzas();
		int index = pizzas.indexOf(pizza);
		WebElement removeButton = removeButtons.get(index);
		// click ignores formaction attribute
		//removeButton.click();
		removeButton.sendKeys(Keys.ENTER);
		wait.until(stalenessOf(removeButton));
		return new PizzaServletPage(driver).checkPageIsLoaded();
	}

	public List<PizzaValue> readPizzas() {
		if (!driver.findElements(By.id("items_empty")).isEmpty()) {
			return List.of();
		}
		return itemsTable.findElements(By.cssSelector("tbody tr")).stream()
				.map(row -> new PizzaValue(
						row.findElement(By.cssSelector("td:nth-child(1)")).getText(),
						row.findElement(By.cssSelector("td:nth-child(2)")).getText(),
						row.findElement(By.cssSelector("td:nth-child(3)")).getText(),
						row.findElements(By.cssSelector("td:nth-child(4) ul li")).stream().map(WebElement::getText).collect(toSet())
				))
				.toList();
	}

	@Deprecated
	public String itemsTableOuterHtml() {
		return itemsTable.getAttribute("outerHTML");
	}

	public PizzaServletPage checkout() {
		checkoutButton.click();
		return new PizzaServletPage(driver).checkPageIsLoaded();
	}

	public PizzaServletPage chooseLanguage(String language) {
		new Select(languageSelect).selectByVisibleText(language);
		chooseLanguageButton.click();
		return new PizzaServletPage(driver).checkPageIsLoaded();
	}

	public record PizzaValue(String size, String crust, String sauce, Set<String> toppings) {
	}

}
