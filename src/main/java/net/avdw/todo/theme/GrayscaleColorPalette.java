package net.avdw.todo.theme;

public class GrayscaleColorPalette implements ColorPalette {
    @Override
    public int primaryTint() {
        return 0xCCCCCC;
    }

    @Override
    public int primaryTone() {
        return 0x999999;
    }

    @Override
    public int primaryShade() {
        return 0x666666;
    }

    @Override
    public int secondaryTone() {
        return 0x333333;
    }

    @Override
    public int accentTone() {
        return 0xFFFFFF;
    }
}
