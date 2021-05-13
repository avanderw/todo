package net.avdw.update.adapter.out.jar;

import net.avdw.update.domain.Release;
import net.avdw.update.port.out.CurrentReleasePort;

import javax.inject.Inject;

class JarVersionAdapter implements CurrentReleasePort {
    @Inject JarVersionAdapter() {

    }

    @Override
    public Release getRelease() {
        return new JarCurrentRelease();
    }
}
