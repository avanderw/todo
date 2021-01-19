package net.avdw.todo.plugin.state;

import com.google.inject.Singleton;
import net.avdw.property.PropertyFile;
import net.avdw.todo.domain.Todo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Singleton
public class StateMapper {
    private final int maxWidth;
    private final Map<String, String> statePatternMap = new HashMap<>();

    StateMapper() {
        PropertyFile propertyFile = new PropertyFile("net.avdw/todo");
        Properties properties = propertyFile.read("state");

        int maxWidth = 0;
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String state = (String) entry.getKey();
            String regex = (String) entry.getValue();
            maxWidth = Math.max(maxWidth, state.length());
            statePatternMap.put(state, regex);
        }
        this.maxWidth = maxWidth;
    }

    public String map(final Todo todo) {
        List<String> matchingStateList = statePatternMap.entrySet().stream()
                .filter(entry -> todo.getText().matches(entry.getValue()))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());

        if (matchingStateList.isEmpty()) {
            return null;
        } else {
            return matchingStateList.get(matchingStateList.size() - 1);
        }
    }

    public int maxWidth() {
        return maxWidth;
    }
}
