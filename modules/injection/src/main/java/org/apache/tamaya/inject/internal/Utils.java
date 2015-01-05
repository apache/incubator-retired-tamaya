/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.inject.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class simplifying some implementation aspects.
 * Created by Anatole on 11.11.2014.
 */
@SuppressWarnings("unchecked")
public final class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    private Utils(){}

    /**
     * Utility method to read out repeatable annotations.
     * @param annotated the annotated instance.
     * @param repeatableAnnotation the repeatable annotation type
     * @param annotationContainer the container annotation type
     * @param <T> the repeatable annotation type
     * @param <R> the repeatable container annotation type
     * @return a list with the annotations found (could be empty, but never null).
     */
	public static <T extends Annotation, R extends Annotation> Collection<T>
            getAnnotations(AnnotatedElement annotated,
                              Class<T> repeatableAnnotation,
                              Class<R> annotationContainer){
        List<T> result = new ArrayList<>();
        R containerAnnot = annotated.getAnnotation(annotationContainer);
        if(containerAnnot!=null){
            Method valueMethod;
            try {
                valueMethod = annotationContainer.getMethod("keys");
                result.addAll(Arrays.asList((T[])valueMethod.invoke(containerAnnot)));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to evaluate repeatable annotation.", e);
            }
        }
        else{
            T annot = annotated.getAnnotation(repeatableAnnotation);
            if(annot!=null){
                result.add(annot);
            }
        }
        return result;
    }

    /**
     * Utility method to read out repeatable annotations.
     * @param annotated the annotated instance.
     * @param repeatableAnnotation the repeatable annotation type
     * @param annotationContainer the container annotation type
     * @param <T> the repeatable annotation type
     * @param <R> the repeatable container annotation type
     * @return a list with the annotations found (could be empty, but never null).
     */
    public static <T extends Annotation, R extends Annotation> Collection<T>
    getAnnotations(AccessibleObject annotated,
                   Class<T> repeatableAnnotation,
                   Class<R> annotationContainer){
        List<T> result = new ArrayList<>();
        R containerAnnot = annotated.getAnnotation(annotationContainer);
        if(containerAnnot!=null){
            Method valueMethod;
            try {
                valueMethod = annotationContainer.getMethod("keys");
                result.addAll(Arrays.asList((T[])valueMethod.invoke(containerAnnot)));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to evaluate repeatable annotation.", e);
            }
        }
        else{
            T annot = annotated.getAnnotation(repeatableAnnotation);
            if(annot!=null){
                result.add(annot);
            }
        }
        return result;
    }

    /**
     * Utility method to read out repeatable annotations.
     * @param annotationType the annotation type.
     * @param objects the accessible objects to be looked up
     * @param <T> the repeatable annotation type
     * @return a list with the annotations found (could be empty, but never null).
     */
    public static <T extends Annotation> T getAnnotation(
                   Class<T> annotationType, AnnotatedElement... objects){
        for(AnnotatedElement obj:objects){
            T annot = obj.getAnnotation(annotationType);
            if(annot!=null){
                return annot;
            }
        }
        return null;
    }
}
