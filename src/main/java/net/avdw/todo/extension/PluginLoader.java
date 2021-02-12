package net.avdw.todo.extension;

import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.extension.moscow.MoscowExt;
import net.avdw.todo.extension.moscow.MoscowSelector;
import net.avdw.todo.extension.moscow.MoscowMapper;

import java.util.HashSet;
import java.util.Set;

public class PluginLoader {

    public Set<Selector> getSelectorSet() {
        Set<Selector> selectorSet = new HashSet<>();
        MoscowExt moscowExt = new MoscowExt();
        selectorSet.add(new MoscowSelector(new MoscowMapper(moscowExt), moscowExt));
        return selectorSet;
    }
}
