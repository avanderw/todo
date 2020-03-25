package net.avdw.todo.theme;

/**
 * Structure to provide colors for the themes.
 * <p>
 * https://www.tigercolor.com/color-lab/color-theory/color-harmonies.htm
 * <p>
 * Red: energy, power, passion
 * Orange: joy, enthusiasm, creativity
 * Yellow: happiness, intellect, energy
 * Green: ambition, growth, freshness, safety
 * Blue: tranquility, confidence, intelligence
 * Purple: luxury, ambition, creativity
 * Black: power, elegance, mystery
 * White: cleanliness, purity, perfection
 * <p>
 * Start with greyscale
 * Use PRIMARY-SECONDARY-ACCENT, 60-30-10 hue rule
 */
public interface ColorPalette<T> {
    T primaryTint();

    T primaryTone();

    T primaryShade();

    T secondaryTint();

    T secondaryTone();

    T secondaryShade();

    T accentTint();

    T accentTone();

    T accentShade();
}
