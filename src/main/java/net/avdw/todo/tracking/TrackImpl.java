package net.avdw.todo.tracking;

import com.google.inject.Inject;
import net.avdw.todo.property.AProperty;

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
