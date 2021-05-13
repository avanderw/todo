package net.avdw.update.adapter.out;

import lombok.SneakyThrows;
import net.avdw.update.port.out.InstallPort;

import javax.inject.Inject;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

class ScriptedInstallAdapter implements InstallPort {
    @Inject ScriptedInstallAdapter() {

    }

    @SneakyThrows
    @Override
    public void installTo(final Path installPath) {
        InputStream source = getClass().getResourceAsStream("/scripts/update.bat");
        Path destination = Paths.get(installPath.resolve("update.bat").toString());
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command("cmd.exe", "/c", "update.bat > todo-jar-with-dependencies.jar-update.log");
        builder.directory(installPath.toFile());
        builder.start();
    }
}
