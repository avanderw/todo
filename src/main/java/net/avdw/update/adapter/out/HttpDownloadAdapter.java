package net.avdw.update.adapter.out;

import lombok.SneakyThrows;
import net.avdw.todo.SuppressFBWarnings;
import net.avdw.update.port.out.DownloadPort;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
class HttpDownloadAdapter implements DownloadPort {
    @Inject
    HttpDownloadAdapter() {
    }

    @Override
    @SneakyThrows
    public void downloadFrom(final URL url, final Path fileZip) {
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileZip.toFile());
             FileChannel fileChannel = fileOutputStream.getChannel()) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
    }
}
