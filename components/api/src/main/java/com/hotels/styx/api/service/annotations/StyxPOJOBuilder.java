/*
  Copyright (C) 2013-2018 Expedia Inc.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.hotels.styx.api.service.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to configure details of a Builder class:
 * instances of which are used as Builders for deserialized
 * POJO values, instead of POJOs being instantiated using
 * constructors or factory methods.
 * Note that this annotation is NOT used to define what is
 * the Builder class for a POJO: rather, this is determined
 * by {@link StyxDeserialize#builder} property of {@link StyxDeserialize}.
 * <p>
 * Annotation is typically used if the naming convention
 * of a Builder class is different from defaults:
 * <ul>
 * </ul>
 *
 * @since 2.0
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StyxPOJOBuilder {
    /**
     * Property to use for re-defining which zero-argument method
     * is considered the actual "build-method": method called after
     * all data has been bound, and the actual instance needs to
     * be instantiated.
     * <p>
     * Default value is "build".
     */
    public String buildMethodName() default "build";

    /**
     * Property used for (re)defining name prefix to use for
     * auto-detecting "with-methods": methods that are similar to
     * "set-methods" (in that they take an argument), but that
     * may also return the new builder instance to use
     * (which may be 'this', or a new modified builder instance).
     * Note that in addition to this prefix, it is also possible
     * to use {@link StyxProperty}
     * annotation to indicate "with-methods" (as well as
     * {@link StyxProperty}).
     * <p>
     * Default value is "with", so that method named "withValue()"
     * would be used for binding JSON property "value" (using type
     * indicated by the argument; or one defined with annotations.
     */
    public String withPrefix() default "with";

    /*
    /**********************************************************
    /* Helper classes
    /**********************************************************
     */

    /**
     * Simple value container for containing values read from
     * {@link StyxPOJOBuilder} annotation instance.
     */
    public class Value {
        public final String buildMethodName;
        public final String withPrefix;

        public Value(StyxPOJOBuilder ann) {
            buildMethodName = ann.buildMethodName();
            withPrefix = ann.withPrefix();
        }
    }
}
