package org.apache.tamaya.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for easily collecting View meta info.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewConfig {

    enum CreateMode {CREATE, LAZY, EAGER}

    String uri();
    String displayName();
    CreateMode createMode() default CreateMode.CREATE;
}