package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SelectOptionContainingTextTest {
  private SelectOptionContainingText command = new SelectOptionContainingText();

  private WebElement element;
  private WebElement option1;
  private WebElement option2;
  private SelenideElement proxy;
  private WebElementSource select;

  @Before
  public void setUp() {
    element = mock(WebElement.class);
    option1 = mock(WebElement.class);
    option2 = mock(WebElement.class);
    proxy = mock(SelenideElement.class);
    select = mock(WebElementSource.class);
    doReturn(element).when(select).getWebElement();
    doReturn("select").when(element).getTagName();
  }

  @Test
  public void selectsFirstMatchingOptionForSingleSelect() {
    doReturn("false").when(element).getAttribute("multiple");
    doReturn(asList(option1, option2)).when(element)
      .findElements(
        By.xpath(".//option[contains(normalize-space(.), \"option-subtext\")]"));

    command.execute(proxy, select, new Object[]{"option-subtext"});

    verify(option1).click();
    verify(option2, never()).click();
  }

  @Test
  public void selectsAllMatchingOptionsForMultipleSelect() {
    doReturn("true").when(element).getAttribute("multiple");
    doReturn(asList(option1, option2)).when(element)
      .findElements(
        By.xpath(".//option[contains(normalize-space(.), \"option-subtext\")]"));

    command.execute(proxy, select, new Object[]{"option-subtext"});

    verify(option1).click();
    verify(option2).click();
  }

  @Test
  public void throwsNoSuchElementExceptionWhenNoElementsFound() {
    String elementText = "option-subtext";
    try {
      command.execute(proxy, select, new Object[]{elementText});
    } catch (NoSuchElementException exception) {
      assertTrue("Text is not present in exception message",
        exception.getMessage().contains("Cannot locate option containing text: " + elementText));
    }
  }
}
