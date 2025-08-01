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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.model.Statement;

/** Test that stack traces are included in the error message created by {@link Expect}. */
@RunWith(JUnit4.class)
public class ExpectFailureWithStackTraceTest {
  private static final String METHOD_NAME = "ExpectFailureWithStackTraceTest.expectTwoFailures";

  @Rule public final FailingExpect failToExpect = new FailingExpect();

  @Test
  @SuppressWarnings("TruthSelfEquals")
  public void expectTwoFailures() {
    failToExpect.delegate.that(4).isNotEqualTo(4);
    failToExpect.delegate.that("abc").contains("x");
  }

  /** Expect class that can examine the error message */
  public static class FailingExpect implements TestRule {
    final Expect delegate = Expect.create();

    @Override
    public Statement apply(Statement base, Description description) {
      Statement s = delegate.apply(base, description);
      return new Statement() {
        @Override
        public void evaluate() throws Throwable {
          AssertionError e = assertThrows(AssertionError.class, () -> s.evaluate());
          // Check that error message contains stack traces. Method name should appear twice,
          // once for each expect error.
          int firstIndex = e.getMessage().indexOf(METHOD_NAME);
          assertThat(firstIndex).isGreaterThan(0);
          int secondIndex = e.getMessage().indexOf(METHOD_NAME, firstIndex + 1);
          assertThat(secondIndex).isGreaterThan(firstIndex);
        }
      };
    }
  }
}
