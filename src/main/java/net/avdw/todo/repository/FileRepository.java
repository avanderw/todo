package net.avdw.todo.repository;

import com.google.inject.Inject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileRepository implements ARepository {
    private final String repositoryPathName;
    private final Path repositoryPath;

    @Inject
    FileRepository(@RepositoryPath String repositoryPathName) {
        this.repositoryPathName = repositoryPathName;
        this.repositoryPath = resolveRepositoryPath(Paths.get(""));
    }

    @Override
    public boolean exists() {
        return Files.exists(repositoryPath);
    }

    @Override
    public Path getPath() {
        return repositoryPath;
    }

    private Path resolveRepositoryPath(Path currentPath) {
        Path localRepositoryPath = currentPath.resolve(repositoryPathName);
        if (Files.exists(localRepositoryPath)) {
            return localRepositoryPath;
        } else if (currentPath.getParent() != null) {
            return resolveRepositoryPath(currentPath.getParent());
        } else {
            Path globalPath = Paths.get(System.getProperty("user.home")).resolve(repositoryPathName);
            if (Files.exists(globalPath)) {
                return globalPath;
            } else {
                return Paths.get("/invalid/path");
            }
        }
    }
}
