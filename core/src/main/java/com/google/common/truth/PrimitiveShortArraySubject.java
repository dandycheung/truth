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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.primitives.Shorts;
import org.jspecify.annotations.Nullable;

/**
 * A subject for {@code short[]}.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
public final class PrimitiveShortArraySubject extends AbstractArraySubject {
  private final short @Nullable [] actual;

  private PrimitiveShortArraySubject(FailureMetadata metadata, short @Nullable [] o) {
    super(metadata, o, /* typeDescriptionOverride= */ "array");
    this.actual = o;
  }

  public IterableSubject asList() {
    return checkNoNeedToDisplayBothValues("asList()").that(Shorts.asList(checkNotNull(actual)));
  }

  static Factory<PrimitiveShortArraySubject, short[]> shortArrays() {
    return PrimitiveShortArraySubject::new;
  }
}
