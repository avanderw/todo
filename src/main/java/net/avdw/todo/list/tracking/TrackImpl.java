package net.avdw.todo.list.tracking;

import com.google.inject.Inject;
import net.avdw.todo.config.AProperty;

public class TrackImpl implements TrackApi {
    private AProperty trackProperty;

    @Inject
    TrackImpl(@TrackedList AProperty trackProperty) {
        this.trackProperty = trackProperty;
    }

    @Override
    public void track(String list) {
        trackProperty.set(list);
    }
}
