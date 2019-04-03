package net.avdw.todo.list;

import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ListFunc {
    private File file;

    public ListFunc(File file) {
        this.file = file;
    }

    public Set<String> list() {
        Set<String> list = new HashSet<>();
        try (Scanner scanner = new Scanner(file)) {
           while (scanner.hasNext()) {
               String lineItem = scanner.nextLine();
               if (!lineItem.isEmpty()) {
                   list.add(lineItem);
               }
           }
        } catch (FileNotFoundException e) {
            Logger.error(e);
        }
        return list;
    }

    public Set<String> list(List<String> arguments) {
        Set<String> list = new HashSet<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String lineItem = scanner.nextLine();
                if (!lineItem.isEmpty() && arguments.stream().allMatch(lineItem::contains)) {
                    list.add(lineItem);
                }
            }
        } catch (FileNotFoundException e) {
            Logger.error(e);
        }
        print(list);
        return list;
    }

    private void print(Set<String> list) {
        System.out.println(list);
    }
}
