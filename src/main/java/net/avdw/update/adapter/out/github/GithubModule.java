package net.avdw.update.adapter.out.github;

import dagger.Binds;
import dagger.Module;
import net.avdw.update.port.out.LatestReleasePort;

@Module
public abstract class GithubModule {
    @Binds abstract LatestReleasePort latestReleasePort(GithubLatestReleaseAdapter githubLatestReleaseAdapter);
}
