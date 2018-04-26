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
 * Annotation use for configuring deserialization aspects, by attaching
 * to "setter" methods or fields, or to value classes.
 * When annotating value classes, configuration is used for instances
 * of the value class but can be overridden by more specific annotations
 * (ones that attach to methods or fields).
 *<p>
 * An example annotation would be:
 *<pre>
 *  &#64;JsonDeserialize(using=MySerializer.class,
 *    as=MyHashMap.class,
 *    keyAs=MyHashKey.class,
 *    contentAs=MyHashValue.class
 *  )
 *</pre>
 *<p>
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StyxDeserialize
{
    /**
     * Annotation for specifying if an external Builder class is to
     * be used for building up deserialized instances of annotated
     * class. If so, an instance of referenced class is first constructed
     * (possibly using a Creator method; or if none defined, using default
     * constructor), and its "with-methods" are used for populating fields;
     * and finally "build-method" is invoked to complete deserialization.
     */
    public Class<?> builder() default Void.class;
}
