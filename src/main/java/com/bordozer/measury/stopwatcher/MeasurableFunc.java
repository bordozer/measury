package com.bordozer.measury.stopwatcher;

@FunctionalInterface
public interface MeasurableFunc<T> {

    T executeAndReturn();
}
