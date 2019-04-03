package net.avdw.todo.list;

import com.sun.istack.internal.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ListFunc {
    private File file;

    public ListFunc(File file) {
        this.file = file;
    }

    public List<String> list() {
        return list(new ArrayList<>());
    }

    public List<String> list(@NotNull List<String> filters) {
        List<String> list = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            int count = 0;
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

    private void print(List<String> list) {
        list.forEach(System.out::println);
    }
}
