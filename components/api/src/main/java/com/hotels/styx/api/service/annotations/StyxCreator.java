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
 * Marker annotation that can be used to define constructors and factory
 * methods as one to use for instantiating new instances of the associated
 * class.
 *<p>
 * NOTE: when annotating creator methods (constructors, factory methods),
 * method must either be:
 * Also note that all {@link StyxProperty} annotations must specify actual name
 * (NOT empty String for "default") unless you use one of extension modules
 * that can detect parameter name; this because default JDK versions before 8
 * have not been able to store and/or retrieve parameter names from bytecode.
 * But with JDK 8 (or using helper libraries such as Paranamer, or other JVM
 * languages like Scala or Kotlin), specifying name is optional.
 * NOTE: As of Jackson 2.6, use of {@link StyxProperty#required()} is supported
 * for Creator methods (but not necessarily for regular setters or fields!).
 *
 * @see StyxCreator
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface StyxCreator
{
    /**
     * Property that is used to indicate how argument(s) is/are bound for creator,
     * in cases there may be multiple alternatives. Currently the one case is that
     * of a single-argument creator method, for which both so-called "delegating" and
     * "property-based" bindings are possible: since
     * delegating mode can not be used for multi-argument creators, the only choice
     * there is "property-based" mode.
     * Check {@link StyxCreator.Mode} for more complete explanation of possible choices.
     *<p>
     * Default value of {@link StyxCreator.Mode#DEFAULT} means that caller is to use standard
     * heuristics for choosing mode to use.
     *
     * @since 2.5
     */
    public Mode mode() default StyxCreator.Mode.DEFAULT;

    /**
     * @since 2.5
     */
    public enum Mode {
        /**
         * Pseudo-mode that indicates that caller is to use default heuristics for
         * choosing mode to use. This typically favors use of delegating mode for
         * single-argument creators that take structured types.
         */
        DEFAULT,

        /**
         * Mode that indicates that if creator takes a single argument, the whole incoming
         * data value is to be bound into declared type of that argument; this "delegate"
         * value is then passed as the argument to creator.
         */
        DELEGATING,

        /**
         * Mode that indicates that the argument(s) for creator are to be bound from matching
         * properties of incoming Object value, using creator argument names (explicit or implicit)
         * to match incoming Object properties to arguments.
         *<p>
         * Note that this mode is currently (2.5) always used for multiple-argument creators;
         * the only ambiguous case is that of a single-argument creator.
         */
        PROPERTIES,

        /**
         * Pseudo-mode that indicates that creator is not to be used. This can be used as a result
         * value for explicit disabling, usually either by custom annotation introspector,
         * or by annotation mix-ins (for example when choosing different creator).
         */
        DISABLED
    }
}

