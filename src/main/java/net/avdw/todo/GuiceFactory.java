package net.avdw.todo;

import picocli.CommandLine;

public class GuiceFactory implements CommandLine.IFactory {
    @Override
    public <K> K create(Class<K> aClass) throws Exception {
        return null;
    }
}
