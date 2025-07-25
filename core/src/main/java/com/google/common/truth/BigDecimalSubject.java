/*
 * Copyright (c) 2015 Google, Inc.
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

import static com.google.common.truth.Fact.numericFact;
import static com.google.common.truth.Fact.simpleFact;

import java.math.BigDecimal;
import org.jspecify.annotations.Nullable;

/**
 * A subject for {@link BigDecimal} values.
 */
public final class BigDecimalSubject extends ComparableSubject<BigDecimal> {
  private final @Nullable BigDecimal actual;

  private BigDecimalSubject(FailureMetadata metadata, @Nullable BigDecimal actual) {
    super(metadata, actual);
    this.actual = actual;
  }

  /**
   * Checks that the actual value is equal to the value of the given {@link BigDecimal}. (i.e.,
   * checks that {@code actual.compareTo(expected) == 0}).
   *
   * <p><b>Note:</b> The scale of the BigDecimal is ignored. If you want to compare the values and
   * the scales, use {@link #isEqualTo(Object)}.
   */
  public void isEqualToIgnoringScale(@Nullable BigDecimal expected) {
    compareValues(expected);
  }

  /**
   * Checks that the actual value is equal to the value of the {@link BigDecimal} created from the
   * expected string (i.e., checks that {@code actual.compareTo(new BigDecimal(expected)) == 0}).
   *
   * <p><b>Note:</b> The scale of the BigDecimal is ignored. If you want to compare the values and
   * the scales, use {@link #isEqualTo(Object)}.
   */
  public void isEqualToIgnoringScale(String expected) {
    compareValues(new BigDecimal(expected));
  }

  /**
   * Checks that the actual value is equal to the value of the {@link BigDecimal} created from the
   * expected {@code long} (i.e., checks that {@code actual.compareTo(new BigDecimal(expected)) ==
   * 0}).
   *
   * <p><b>Note:</b> The scale of the BigDecimal is ignored. If you want to compare the values and
   * the scales, use {@link #isEqualTo(Object)}.
   */
  public void isEqualToIgnoringScale(long expected) {
    compareValues(new BigDecimal(expected));
  }

  /**
   * Checks that the actual value (including scale) is equal to the given {@link BigDecimal}.
   *
   * <p><b>Note:</b> If you only want to compare the values of the BigDecimals and not their scales,
   * use {@link #isEqualToIgnoringScale(BigDecimal)} instead.
   */
  @Override // To express more specific javadoc
  public void isEqualTo(@Nullable Object expected) {
    super.isEqualTo(expected);
  }

  /**
   * Checks that the actual value is equivalent to the given value according to {@link
   * Comparable#compareTo}, (i.e., checks that {@code a.compareTo(b) == 0}). This method behaves
   * identically to (the more clearly named) {@link #isEqualToIgnoringScale(BigDecimal)}.
   *
   * <p><b>Note:</b> Do not use this method for checking object equality. Instead, use {@link
   * #isEqualTo(Object)}.
   */
  @Override
  public void isEquivalentAccordingToCompareTo(@Nullable BigDecimal expected) {
    compareValues(expected);
  }

  private void compareValues(@Nullable BigDecimal expected) {
    if (actual == null || expected == null) {
      // This won't mention "(scale is ignored)" if it fails, but that seems tolerable or even good?
      isEqualTo(expected);
    } else if (actual.compareTo(expected) != 0) {
      failWithoutActual(
          numericFact("expected", expected),
          numericFact("but was", actual),
          simpleFact("(scale is ignored)"));
    }
  }

  static Factory<BigDecimalSubject, BigDecimal> bigDecimals() {
    return BigDecimalSubject::new;
  }
}
