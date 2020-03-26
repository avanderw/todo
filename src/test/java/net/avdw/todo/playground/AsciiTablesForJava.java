package net.avdw.todo.playground;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;

import java.util.Arrays;
import java.util.List;

/**
 * https://github.com/freva/ascii-table
 */
public class AsciiTablesForJava {
    public static void main(String[] args) {
        List<Planet> planets = Arrays.asList(
                new Planet(1, "Mercury", 0.382, 0.06, "minimal"),
                new Planet(2, "Venus", 0.949, 0.82, "Carbon dioxide, Nitrogen"),
                new Planet(3, "Earth", 1.0, 1.0, "Nitrogen, Oxygen, Argon"),
                new Planet(4, "Mars", 0.532, 0.11, "Carbon dioxide, Nitrogen, Argon"));

        System.out.println(AsciiTable.getTable(planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").with(planet -> planet.name),
                new Column().header("Diameter").with(planet -> String.format("%.03f", planet.diameter)),
                new Column().header("Mass").with(planet -> String.format("%.02f", planet.mass)),
                new Column().header("Atmosphere").with(planet -> planet.atmosphere))));
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(AsciiTable.getTable(planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").headerAlign(HorizontalAlign.CENTER).dataAlign(HorizontalAlign.LEFT).with(planet -> planet.name),
                new Column().header("Diameter").with(planet -> String.format("%.03f", planet.diameter)),
                new Column().header("Mass").with(planet -> String.format("%.02f", planet.mass)),
                new Column().header("Atmosphere").headerAlign(HorizontalAlign.RIGHT).dataAlign(HorizontalAlign.CENTER).with(planet -> planet.atmosphere))));
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(AsciiTable.getTable(planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").with(planet -> planet.name),
                new Column().header("Diameter").with(planet -> String.format("%.03f", planet.diameter)),
                new Column().header("Mass").with(planet -> String.format("%.02f", planet.mass)),
                new Column().header("Atmosphere Composition").maxColumnWidth(12).with(planet -> planet.atmosphere))));
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(AsciiTable.getTable(planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").footer("Average").headerAlign(HorizontalAlign.CENTER).dataAlign(HorizontalAlign.RIGHT).with(planet -> planet.name),
                new Column().header("Diameter").headerAlign(HorizontalAlign.RIGHT).dataAlign(HorizontalAlign.CENTER).footerAlign(HorizontalAlign.CENTER)
                        .footer(String.format("%.03f", planets.stream().mapToDouble(planet -> planet.diameter).average().orElse(0)))
                        .with(planet -> String.format("%.03f", planet.diameter)),
                new Column().header("Mass").headerAlign(HorizontalAlign.RIGHT).dataAlign(HorizontalAlign.LEFT)
                        .footer(String.format("%.02f", planets.stream().mapToDouble(planet -> planet.mass).average().orElse(0)))
                        .with(planet -> String.format("%.02f", planet.mass)),
                new Column().header("Atmosphere").headerAlign(HorizontalAlign.LEFT).dataAlign(HorizontalAlign.CENTER).with(planet -> planet.atmosphere))));
        System.out.println();
        System.out.println();
        System.out.println();
        Character[] borderStyles = AsciiTable.BASIC_ASCII_NO_DATA_SEPARATORS_NO_OUTSIDE_BORDER;
        System.out.println(AsciiTable.getTable(borderStyles, planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").footer("Average").headerAlign(HorizontalAlign.CENTER).dataAlign(HorizontalAlign.RIGHT).with(planet -> planet.name),
                new Column().header("Diameter").headerAlign(HorizontalAlign.RIGHT).dataAlign(HorizontalAlign.CENTER).footerAlign(HorizontalAlign.CENTER)
                        .footer(String.format("%.03f", planets.stream().mapToDouble(planet -> planet.diameter).average().orElse(0)))
                        .with(planet -> String.format("%.03f", planet.diameter)),
                new Column().header("Mass").headerAlign(HorizontalAlign.RIGHT).dataAlign(HorizontalAlign.LEFT)
                        .footer(String.format("%.02f", planets.stream().mapToDouble(planet -> planet.mass).average().orElse(0)))
                        .with(planet -> String.format("%.02f", planet.mass)),
                new Column().header("Atmosphere").headerAlign(HorizontalAlign.LEFT).dataAlign(HorizontalAlign.CENTER).with(planet -> planet.atmosphere))));
    }

    private static class Planet {
        public final int num;
        public final String name;
        public final double diameter;
        public final double mass;
        public final String atmosphere;

        public Planet(int num, String name, double diameter, double mass, String atmosphere) {
            this.num = num;
            this.name = name;
            this.diameter = diameter;
            this.mass = mass;
            this.atmosphere = atmosphere;
        }
    }
}
