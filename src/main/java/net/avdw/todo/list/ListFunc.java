package net.avdw.todo.list;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListFunc {
    private File todoFile;
    private final File doneFile;

    public ListFunc(File todoFile) {
        this.todoFile = todoFile;
        this.doneFile = todoFile.toPath().subpath(0, todoFile.toPath().getNameCount() - 1).resolve("done.txt").toFile();
    }

    public List<String> list() {
        return list(new ArrayList<>());
    }

    public List<String> list(List<String> filters) {
        return list(todoFile, filters, 0);
    }

    public List<String> listContexts() {
        List<String> contexts = extractToken("@");
        if (contexts.isEmpty()) {
            System.out.println("No context tags.");
        } else {
            contexts.forEach(System.out::println);
        }
        return contexts;
    }

    public List<String> listProjects() {
        List<String> projects = extractToken("+");
        if (projects.isEmpty()) {
            System.out.println("No project tags.");
        } else {
            projects.forEach(System.out::println);
        }
        return projects;
    }

    public List<String> listPriority() {
        Pattern pattern = Pattern.compile("^\\([A-Z]\\)");
        List<String> list = new ArrayList<>();
        try (Scanner scanner = new Scanner(todoFile)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    list.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            Logger.error(e);
        }
        print(list);
        return list;
    }

    public List<String> listAll() {
        List<String> list = new ArrayList<>();
        list.addAll(list(todoFile));
        list.addAll(list(doneFile, list.size()));
        print(list);
        return list;
    }

    private List<String> list(File file, List<String> filters, int initialCount) {
        List<String> list = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            int count = initialCount;
            while (scanner.hasNext()) {
                String lineItem = scanner.nextLine();
                if (lineItem.isEmpty()) {
                    continue;
                }
                count++;
                lineItem = String.format("[%s] %s", StringUtils.leftPad(Integer.toString(count), 2, "0"), lineItem);
                if (filters.isEmpty()) {
                    list.add(lineItem);
                } else if (filters.stream().allMatch(lineItem::contains)) {
                    list.add(lineItem);
                }
            }
        } catch (FileNotFoundException e) {
            Logger.error(e);
        }
        print(list);
        return list;
    }

    private List<String> extractToken(String startsWith) {
        Set<String> list = new HashSet<>();
        try (Scanner scanner = new Scanner(todoFile)) {
            while (scanner.hasNext()) {
                String token = scanner.next();
                if (token.startsWith(startsWith)) {
                    list.add(token);
                }
            }
        } catch (FileNotFoundException e) {
            Logger.error(e);
        }
        return new ArrayList<>(list);
    }

    private void print(List<String> list) {
        if (list.isEmpty()) {
            System.out.println("No todo items.");
            return;
        }
        list.sort(Comparator.comparing(s->s.substring(5)));
        list.forEach(System.out::println);
    }

    private List<String> list(File file) {
        return list(file, new ArrayList<>(), 0);
    }

    private List<String> list(File file, int initialCount) {
        return list(file, new ArrayList<>(), initialCount);
    }
}
