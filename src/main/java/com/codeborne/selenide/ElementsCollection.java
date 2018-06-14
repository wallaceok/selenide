package com.codeborne.selenide;

import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.CollectionElement;
import com.codeborne.selenide.impl.CollectionElementByCondition;
import com.codeborne.selenide.impl.FilteringCollection;
import com.codeborne.selenide.impl.HeadOfCollection;
import com.codeborne.selenide.impl.LastCollectionElement;
import com.codeborne.selenide.impl.SelenideElementIterator;
import com.codeborne.selenide.impl.SelenideElementListIterator;
import com.codeborne.selenide.impl.TailOfCollection;
import com.codeborne.selenide.impl.WebElementsCollection;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Configuration.assertionMode;
import static com.codeborne.selenide.Configuration.collectionsPollingInterval;
import static com.codeborne.selenide.Configuration.collectionsTimeout;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.logevents.ErrorsCollector.validateAssertionMode;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.util.stream.Collectors.toList;

public class ElementsCollection extends AbstractList<SelenideElement> {
  private final WebElementsCollection collection;

  public ElementsCollection(WebElementsCollection collection) {
    this.collection = collection;
  }

  /**
   * Asserts if the collection is of given size
   *
   * @param expectedSize
   * @return ElementsCollection
   */
  public ElementsCollection shouldHaveSize(int expectedSize) {
    return shouldHave(CollectionCondition.size(expectedSize));
  }

  /**
   * Asserts conditions
   * $$(".error").shouldBe(empty)
   *
   */
  public ElementsCollection shouldBe(CollectionCondition... conditions) {
    return should("be", conditions);
  }

  /**
   * Assert conditions
   * $$(".error").shouldHave(size(3))
   * $$(".error").shouldHave(texts("Error1", "Error2"))
   */
  public ElementsCollection shouldHave(CollectionCondition... conditions) {
    return should("have", conditions);
  }

  protected ElementsCollection should(String prefix, CollectionCondition... conditions) {
    validateAssertionMode();

    SelenideLog log = SelenideLogger.beginStep(collection.description(), "should " + prefix, (Object[]) conditions);
    try {
      for (CollectionCondition condition : conditions) {
        waitUntil(condition, collectionsTimeout);
      }
      SelenideLogger.commitStep(log, PASS);
      return this;
    }
    catch (Error error) {
      Error wrappedError = UIAssertionError.wrap(error, collectionsTimeout);
      SelenideLogger.commitStep(log, wrappedError);
      switch (assertionMode) {
        case SOFT:
          return this;
        default:
          throw wrappedError;
      }
    }
    catch (RuntimeException e) {
      SelenideLogger.commitStep(log, e);
      throw e;
    }
  }

  protected void waitUntil(CollectionCondition condition, long timeoutMs) {
    Exception lastError = null;
    List<WebElement> actualElements = null;
    final long startTime = System.currentTimeMillis();
    do {
      try {
        actualElements = collection.getActualElements();
        if (condition.apply(actualElements)) {
          return;
        }
      }
      catch (WebDriverException elementNotFound) {
        lastError = elementNotFound;

        if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
          throw Cleanup.of.wrap(elementNotFound);
        }
      }
      sleep(collectionsPollingInterval);
    }
    while (System.currentTimeMillis() - startTime < timeoutMs);
    condition.fail(collection, actualElements, lastError, timeoutMs);
  }
  void sleep(long ms) {
    Selenide.sleep(ms);
  }

  /**
   * Filters collection elements based on the given condition (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
   * @param condition
   * @return ElementsCollection
   */
  public ElementsCollection filter(Condition condition) {
    return new ElementsCollection(new FilteringCollection(collection, condition));
  }

  /**
   * Filters collection elements based on the given condition (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
   * @see #filter(Condition)
   * @param condition
   * @return ElementsCollection
   */
  public ElementsCollection filterBy(Condition condition) {
    return filter(condition);
  }

  /**
   * Filters elements excluding those which met the given condition (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
   * @param condition
   * @return ElementsCollection
   */
  public ElementsCollection exclude(Condition condition) {
    return new ElementsCollection(new FilteringCollection(collection, not(condition)));
  }

  /**
   * Filters elements excluding those which met the given condition (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
   * @see #exclude(Condition)
   * @param condition
   * @return ElementsCollection
   */
  public ElementsCollection excludeWith(Condition condition) {
    return exclude(condition);
  }

  /**
   * Find the first element which met the given condition (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
   * @param condition
   * @return SelenideElement
   */
  public SelenideElement find(Condition condition) {
    return CollectionElementByCondition.wrap(collection, condition);
  }

  /**
   * Find the first element which met the given condition (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
   * @see #find(Condition)
   * @param condition
   * @return SelenideElement
   */
  public SelenideElement findBy(Condition condition) {
    return find(condition);
  }

  private List<WebElement> getElements() {
    return collection.getElements();
  }

  /**
   * Gets all the texts in elements collection
   * @return array of texts
   */
  public List<String> texts() {
    return texts(getElements());
  }

  /**
   * @deprecated Use method com.codeborne.selenide.ElementsCollection#texts() that returns List instead of array
   */
  @Deprecated
  public String[] getTexts() {
    return getTexts(getElements());
  }

  /**
   * Fail-safe method for retrieving texts of given elements.
   * @param elements Any collection of WebElements
   * @return Array of texts (or exceptions in case of any WebDriverExceptions)
   */
  public static List<String> texts(Collection<WebElement> elements) {
    return elements.stream().map(e -> getText(e)).collect(toList());
  }

  /**
   * @deprecated Use method com.codeborne.selenide.ElementsCollection#texts(java.util.Collection)
   *              that returns List instead of array
   */
  @Deprecated
  public static String[] getTexts(Collection<WebElement> elements) {
    String[] texts = new String[elements.size()];
    int i = 0;
    for (WebElement element : elements) {
      texts[i++] = getText(element);
    }
    return texts;
  }

  private static String getText(WebElement element) {
    try {
      return element.getText();
    } catch (WebDriverException elementDisappeared) {
      return elementDisappeared.toString();
    }
  }

  /**
   * Outputs string presentation of the element's collection
   * @param elements
   * @return String
   */
  public static String elementsToString(Collection<WebElement> elements) {
    if (elements == null) {
      return "[not loaded yet...]";
    }

    if (elements.isEmpty()) {
      return "[]";
    }

    StringBuilder sb = new StringBuilder(256);
    sb.append("[\n\t");
    for (WebElement element : elements) {
      if (sb.length() > 4) {
        sb.append(",\n\t");
      }
      sb.append($(element));
    }
    sb.append("\n]");
    return sb.toString();
  }

  /**
   * Gets the n-th element of collection (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
   *
   * @param index 0..N
   * @return
   */
  @Override
  public SelenideElement get(int index) {
    return CollectionElement.wrap(collection, index);
  }

  /**
   * returns the first element of the collection
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
   * NOTICE: $(css) is faster and returns the same result as $$(css).first()
   * @return
   */
  public SelenideElement first() {
    return get(0);
  }

  /**
   * returns the last element of the collection (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
   * @return
   */
  public SelenideElement last() {
    return LastCollectionElement.wrap(collection);
  }

  /**
   * returns the first n elements of the collection (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
   * @param elements number of elements 1..N
   */
  public ElementsCollection first(int elements) {
    return new ElementsCollection(new HeadOfCollection(collection, elements));
  }

  /**
   * returns the last n elements of the collection (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
   * @param elements number of elements 1..N
   */
  public ElementsCollection last(int elements) {
    return new ElementsCollection(new TailOfCollection(collection, elements));
  }

  /**
   * return actual size of the collection, doesn't wait on collection to be loaded.
   * ATTENTION not recommended for use in tests. Use collection.shouldHave(size(n)); for assertions instead.
   * @return
   */
  @Override
  public int size() {
    return getElements().size();
  }

  @Override
  public Iterator<SelenideElement> iterator() {
    return new SelenideElementIterator(collection);
  }

  @Override
  public ListIterator<SelenideElement> listIterator(int index) {
    return new SelenideElementListIterator(collection, index);
  }

  @Override
  public String toString() {
    try {
      return elementsToString(getElements());
    } catch (Exception e) {
      return String.format("[%s]", Cleanup.of.webdriverExceptionMessage(e));
    }
  }
}
