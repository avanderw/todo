package net.avdw.todo.plugin.plan;

public enum PlanType {
    STRATEGIC("creating value"),
    TACTICAL("refining value"),
    OPERATIONAL("maintaining value");

    private final String desc;

    PlanType(final String desc) {

        this.desc = desc;
    }

    @Override
    public String toString() {
        return String.format("%11s ( %s )", this.name(), desc);
    }
}
