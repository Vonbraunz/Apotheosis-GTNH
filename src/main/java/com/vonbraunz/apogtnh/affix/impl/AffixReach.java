package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/**
 * Increases block interaction range by 1 block (survival 4.5 -> 5.5), via
 * MixinPlayerControllerMP. Single level only, deliberately -- vanilla's server-side dig
 * validation (NetHandlerPlayServer) enforces its own fixed 6.0-block cap independent of
 * PlayerControllerMP.getBlockReachDistance(), so anything past +1.5 gets silently dropped
 * server-side (in singleplayer too, since the integrated server runs the same check).
 * +1 keeps the effective 5.5 blocks safely under that cap.
 */
public class AffixReach extends Affix {

    public AffixReach() {
        super("apogtnh:reach", 1, 1, LootCategory.TOOL);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Reach";
    }

    @Override
    public String description(int level) {
        return "Extends block interaction range by 1 block.";
    }

    @Override
    public String displayName(int level) {
        return "Extending";
    }
}
