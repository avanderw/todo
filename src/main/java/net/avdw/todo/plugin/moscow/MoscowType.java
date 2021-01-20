package net.avdw.todo.plugin.moscow;

public enum MoscowType {
    MUST("non-negotiable, mandatory"),
    SHOULD("important, not vital, significant value"),
    COULD("nice to have, small impact"),
    WONT("not priority given time frame");

    private final String desc;

    MoscowType(final String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return String.format("%6s (%s)", name(), desc);
    }
}
