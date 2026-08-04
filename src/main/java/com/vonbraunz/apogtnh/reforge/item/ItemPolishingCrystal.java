package com.vonbraunz.apogtnh.reforge.item;

import net.minecraft.item.Item;

/**
 * Polishing Crystal -- anvil right-slot item that increases the level of one affix on an
 * affixed item. Stack count targets the affix: 1 crystal = first affix, 2 = second, etc.
 * Rejects if the count exceeds the number of affixes or the targeted affix is already at
 * its maximum level.
 */
public class ItemPolishingCrystal extends Item {

    public ItemPolishingCrystal() {
        setMaxStackSize(4); // up to 3 affixes max, 4th stack size = reject
        setTextureName("apogtnh:polishing_crystal");
    }
}
