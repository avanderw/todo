package net.avdw.update.adapter.out.jar;

import dagger.Binds;
import dagger.Module;
import net.avdw.update.port.out.CurrentReleasePort;

@Module
public abstract class JarModule {
    @Binds
    abstract CurrentReleasePort currentReleasePort(JarVersionAdapter jarVersionAdapter);
}
