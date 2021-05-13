package net.avdw.update;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import lombok.SneakyThrows;
import net.avdw.update.adapter.out.OutModule;
import net.avdw.update.port.in.UpdateAvailableQuery;
import net.avdw.update.port.in.UpdateToLatestUseCase;

import javax.inject.Singleton;
import java.io.File;
import java.nio.file.Path;

@Module(includes = {OutModule.class})
public abstract class UpdateModule {
    @Binds abstract UpdateAvailableQuery updateAvailableQuery(UpdateService updateService);
    @Binds abstract UpdateToLatestUseCase updateToLatestUseCase(UpdateService updateService);

    @SneakyThrows
    @Provides @Singleton @UpdateFeature
    static Path jarPath() {
        return new File(UpdateModule.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath().getParent();
    }
}
