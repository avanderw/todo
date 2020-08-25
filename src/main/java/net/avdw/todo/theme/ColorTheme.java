package net.avdw.todo.theme;

@Deprecated
public interface ColorTheme {
    String selected();

    String txt();

    String blockComplete();

    String blockIncomplete();

    String context();

    String project();

    int progressStart();

    int progressEnd();

    String secondary();

    String complete();

    String completeBg();

    String incomplete();

    String incompleteBg();

    String start();

    String priority();

    String addon();

    String postDue();

    String preDue();

    String error();

    String info();

    String good();

    String warn();

}
