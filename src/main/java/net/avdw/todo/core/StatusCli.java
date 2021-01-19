package net.avdw.todo.core;

import com.google.inject.Inject;
import net.avdw.property.PropertyFile;
import net.avdw.todo.PropertyKey;
import net.avdw.todo.domain.IsDone;
import net.avdw.todo.domain.IsParked;
import net.avdw.todo.domain.IsPriority;
import net.avdw.todo.domain.IsRemoved;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Command(name = "status", resourceBundle = "messages", description = "${bundle:status}",
        mixinStandardHelpOptions = true)
public class StatusCli implements Runnable {

    @Spec private CommandSpec spec;
    @Inject private Path todoPath;
    @Inject private Repository<Integer, Todo> todoRepository;

    @Override
    public void run() {
        Specification<Integer, Todo> any = new Any<>();

        long priority = todoRepository.findAll(new IsPriority()).size();
        long active = todoRepository.findAll(any).size();
        long done = todoRepository.findAll(new IsDone()).size();
        long removed = todoRepository.findAll(new IsRemoved()).size();
        long parked = todoRepository.findAll(new IsParked()).size();
        long toArchive = done + removed + parked;

        String status = String.format("On list \"%s\"%n", todoPath.toAbsolutePath());
        if (active > 0) {
            status += String.format("There are %s active items%n", active);
        }

        if (priority > 0) {
            if (priority == 1) {
                status += String.format("%nThere is %s priority item%n", priority);
            } else {
                status += String.format("%nThere are %s priority items%n", priority);
            }
            status += String.format("  (use \"todo pri...\" to view)%n");
            status += String.format("  (use \"todo pri -R...\" to remove all priority)%n");
        }

        if (toArchive > 0) {
            status += String.format("%nChanges to be archived:%n");
            status += String.format("  (use \"todo archive...\" to archive inactive items)%n");
            if (done > 0) {
                status += String.format("    %s done items%n", done);
            }
            if (removed > 0) {
                status += String.format("    %s removed items%n", removed);
            }
            if (parked > 0) {
                status += String.format("    %s parked items%n", parked);
            }
        }

        List<Path> otherList = new ArrayList<>();
        PropertyFile propertyFile = new PropertyFile("net.avdw");
        Properties properties = propertyFile.read("todo");
        if (properties.containsKey(PropertyKey.KNOWN_LISTS)) {
            String lists = properties.getProperty(PropertyKey.KNOWN_LISTS);
            if (!lists.isEmpty()) {
                for (String path : lists.split(";")) {
                    otherList.add(Paths.get(path));
                }
            }
        }


        if (otherList.size() > 0) {
            status += String.format("%nOther lists:%n");
            status += String.format("  (use \"cd <path>...\" to switch to them)%n");
            status += otherList.stream().map(Path::toString).reduce("", (a, b) -> a + String.format("    %s%n", b));
        }

        spec.commandLine().getOut().println(status);
    }
}
