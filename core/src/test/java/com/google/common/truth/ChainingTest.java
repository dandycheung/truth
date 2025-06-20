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

import static com.google.common.truth.Fact.simpleFact;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import org.jspecify.annotations.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for chained subjects (produced with {@link Subject#check(String, Object...)}, etc.). */
@RunWith(JUnit4.class)
public final class ChainingTest {
  private static final Throwable throwable = new Throwable("root");

  @Test
  public void noChaining() {
    AssertionError e =
        expectFailure(whenTesting -> whenTesting.that("root").isThePresentKingOfFrance());
    assertNoCause(e, "message");
  }

  @Test
  public void noChainingRootThrowable() {
    AssertionError e =
        expectFailure(whenTesting -> whenTesting.that(throwable).isThePresentKingOfFrance());
    assertHasCause(e, "message");
  }

  // e.g., future.failureCause()

  @Test
  public void oneLevelNamed() {
    AssertionError e =
        expectFailure(
            whenTesting ->
                whenTesting
                    .that("root")
                    .delegatingToNamed("child", "child")
                    .isThePresentKingOfFrance());
    assertNoCause(e, "value of    : myObject.child\nmessage\nmyObject was: root");
  }

  @Test
  public void twoLevelsNamed() {
    AssertionError e =
        expectFailure(
            whenTesting ->
                whenTesting
                    .that("root")
                    .delegatingToNamed("child", "child")
                    .delegatingToNamed("grandchild", "grandchild")
                    .isThePresentKingOfFrance());
    assertNoCause(e, "value of    : myObject.child.grandchild\nmessage\nmyObject was: root");
  }

  @Test
  public void oneLevelNamedNoNeedToDisplayBoth() {
    AssertionError e =
        expectFailure(
            whenTesting ->
                whenTesting
                    .that("root")
                    .delegatingToNamedNoNeedToDisplayBoth("child", "child")
                    .isThePresentKingOfFrance());
    assertNoCause(e, "value of: myObject.child\nmessage");
  }

  @Test
  public void twoLevelsNamedNoNeedToDisplayBoth() {
    AssertionError e =
        expectFailure(
            whenTesting ->
                whenTesting
                    .that("root")
                    .delegatingToNamedNoNeedToDisplayBoth("child", "child")
                    .delegatingToNamedNoNeedToDisplayBoth("grandchild", "grandchild")
                    .isThePresentKingOfFrance());
    assertNoCause(e, "value of: myObject.child.grandchild\nmessage");
  }

  @Test
  public void twoLevelsNamedOnlyFirstNoNeedToDisplayBoth() {
    AssertionError e =
        expectFailure(
            whenTesting ->
                whenTesting
                    .that("root")
                    .delegatingToNamedNoNeedToDisplayBoth("child", "child")
                    .delegatingToNamed("grandchild", "grandchild")
                    .isThePresentKingOfFrance());
    assertNoCause(e, "value of    : myObject.child.grandchild\nmessage\nmyObject was: root");
  }

  @Test
  public void twoLevelsNamedOnlySecondNoNeedToDisplayBoth() {
    AssertionError e =
        expectFailure(
            whenTesting ->
                whenTesting
                    .that("root")
                    .delegatingToNamed("child", "child")
                    .delegatingToNamedNoNeedToDisplayBoth("grandchild", "grandchild")
                    .isThePresentKingOfFrance());
    assertNoCause(e, "value of    : myObject.child.grandchild\nmessage\nmyObject was: root");
  }

  @Test
  public void namedAndMessage() {
    AssertionError e =
        ExpectFailure.expectFailure(
            whenTesting ->
                whenTesting
                    .withMessage("prefix")
                    .about(myObjects())
                    .that("root")
                    .delegatingToNamed("child", "child")
                    .isThePresentKingOfFrance());
    assertNoCause(e, "prefix\nvalue of    : myObject.child\nmessage\nmyObject was: root");
  }

  @Test
  public void checkFailWithName() {
    AssertionError e = expectFailure(whenTesting -> whenTesting.that("root").doCheckFail("child"));
    assertNoCause(e, "message\nvalue of    : myObject.child\nmyObject was: root");
  }

  @SuppressWarnings("LenientFormatStringValidation") // Intentional for testing.
  @Test
  public void badFormat() {
    assertThrows(IllegalArgumentException.class, () -> assertThat("root").check("%s %s", 1, 2, 3));
  }

  /*
   * TODO(cpovirk): It would be nice to have multiple Subject subclasses so that we know we're
   * pulling the type from the right link in the chain. But we get some coverage of that from other
   * tests like MultimapSubjectTest.
   */

  private static final class MyObjectSubject extends Subject {
    private MyObjectSubject(FailureMetadata metadata, @Nullable Object actual) {
      super(metadata, actual);
    }

    /** Runs a check that always fails with the generic message "message." */
    void isThePresentKingOfFrance() {
      failWithoutActual(simpleFact("message"));
    }

    void doCheckFail(String name) {
      check(name).withMessage("message").fail();
    }

    /**
     * Returns a new {@link MyObjectSubject} for the given actual value, chaining it to the current
     * subject with {@link Subject#check}.
     */
    MyObjectSubject delegatingToNamed(Object actual, String name) {
      return check(name).about(myObjects()).that(actual);
    }

    MyObjectSubject delegatingToNamedNoNeedToDisplayBoth(Object actual, String name) {
      return checkNoNeedToDisplayBothValues(name).about(myObjects()).that(actual);
    }
  }

  private static Subject.Factory<MyObjectSubject, Object> myObjects() {
    return MyObjectSubject::new;
  }

  private static AssertionError expectFailure(
      ExpectFailure.SimpleSubjectBuilderCallback<MyObjectSubject, Object> assertionCallback) {
    return ExpectFailure.expectFailureAbout(myObjects(), assertionCallback);
  }

  private static void assertNoCause(AssertionError failure, String message) {
    assertThat(failure).hasMessageThat().isEqualTo(message);
    assertThat(failure).hasCauseThat().isNull();
  }

  private static void assertHasCause(AssertionError failure, String message) {
    assertThat(failure).hasMessageThat().isEqualTo(message);
    assertThat(failure).hasCauseThat().isEqualTo(throwable);
  }
}
