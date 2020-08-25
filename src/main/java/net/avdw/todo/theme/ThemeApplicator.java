package net.avdw.todo.theme;

import com.google.inject.Inject;
import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.color.ColorInterpolator;
import net.avdw.todo.number.Interpolation;
import net.avdw.todo.render.TodoBarRenderer;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * @see net.avdw.todo.style.StyleApplicator
 */
@Deprecated
public class ThemeApplicator {

    private final int lineLength;
    private final int progressBarLength;
    private final ColorTheme colorTheme;
    private final ColorConverter colorConverter;
    private final ColorInterpolator colorInterpolator;
    private final TodoBarRenderer todoBarRenderer;

    @Inject
    ThemeApplicator(@LineLength final int lineLength, @ProgressBarLength final int progressBarLength, final ColorTheme colorTheme, final ColorConverter colorConverter, final ColorInterpolator colorInterpolator, final TodoBarRenderer todoBarRenderer) {
        this.lineLength = lineLength;
        this.progressBarLength = progressBarLength;
        this.colorTheme = colorTheme;
        this.colorConverter = colorConverter;
        this.colorInterpolator = colorInterpolator;
        this.todoBarRenderer = todoBarRenderer;
    }

    public String header(final String text) {
        return StringUtils.center(String.format("< %s >", text), lineLength, "-");
    }

    public Function<Object, Object> header() {
        return (obj) -> StringUtils.center(String.format("< %s >", obj), lineLength, "-");
    }

    public Function<Object, Object> count() {
        return (obj) -> StringUtils.leftPad(obj.toString(), 3);
    }

    public Function<Object, Object> progress() {
        return (obj) -> {
            double progress = new Double(obj.toString());
            int color = colorInterpolator.interpolate(colorTheme.progressStart(), colorTheme.progressEnd(), progress, Interpolation.LINEAR);
            String ansiColor = colorConverter.hexToAnsiFg(color, false);
            String percentage = String.format("%3.0f%%", progress * 100);
            return String.format("%s%s%s", ansiColor, percentage, colorTheme.txt());
        };
    }

    public Function<Object, Object> selected() {
        return (obj) -> String.format("%s%s%s", colorTheme.selected(), obj, colorTheme.txt());
    }

    public Function<Object, Object> subHeader() {
        return (obj) -> StringUtils.center(String.format("[ %s ]", obj), lineLength, "-");
    }

    public Function<Object, Object> rightDivider() {
        return (obj) -> StringUtils.leftPad(String.format("< %s >--", obj), lineLength, "-");
    }

    public Function<Object, Object> rightJustify() {
        return (obj) -> StringUtils.leftPad(obj.toString(), lineLength, " ");
    }

    public Function<Object, Object> center() {
        return (obj) -> StringUtils.center(obj.toString(), lineLength);
    }

    public Function<Object, Object> secondary() {
        return (obj) -> String.format("%s%s%s", colorTheme.secondary(), obj, colorTheme.txt());
    }

    public Function<Object, Object> heading() {
        return (obj) -> StringUtils.center(String.format("-- %s --", obj), lineLength);
    }

    public Function<Object, Object> divider() {
        return (obj) -> StringUtils.repeat("-", lineLength);
    }

    public Function<Object, Object> info() {
        return (obj) -> String.format("%s%s%s", colorTheme.info(), obj, colorTheme.txt());
    }

    public Function<Object, Object> good() {
        return (obj) -> String.format("%s%s%s", colorTheme.good(), obj, colorTheme.txt());
    }

    public Function<Object, Object> warn() {
        return (obj) -> String.format("%s%s%s", colorTheme.warn(), obj, colorTheme.txt());
    }

    public Function<Object, Object> action() {
        return (obj) -> String.format("%s! %s%s", colorTheme.good(), obj, colorTheme.txt());
    }

    public Function<Object, Object> bar() {
        return (obj) -> todoBarRenderer.createBar(Integer.parseInt(obj.toString()));
    }

    public Function<Object, Object> progressBar() {
        return (obj) -> {
            int progressBarCount = Integer.parseInt(obj.toString()) * progressBarLength / 100;
            return StringUtils.rightPad(todoBarRenderer.createBar(progressBarCount), progressBarLength, " ");
        };
    }

    public Function<Object, Object> complete() {
        return (obj) -> complete(obj.toString());
    }

    public Function<Object, Object> incomplete() {
        return (obj) -> incomplete(obj.toString());
    }

    public Function<Object, Object> completeBg() {
        return (obj) -> String.format("%s%s%s", colorTheme.completeBg(), obj, colorTheme.txt());
    }

    public Function<Object, Object> incompleteBg() {
        return (obj) -> String.format("%s%s%s", colorTheme.incompleteBg(), obj, colorTheme.txt());
    }

    public String hr() {
        return StringUtils.repeat("-", lineLength);
    }

    public String txt(final String text) {
        return String.format("%s%s", colorTheme.txt(), text);
    }

    public String blockComplete() {
        return String.format("%s %s", colorTheme.blockComplete(), colorTheme.txt());
    }

    public String blockIncomplete() {
        return String.format("%s %s", colorTheme.blockIncomplete(), colorTheme.txt());
    }

    public String context(final String context) {
        return String.format("%s%s%s", colorTheme.context(), context, colorTheme.txt());
    }

    public String project(final String project) {
        return String.format("%s%s%s", colorTheme.project(), project, colorTheme.txt());
    }

    public String progress(final String text, final Double progress) {
        int color = colorInterpolator.interpolate(colorTheme.progressStart(), colorTheme.progressEnd(), progress, Interpolation.LINEAR);
        String ansiColor = colorConverter.hexToAnsiFg(color, false);
        return String.format("%s%s%s", ansiColor, text, colorTheme.txt());
    }

    public String reset() {
        return colorTheme.txt();
    }

    public String complete(final String text) {
        return String.format("%s%s%s", colorTheme.complete(), text, colorTheme.txt());
    }

    public String incomplete(final String text) {
        return String.format("%s%s%s", colorTheme.incomplete(), text, colorTheme.txt());
    }

    public String start(final String token) {
        return String.format("%s%s%s", colorTheme.start(), token, colorTheme.txt());
    }

    public String priority(final String token) {
        return String.format("%s%s%s", colorTheme.priority(), token, colorTheme.txt());
    }

    public String addon(final String token) {
        return String.format("%s%s%s", colorTheme.addon(), token, colorTheme.txt());
    }

    public String postDue(final String token) {
        return String.format("%s%s%s", colorTheme.postDue(), token, colorTheme.txt());
    }

    public String preDue(final String token) {
        return String.format("%s%s%s", colorTheme.preDue(), token, colorTheme.txt());
    }

    public String error(final String token) {
        return String.format("%s%s%s", colorTheme.error(), token, colorTheme.txt());
    }

}
