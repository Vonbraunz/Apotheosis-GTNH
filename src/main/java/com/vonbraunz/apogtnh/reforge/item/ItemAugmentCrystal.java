package com.vonbraunz.apogtnh.reforge.item;

import net.minecraft.item.Item;

/**
 * Anvil right-slot item that selects augmenting (single random affix reroll) instead of
 * reforging. Single variant, no rarity subtypes -- unlike ItemRarityMaterial, which item
 * you insert *is* the choice of mechanic. Scaffold only, no texture yet (renders as
 * checkerboard until textures/items/augment_crystal.png exists).
 */
public class ItemAugmentCrystal extends Item {

    public ItemAugmentCrystal() {
        setMaxStackSize(64);
        setTextureName("apogtnh:augment_crystal");
    }
}
