package com.codeborne.selenide.impl;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HeadOfCollectionTest {
  private final WebElement element1 = mock(WebElement.class);
  private final WebElement element2 = mock(WebElement.class);
  private final WebElement element3 = mock(WebElement.class);
  WebElementsCollection originalCollection = mock(WebElementsCollection.class);

  @Before
  public void setUp() {
    when(originalCollection.getActualElements()).thenReturn(asList(element1, element2, element3));
  }

  @Test
  public void lessThanOriginalSize() {
    HeadOfCollection $$ = new HeadOfCollection(originalCollection, 2);
    assertEquals(asList(element1, element2), $$.getElements());
  }

  @Test
  public void equalToOriginalSize() {
    HeadOfCollection $$ = new HeadOfCollection(originalCollection, 3);
    assertEquals(asList(element1, element2, element3), $$.getElements());
  }

  @Test
  public void greaterThanOriginalSize() {
    HeadOfCollection $$ = new HeadOfCollection(originalCollection, 4);
    assertEquals(asList(element1, element2, element3), $$.getElements());
  }
}
