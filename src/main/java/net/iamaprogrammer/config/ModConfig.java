package net.iamaprogrammer.config;

import net.iamaprogrammer.WaxItemFrames;
import net.iamaprogrammer.config.core.Config;

public class ModConfig implements Config {
    private boolean fix_item_frame_when_waxed;

    public ModConfig() {}

    public ModConfig(boolean fix_item_frame_when_waxed) {
        this.fix_item_frame_when_waxed = fix_item_frame_when_waxed;
    }

    public ModConfig(ModConfig copy) {
        this(copy.fix_item_frame_when_waxed);
    }

    public boolean isItemFrameFixedWhenWaxed() {
        return fix_item_frame_when_waxed;
    }

    public void shouldFixItemFrameWhenWaxed(boolean fix_item_frame_when_waxed) {
        this.fix_item_frame_when_waxed = fix_item_frame_when_waxed;
    }

    @Override
    public String fileName() {
        return WaxItemFrames.MOD_ID;
    }
}