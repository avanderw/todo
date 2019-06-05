package net.avdw.todo.config;

public class AProperty implements APropertyDao {
    private final APropertyRepository repository;
    private final String key;
    private String cache;

    AProperty(APropertyRepository repository, String key) {
        this.repository = repository;
        this.key = key;

        this.cache = repository.getProperty(key);
    }

    @Override
    public void set(String value) {
        this.cache = value;
        repository.saveProperty(key, value);
    }

    @Override
    public String get() {
        return cache;
    }
}
