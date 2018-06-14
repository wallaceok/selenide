package com.codeborne.selenide;

import com.codeborne.selenide.collections.ExactTexts;
import com.codeborne.selenide.collections.ListSize;
import com.codeborne.selenide.collections.SizeGreaterThan;
import com.codeborne.selenide.collections.SizeGreaterThanOrEqual;
import com.codeborne.selenide.collections.SizeLessThan;
import com.codeborne.selenide.collections.SizeLessThanOrEqual;
import com.codeborne.selenide.collections.SizeNotEqual;
import com.codeborne.selenide.collections.Texts;
import com.codeborne.selenide.collections.TextsInAnyOrder;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CollectionConditionTest {
  @Test
  public void testSizeIsEmptyListSize() {
    CollectionCondition collectionCondition = CollectionCondition.size(10);
    assertThat(collectionCondition, instanceOf(ListSize.class));
  }

  @Test
  public void testSizeGreaterThan() {
    CollectionCondition collectionCondition = CollectionCondition.sizeGreaterThan(10);
    assertThat(collectionCondition, instanceOf(SizeGreaterThan.class));
  }

  @Test
  public void testSizeGraterThenOrEqual() {
    CollectionCondition collectionCondition = CollectionCondition.sizeGreaterThanOrEqual(10);
    assertThat(collectionCondition, instanceOf(SizeGreaterThanOrEqual.class));
  }

  @Test
  public void testSizeLessThan() {
    CollectionCondition collectionCondition = CollectionCondition.sizeLessThan(10);
    assertThat(collectionCondition, instanceOf(SizeLessThan.class));
  }

  @Test
  public void testSizeLessThanOrEqual() {
    CollectionCondition collectionCondition = CollectionCondition.sizeLessThanOrEqual(10);
    assertThat(collectionCondition, instanceOf(SizeLessThanOrEqual.class));
  }

  @Test
  public void testSizeNotEqual() {
    CollectionCondition collectionCondition = CollectionCondition.sizeNotEqual(10);
    assertThat(collectionCondition, instanceOf(SizeNotEqual.class));
  }

  @Test
  public void testTextsWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.texts("One", "Two", "Three");
    assertThat(collectionCondition, instanceOf(Texts.class));
    assertEquals("Texts content", "Texts [One, Two, Three]", collectionCondition.toString());
  }

  @Test
  public void testTextsWithListOfStrings() {
    CollectionCondition collectionCondition = CollectionCondition.texts(asList("One", "Two", "Three"));
    assertThat(collectionCondition, instanceOf(Texts.class));
    assertEquals("Texts content", "Texts [One, Two, Three]", collectionCondition.toString());
  }

  @Test
  public void testExactTextsWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.exactTexts("One", "Two", "Three");
    assertThat(collectionCondition, instanceOf(ExactTexts.class));
    assertEquals("Exact texts content", "Exact texts [One, Two, Three]", collectionCondition.toString());
  }

  @Test
  public void testExactTextsWithListOfStrings() {
    CollectionCondition collectionCondition = CollectionCondition.exactTexts(asList("One", "Two", "Three"));
    assertThat(collectionCondition, instanceOf(ExactTexts.class));
    assertEquals("Exact texts content", "Exact texts [One, Two, Three]", collectionCondition.toString());
  }

  @Test
  public void testTextsInAnyOrderWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.textsInAnyOrder("One", "Two", "Three");
    assertThat(collectionCondition, instanceOf(TextsInAnyOrder.class));
    assertEquals("Text in any order content", "TextsInAnyOrder [One, Two, Three]", collectionCondition.toString());
  }

  @Test
  public void testTextsInAnyOrderWithStringsList() {
    CollectionCondition collectionCondition = CollectionCondition.textsInAnyOrder(asList("One", "Two", "Three"));
    assertThat(collectionCondition, instanceOf(TextsInAnyOrder.class));
    assertEquals("Text in any order content", "TextsInAnyOrder [One, Two, Three]", collectionCondition.toString());
  }
}
