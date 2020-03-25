package net.avdw.todo.theme;

public class GrayscaleColorPalette implements ColorPalette<Integer> {
    @Override
    public Integer primaryTint() {
        return 0xBBBBBB;
    }

    @Override
    public Integer primaryTone() {
        return 0x999999;
    }

    @Override
    public Integer primaryShade() {
        return 0x777777;
    }

    @Override
    public Integer secondaryTint() {
        return 0x555555;
    }

    @Override
    public Integer secondaryTone() {
        return 0x333333;
    }

    @Override
    public Integer secondaryShade() {
        return 0x111111;
    }

    @Override
    public Integer accentTint() {
        return 0xFFFFFF;
    }

    @Override
    public Integer accentTone() {
        return 0xEEEEEE;
    }

    @Override
    public Integer accentShade() {
        return 0xDDDDDD;
    }
}
