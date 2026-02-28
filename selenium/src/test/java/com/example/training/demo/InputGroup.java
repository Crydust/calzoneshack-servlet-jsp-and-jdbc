package com.example.training.demo;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;

/**
 * Models a CHECKBOX or RADIO group, providing helper methods to select and deselect them.
 */
public class InputGroup {
	private final List<WebElement> elements;
	private List<InputWithLabel> enrichedInputs = null;
	private final boolean isMultiple;

	public static InputGroup ofCheckboxes(List<WebElement> checkboxes) {
		return new InputGroup(checkboxes, true);
	}

	public static InputGroup ofRadioButtons(List<WebElement> checkboxes) {
		return new InputGroup(checkboxes, false);
	}

	/**
	 * Constructor. A check is made that the given elements are, indeed, checkbox or radio tags. If one is not,
	 * then an UnexpectedTagNameException is thrown.
	 *
	 * @param elements   INPUT elements to wrap
	 * @param isMultiple true for checkboxes, false for radio buttons
	 * @throws UnexpectedTagNameException when elements are not input tags
	 * @throws InvalidArgumentException   when elements are not checkbox tags
	 */
	private InputGroup(List<WebElement> elements, boolean isMultiple) {
		for (WebElement element : elements) {
			String tagName = element.getTagName();
			if (!"input".equalsIgnoreCase(tagName)) {
				throw new UnexpectedTagNameException("input", tagName);
			}
		}

		String expectedType = isMultiple ? "checkbox" : "radio";
		for (WebElement element : elements) {
			String type = element.getAttribute("type");
			if (!expectedType.equalsIgnoreCase(type)) {
				throw new InvalidArgumentException("Element type should have been \"%s\" but was \"%s\"".formatted(expectedType, type));
			}
		}

		this.elements = elements;
		this.isMultiple = isMultiple;
	}

	public List<WebElement> getWrappedElements() {
		return elements;
	}

	public boolean isMultiple() {
		return isMultiple;
	}

	/**
	 * @return All inputs belonging to this input group
	 */
	public List<WebElement> getInputs() {
		return elements;
	}

	/**
	 * @return All selected inputs belonging to this input group
	 */
	public List<WebElement> getAllSelectedInputs() {
		return elements.stream().filter(WebElement::isSelected).toList();
	}

	/**
	 * @return The first selected input in this input group
	 * @throws NoSuchElementException If no input is selected
	 */
	public WebElement getFirstSelectedInput() {
		return elements.stream()
				.filter(WebElement::isSelected)
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException("No %s are selected".formatted(isMultiple ? "checkboxes" : "radio buttons")));
	}

	/**
	 * Select all input that have a label with text matching the argument.
	 * That is, when given "Bar" this would select an input like:
	 *
	 * <ul>
	 *     <li><code>&lt;input type="checkbox" name="foo" value="bar" id="foo_bar"&gt;&lt;label for="foo_bar" &gt;Bar&lt;/label&gt;</code></li>
	 *     <li><code>&lt;label&gt;&lt;input type="checkbox" name="foo" value="bar"&gt;Bar&lt;/label&gt;</code></li>
	 * </ul>
	 *
	 * @param text The visible text to match against
	 * @throws NoSuchElementException If no matching input elements are found based on the visible text of a label element
	 */
	public void selectByVisibleText(String text) {
		List<InputWithLabel> found = getEnrichedInputs().stream()
				.filter(it -> it.label != null)
				.filter(it -> normalizeSpace(it.label.getText()).equals(normalizeSpace(text)))
				.limit(isMultiple ? Long.MAX_VALUE : 1)
				.toList();
		if (found.isEmpty()) {
			throw new NoSuchElementException("Cannot locate %s with text: %s".formatted(isMultiple ? "checkboxes" : "radio buttons", text));
		}
		found.forEach(it -> setSelected(it.input, true));
	}

	/**
	 * Select the input at the given index. This is done merely by counting.
	 *
	 * @param index The option at this index will be selected (zero-based)
	 * @throws NoSuchElementException If no matching input elements are found
	 */
	public void selectByIndex(int index) {
		setSelectedByIndex(index, true);
	}

	/**
	 * Select all inputs that have a value matching the argument.
	 * That is, when given "bar" this would select an input like:
	 *
	 * <ul>
	 *     <li><code>&lt;input type="checkbox" name="foo" value="bar" id="foo_bar"&gt;&lt;label for="foo_bar" &gt;Bar&lt;/label&gt;</code></li>
	 *     <li><code>&lt;label&gt;&lt;input type="checkbox" name="foo" value="bar"&gt;Bar&lt;/label&gt;</code></li>
	 * </ul>
	 *
	 * @param value The value to match against
	 * @throws NoSuchElementException If no matching input elements are found
	 */
	public void selectByValue(String value) {
		List<WebElement> found = elements.stream()
				.filter(it -> value.equals(it.getAttribute("value")))
				.limit(isMultiple ? Long.MAX_VALUE : 1).toList();
		if (found.isEmpty()) {
			throw new NoSuchElementException("Cannot locate %s with value: %s".formatted(isMultiple ? "checkboxes" : "radio buttons", value));
		}
		found.forEach(it -> setSelected(it, true));
	}

	/**
	 * Deselect all selected inputs in the group.
	 *
	 * @throws UnsupportedOperationException If the group is a radio button group
	 */
	public void deselectAll() {
		if (!isMultiple) {
			throw new UnsupportedOperationException("You may only deselect all options of checkboxes");
		}
		for (WebElement checkbox : elements) {
			setSelected(checkbox, false);
		}
	}

	/**
	 * Deselect all selected inputs that have a value matching the argument.
	 * That is, when given "bar" this would deselect a checkbox like:
	 *
	 * <ul>
	 *     <li><code>&lt;input type="checkbox" name="foo" value="bar" id="foo_bar"&gt;&lt;label for="foo_bar" &gt;Bar&lt;/label&gt;</code></li>
	 *     <li><code>&lt;label&gt;&lt;input type="checkbox" name="foo" value="bar"&gt;Bar&lt;/label&gt;</code></li>
	 * </ul>
	 *
	 * @param value The value to match against
	 * @throws NoSuchElementException If no matching input elements are found
	 * @throws UnsupportedOperationException If the group is a radio button group
	 */
	public void deselectByValue(String value) {
		if (!isMultiple) {
			throw new UnsupportedOperationException("You may only deselect options of checkboxes");
		}
		for (WebElement checkbox : findInputsByValue(value)) {
			setSelected(checkbox, false);
		}
	}

	/**
	 * Deselect the selected input at the given index. This is done merely by counting.
	 *
	 * @param index The option at this index will be deselected (zero-based)
	 * @throws NoSuchElementException If no matching input elements are found
	 * @throws UnsupportedOperationException If the group is a radio button group
	 */
	public void deselectByIndex(int index) {
		if (!isMultiple) {
			throw new UnsupportedOperationException("You may only deselect options of checkboxes");
		}
		setSelectedByIndex(index, false);
	}

	/**
	 * Deselect all inputs that have a label with text matching the argument.
	 * The label must have a for attribute referring to an input id with the group name.
	 * That is, when given "Bar" this would deselect a checkbox like:
	 *
	 * <ul>
	 *     <li><code>&lt;input type="checkbox" name="foo" value="bar" id="foo_bar"&gt;&lt;label for="foo_bar" &gt;Bar&lt;/label&gt;</code></li>
	 *     <li><code>&lt;label&gt;&lt;input type="checkbox" name="foo" value="bar"&gt;Bar&lt;/label&gt;</code></li>
	 * </ul>
	 *
	 * @param text The visible text to match against
	 * @throws NoSuchElementException If no matching input elements are found
	 * @throws UnsupportedOperationException If the group is a radio button group
	 */
	public void deselectByVisibleText(String text) {
		if (!isMultiple()) {
			throw new UnsupportedOperationException("You may only deselect options of checkboxes");
		}
		List<InputWithLabel> found = getEnrichedInputs().stream()
				.filter(it -> it.label != null)
				.filter(it -> normalizeSpace(it.label.getText()).equals(normalizeSpace(text)))
				.toList();
		if (found.isEmpty()) {
			throw new NoSuchElementException("Cannot locate checkbox with text: " + text);
		}
		found.forEach(it -> setSelected(it.input, false));
	}

	private List<WebElement> findInputsByValue(String value) {
		List<WebElement> found = elements.stream().filter(it -> value.equals(it.getAttribute("value"))).toList();
		if (found.isEmpty()) {
			throw new NoSuchElementException("Cannot locate %s with value: %s".formatted(isMultiple ? "checkboxes" : "radio buttons", value));
		}
		return found;
	}

	private void setSelectedByIndex(int index, boolean select) {
		if (index < 0 || elements.size() <= index) {
			throw new NoSuchElementException("Cannot locate %s with index: %d".formatted(isMultiple ? "checkboxes" : "radio buttons", index));
		}
		setSelected(elements.get(index), select);
	}

	private void setSelected(WebElement input, boolean select) {
		assertInputIsEnabled(input, select);
		if (input.isSelected() != select) {
			input.click();
		}
	}

	private void assertInputIsEnabled(WebElement input, boolean select) {
		if (select && !input.isEnabled()) {
			throw new UnsupportedOperationException("You may not select a disabled %s".formatted(isMultiple ? "checkbox" : "radio button"));
		}
	}

	/**
	 * The normalize-space function strips leading and trailing white-space from a string,
	 * replaces sequences of whitespace characters by a single space, and returns the resulting string.
	 */
	private static String normalizeSpace(String text) {
		if (text == null) {
			return null;
		}
		return text.replaceAll("\\s+", " ").trim();
	}

	private List<InputWithLabel> getEnrichedInputs() {
		if (enrichedInputs == null) {
			enrichedInputs = enrichInputs(elements);
		}
		return enrichedInputs;
	}

	/**
	 * Enriches a list of input elements with their corresponding labels.
	 */
	private static List<InputWithLabel> enrichInputs(List<WebElement> inputs) {
		if (inputs == null || inputs.isEmpty()) {
			return List.of();
		}

		// this is a performance optimization to avoid repeatedly running the expensive operation
		final Map<String, List<WebElement>> labelsById = findLabelsBasedOnForAttributes(inputs);

		final ArrayList<InputWithLabel> inputWithLabels = new ArrayList<>();
		for (WebElement input : inputs) {
			final String id = input.getAttribute("id");
			List<WebElement> labels = List.of();
			if (id != null) {
				// We've created the labelsById map to avoid repeatedly running this expensive operation
				// labels = input.findElements(By.xpath("//label[@for=%s]".formatted(Quotes.escape(id))));
				labels = labelsById.getOrDefault(id, List.of());
			}
			if (labels.isEmpty()) {
				labels = input.findElements(By.xpath("./ancestor::label"));
			}
			inputWithLabels.add(new InputWithLabel(input, labels.isEmpty() ? null : labels.getFirst()));
		}
		return inputWithLabels;
	}

	private static Map<String, List<WebElement>> findLabelsBasedOnForAttributes(List<WebElement> inputs) {
		if (inputs == null || inputs.isEmpty()) {
			return Map.of();
		}
		final Map<String, List<WebElement>> labelsById = new HashMap<>();
		final List<String> inputIds = inputs.stream()
				.map(it -> it.getAttribute("id"))
				.filter(Objects::nonNull)
				.distinct()
				.toList();
		final int partitionSize = 20;
		final WebElement firstInput = inputs.getFirst();
		for (int i = 0; i < inputIds.size(); i += partitionSize) {
			final String xpath = inputIds.subList(i, Math.min(i + partitionSize, inputs.size())).stream()
					.map(Quotes::escape)
					.collect(joining(" or @for=", "//label[@for=", "]"));
			firstInput
					.findElements(By.xpath(xpath))
					.forEach(label -> labelsById.computeIfAbsent(label.getAttribute("for"), k -> new ArrayList<>()).add(label));
		}
		return labelsById;
	}

	private record InputWithLabel(WebElement input, WebElement label) {
	}

}
