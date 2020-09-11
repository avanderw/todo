package net.avdw.todo.refactor;

import picocli.CommandLine;

@CommandLine.Command(name = "chart", description = "Chart a pivot of data")
public class TodoChart implements Runnable {
    @CommandLine.Parameters(description = "Second key to extract", arity = "0..1", index = "1")
    private String secondKey;
    @CommandLine.Parameters(description = "Third key to extract", arity = "0..1", index = "2")
    private String thirdKey;


    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        if (secondKey == null) {
            showBarChart();
        } else if (thirdKey == null) {
            showScatterChart();
        } else {
            showBubbleChart();
        }
    }

    private void showBarChart() {
        throw new UnsupportedOperationException();
    }

    private void showBubbleChart() {
        throw new UnsupportedOperationException();
    }

    private void showScatterChart() {
        throw new UnsupportedOperationException();
    }
}
