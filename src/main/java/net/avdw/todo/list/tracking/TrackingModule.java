package net.avdw.todo.list.tracking;

import com.google.inject.AbstractModule;

public class TrackingModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TrackApi.class).to(TrackImpl.class);
    }
}
