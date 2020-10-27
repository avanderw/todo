package net.avdw.todo.plugin.state;

import net.avdw.todo.PropertyFile;
import net.avdw.todo.domain.Todo;
import org.checkerframework.framework.qual.Unused;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class StateMapper {
    private final String baseState;
    private final int maxWidth;
    private final Map<String, String> statePatternMap = new HashMap<>();

    StateMapper() {
        PropertyFile propertyFile = new PropertyFile("net.avdw/todo");
        Properties properties = propertyFile.read("state");

        int maxWidth = 0;
        String baseState = "";
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String state = (String) entry.getKey();
            String regex = (String) entry.getValue();
            maxWidth = Math.max(maxWidth, state.length());
            if (regex.equals(".*")) {
                baseState = state;
            } else {
                statePatternMap.put(state, regex);
            }
        }
        this.baseState = baseState;
        this.maxWidth = maxWidth;
    }

    public String map(final Todo todo) {
        List<String> matchingStateList = statePatternMap.entrySet().stream()
                .filter(entry -> todo.getText().matches(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (matchingStateList.isEmpty()) {
            return baseState;
        } else if (matchingStateList.size() > 1) {
            throw new UnsupportedOperationException();
        } else {
            return matchingStateList.get(0);
        }
    }

    public int maxWidth() {
        return maxWidth;
    }
}
