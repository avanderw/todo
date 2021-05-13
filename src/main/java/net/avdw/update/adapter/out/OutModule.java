package net.avdw.update.adapter.out;

import dagger.Binds;
import dagger.Module;
import net.avdw.update.adapter.out.github.GithubModule;
import net.avdw.update.adapter.out.jar.JarModule;
import net.avdw.update.port.out.DownloadPort;
import net.avdw.update.port.out.InstallPort;
import net.avdw.update.port.out.UnzipPort;

@Module (includes = {JarModule.class, GithubModule.class})
public abstract class OutModule {
    @Binds abstract DownloadPort downloadPort(HttpDownloadAdapter httpDownloadAdapter);
    @Binds abstract UnzipPort unzipPort(ZipAdapter zipAdapter);
    @Binds abstract InstallPort installPort(ScriptedInstallAdapter scriptedInstallAdapter);
}
