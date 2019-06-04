package net.avdw.todo.tracking;

import com.google.inject.AbstractModule;

public class TrackModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TrackApi.class).to(TrackImpl.class);
    }
}
