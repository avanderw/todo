package net.avdw.todo.number;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.function.Function;

@Module
public class NumberModule {
    @Provides
    @Singleton
    Function<Double, Double> defaultInterpolation() {
        return Interpolation.LINEAR;
    }
}
