package com.bordozer.measury.stopwatcher;

import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@TypeQualifierDefault(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WatchEntryPoint {
    String value();
}
