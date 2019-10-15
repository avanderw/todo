package net.avdw.todo.number;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.function.Function;

public class NumberModule extends AbstractModule {
    @Provides
    @Singleton
    Function<Double, Double> defaultInterpolation() {
        return Interpolation.LINEAR;
    }
}
