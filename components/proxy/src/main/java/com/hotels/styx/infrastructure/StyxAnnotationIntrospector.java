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
package com.hotels.styx.infrastructure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.introspect.*;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.hotels.styx.api.service.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

public class StyxAnnotationIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public String findEnumValue(Enum<?> value) {
        // 11-Jun-2015, tatu: As per [databind#677], need to allow explicit naming.
        //   Unfortunately can not quite use standard AnnotatedClass here (due to various
        //   reasons, including odd representation JVM uses); has to do for now
        try {
            // We know that values are actually static fields with matching name so:
            Field f = value.getClass().getField(value.name());
            if (f != null) {
                StyxProperty prop = f.getAnnotation(StyxProperty.class);
                if (prop != null) {
                    String n = prop.value();
                    if (n != null && !n.isEmpty()) {
                        return n;
                    }
                }
            }
        } catch (SecurityException e) {
            // 17-Sep-2015, tatu: Anything we could/should do here?
        } catch (NoSuchFieldException e) {
            // 17-Sep-2015, tatu: should not really happen. But... can we do anything?
        }
        return value.name();
    }

    @Override // since 2.7
    public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
        HashMap<String,String> expl = null;
        for (Field f : ClassUtil.getDeclaredFields(enumType)) {
            if (!f.isEnumConstant()) {
                continue;
            }
            StyxProperty prop = f.getAnnotation(StyxProperty.class);
            if (prop == null) {
                continue;
            }
            String n = prop.value();
            if (n.isEmpty()) {
                continue;
            }
            if (expl == null) {
                expl = new HashMap<String,String>();
            }
            expl.put(f.getName(), n);
        }
        // and then stitch them together if and as necessary
        if (expl != null) {
            for (int i = 0, end = enumValues.length; i < end; ++i) {
                String defName = enumValues[i].name();
                String explValue = expl.get(defName);
                if (explValue != null) {
                    names[i] = explValue;
                }
            }
        }
        return names;
    }
    @Override
    public Boolean hasRequiredMarker(AnnotatedMember m)
    {
        StyxProperty ann = _findAnnotation(m, StyxProperty.class);
        if (ann != null) {
            return ann.required();
        }
        return null;
    }

    @Override
    public JsonProperty.Access findPropertyAccess(Annotated m) {
        StyxProperty ann = _findAnnotation(m, StyxProperty.class);
        if (ann != null) {
            return JsonProperty.Access.valueOf(ann.access().name());
        }
        return null;
    }


    @Override
    public Integer findPropertyIndex(Annotated ann) {
        StyxProperty prop = _findAnnotation(ann, StyxProperty.class);
        if (prop != null) {
            int ix = prop.index();
            if (ix != StyxProperty.INDEX_UNKNOWN) {
                return Integer.valueOf(ix);
            }
        }
        return null;
    }

    @Override
    public String findPropertyDefaultValue(Annotated ann) {
        StyxProperty prop = _findAnnotation(ann, StyxProperty.class);
        if (prop == null) {
            return null;
        }
        String str = prop.defaultValue();
        // Since annotations do not allow nulls, need to assume empty means "none"
        return str.isEmpty() ? null : str;
    }

        /*
    /**********************************************************
    /* Serialization: property annotations
    /**********************************************************
     */

    @Override
    public PropertyName findNameForSerialization(Annotated a) {
        StyxProperty pann = _findAnnotation(a, StyxProperty.class);
        if (pann != null) {
            return PropertyName.construct(pann.value());
        }
        return super.findNameForSerialization(a);
    }

    /*
    /**********************************************************
    /* Deserialization: property annotations
    /**********************************************************
     */

    @Override
    public PropertyName findNameForDeserialization(Annotated a)
    {
        StyxProperty pann = _findAnnotation(a, StyxProperty.class);
        if (pann != null) {
            return PropertyName.construct(pann.value());
        }
        return super.findNameForDeserialization(a);
    }

    @Override
    public boolean hasCreatorAnnotation(Annotated a)
    {
        /* No dedicated disabling; regular @JsonIgnore used
         * if needs to be ignored (and if so, is handled prior
         * to this method getting called)
         */
        StyxCreator ann = _findAnnotation(a, StyxCreator.class);
        if (ann != null) {
            return (ann.mode() != StyxCreator.Mode.DISABLED);
        }
        // 19-Apr-2016, tatu: As per [databind#1197], [databind#1122] (and some related),
        //    may or may not consider it a creator
        return super.hasCreatorAnnotation(a);
    }

    @Override
    public JsonCreator.Mode findCreatorBinding(Annotated a) {
        StyxCreator ann = _findAnnotation(a, StyxCreator.class);
        return (ann == null) ? null : JsonCreator.Mode.valueOf(ann.mode().name());
    }

    @Override
    public Class<?> findPOJOBuilder(AnnotatedClass ac)
    {
        StyxDeserialize ann = _findAnnotation(ac, StyxDeserialize.class);
        return (ann == null) ? super.findPOJOBuilder(ac) : _classIfExplicit(ann.builder());
    }

    @Override
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac)
    {
        StyxPOJOBuilder ann = _findAnnotation(ac, StyxPOJOBuilder.class);
        if (ann != null) {
            JsonPOJOBuilder.Value value = new JsonPOJOBuilder.Value(new JsonPOJOBuilder() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return JsonPOJOBuilder.class;
                }

                @Override
                public String buildMethodName() {
                    return ann.buildMethodName();
                }

                @Override
                public String withPrefix() {
                    return ann.withPrefix();
                }
            });
            return value;
        }
        return null;
    }
        /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    protected boolean _isIgnorable(Annotated a)
    {
        StyxIgnore ann = _findAnnotation(a, StyxIgnore.class);
        if (ann != null) {
            return ann.value();
        }
        return super._isIgnorable(a);
    }
}
