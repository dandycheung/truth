/*
 * Copyright (c) 2018 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.truth;

import static com.google.common.truth.Fact.fact;
import static com.google.common.truth.Fact.formatNumericValue;
import static com.google.common.truth.Fact.makeMessage;
import static com.google.common.truth.Fact.numericFact;
import static com.google.common.truth.Fact.simpleFact;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for {@link Fact}. */
@RunWith(JUnit4.class)
public class FactTest {

  private static final Joiner TEXT = Joiner.on("\n");

  @Test
  public void string() {
    assertThat(fact("foo", "bar").toString()).isEqualTo("foo: bar");
  }

  @Test
  public void stringWithoutValue() {
    assertThat(simpleFact("foo").toString()).isEqualTo("foo");
  }

  @Test
  public void oneFacts() {
    assertThat(makeMessage(ImmutableList.of(), ImmutableList.of(fact("foo", "bar"))))
        .isEqualTo("foo: bar");
  }

  @Test
  public void twoFacts() {
    assertThat(
            makeMessage(
                ImmutableList.of(),
                ImmutableList.of(fact("foo", "bar"), fact("longer name", "other value"))))
        .isEqualTo(
            TEXT.join(
                "foo        : bar", // force a line break
                "longer name: other value"));
  }

  @Test
  public void mixingFactsAndNumericFacts() {
    assertThat(
            makeMessage(
                ImmutableList.of(),
                ImmutableList.of(
                    fact("value of", "optionalInt.getAsInt()"),
                    numericFact("expected", 2000),
                    numericFact("but was", 1337),
                    numericFact("outside tolerance", 42))))
        .isEqualTo(
            TEXT.join(
                "value of         : optionalInt.getAsInt()",
                "expected         : 2,000",
                "but was          : 1,337",
                "outside tolerance:    42"));
  }

  @Test
  public void numericFacts_integers() {
    assertThat(
            makeMessage(
                ImmutableList.of(),
                ImmutableList.of(
                    numericFact("expected", 802604),
                    numericFact("but was", 773804),
                    numericFact("outside tolerance", 9599))))
        .isEqualTo(
            TEXT.join(
                "expected         : 802,604",
                "but was          : 773,804",
                "outside tolerance:   9,599"));
  }

  @Test
  public void numericFacts_doubles() {
    assertThat(
            makeMessage(
                ImmutableList.of(),
                ImmutableList.of(
                    numericFact("expected", 802604.123),
                    numericFact("but was", 773804.123),
                    numericFact("outside tolerance", 95.11111111))))
        .isEqualTo(
            TEXT.join(
                "expected         : 802,604.123",
                "but was          : 773,804.123",
                "outside tolerance:      95.11111111"));
  }

  @Test
  public void numericFacts_bigDecimals() {
    assertThat(
            makeMessage(
                ImmutableList.of(),
                ImmutableList.of(
                    numericFact("expected", new BigDecimal("802604.123")),
                    numericFact("but was", new BigDecimal("3804.123")))))
        .isEqualTo(
            TEXT.join(
                "expected: 802,604.123", // force a line break
                "but was :   3,804.123"));
  }

  @Test
  public void oneFactWithoutValue() {
    assertThat(makeMessage(ImmutableList.of(), ImmutableList.of(simpleFact("foo"))))
        .isEqualTo("foo");
  }

  @Test
  public void twoFactsOneWithoutValue() {
    assertThat(
            makeMessage(
                ImmutableList.of(), ImmutableList.of(fact("hello", "there"), simpleFact("foo"))))
        .isEqualTo("hello: there\nfoo");
  }

  @Test
  public void newline() {
    assertThat(makeMessage(ImmutableList.of(), ImmutableList.of(fact("foo", "bar\nbaz"))))
        .isEqualTo("foo:\n    bar\n    baz");
  }

  @Test
  public void newlineWithoutValue() {
    assertThat(
            makeMessage(
                ImmutableList.of(),
                ImmutableList.of(fact("hello", "there\neveryone"), simpleFact("xyz"))))
        .isEqualTo("hello:\n    there\n    everyone\nxyz");
  }

  @Test
  public void withMessage() {
    assertThat(makeMessage(ImmutableList.of("hello"), ImmutableList.of(fact("foo", "bar"))))
        .isEqualTo("hello\nfoo: bar");
  }

  @Test
  public void formatNumericValue_null() {
    assertThat(formatNumericValue(null)).isEqualTo("null");
  }

  @Test
  public void formatNumericValue_zero() {
    assertThat(formatNumericValue(0)).isEqualTo("0");
    assertThat(formatNumericValue(0L)).isEqualTo("0");
    assertThat(formatNumericValue(-0)).isEqualTo("0");
    assertThat(formatNumericValue(-0L)).isEqualTo("0");
    assertThat(formatNumericValue(-0.0f)).isEqualTo("-0.0");
    assertThat(formatNumericValue(0.0f)).isEqualTo("0.0");
    assertThat(formatNumericValue(-0.0d)).isEqualTo("-0.0");
    assertThat(formatNumericValue(0.0d)).isEqualTo("0.0");
  }

  @Test
  @GwtIncompatible
  public void formatNumericValue_positive() {
    assertThat(formatNumericValue(9.99f)).isEqualTo("9.99");
    assertThat(formatNumericValue(9.99d)).isEqualTo("9.99");
    assertThat(formatNumericValue(9)).isEqualTo("9");
    assertThat(formatNumericValue(999L)).isEqualTo("999");
    assertThat(formatNumericValue(9599)).isEqualTo("9,599");
    assertThat(formatNumericValue(9599.99f)).isEqualTo("9,599.99");
    assertThat(formatNumericValue(9599.99d)).isEqualTo("9,599.99");
    assertThat(formatNumericValue(20000L)).isEqualTo("20,000");
    assertThat(formatNumericValue(802604)).isEqualTo("802,604");
    assertThat(formatNumericValue(1234567890)).isEqualTo("1,234,567,890");
    assertThat(formatNumericValue(1234567890L)).isEqualTo("1,234,567,890");
    assertThat(formatNumericValue(Integer.MAX_VALUE)).isEqualTo("2,147,483,647");
    assertThat(formatNumericValue(Long.MAX_VALUE)).isEqualTo("9,223,372,036,854,775,807");
    assertThat(formatNumericValue(Double.MAX_VALUE)).isEqualTo("1.7976931348623157E308");
    assertThat(formatNumericValue(Float.MAX_VALUE)).isEqualTo("3.4028235E38");
  }

  @Test
  @GwtIncompatible
  public void formatNumericValue_negative() {
    assertThat(formatNumericValue(-9)).isEqualTo("-9");
    assertThat(formatNumericValue(-999L)).isEqualTo("-999");
    assertThat(formatNumericValue(-9599)).isEqualTo("-9,599");
    assertThat(formatNumericValue(-9599.99f)).isEqualTo("-9,599.99");
    assertThat(formatNumericValue(-9599.99d)).isEqualTo("-9,599.99");
    assertThat(formatNumericValue(-20000L)).isEqualTo("-20,000");
    assertThat(formatNumericValue(-802604)).isEqualTo("-802,604");
    assertThat(formatNumericValue(-1234567890)).isEqualTo("-1,234,567,890");
    assertThat(formatNumericValue(-1234567890L)).isEqualTo("-1,234,567,890");
    assertThat(formatNumericValue(Integer.MIN_VALUE)).isEqualTo("-2,147,483,648");
    assertThat(formatNumericValue(Long.MIN_VALUE)).isEqualTo("-9,223,372,036,854,775,808");
  }

  @Test
  public void formatNumericValue_infinity() {
    assertThat(formatNumericValue(Double.POSITIVE_INFINITY)).isEqualTo("Infinity");
    assertThat(formatNumericValue(Double.NEGATIVE_INFINITY)).isEqualTo("-Infinity");
    assertThat(formatNumericValue(Float.POSITIVE_INFINITY)).isEqualTo("Infinity");
    assertThat(formatNumericValue(Float.NEGATIVE_INFINITY)).isEqualTo("-Infinity");
  }

  @Test
  public void formatNumericValue_nan() {
    assertThat(formatNumericValue(Double.NaN)).isEqualTo("NaN");
    assertThat(formatNumericValue(Float.NaN)).isEqualTo("NaN");
  }

  @Test
  public void formatNumericValue_throwsExceptionForNonNumericValue() {
    assertThrows(IllegalArgumentException.class, () -> formatNumericValue(new BigInteger("42")));
  }
}
