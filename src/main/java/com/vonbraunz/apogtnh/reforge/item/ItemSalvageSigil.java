package com.vonbraunz.apogtnh.reforge.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Salvaging Hammer -- used in the crafting table alongside an affixed item to salvage it
 * into rarity material. Uses durability instead of being consumed: each salvage chips 1
 * durability via vanilla's container-item mechanic (same way buckets leave empty buckets
 * in the crafting grid). Stacks to 1, 256 uses, not repairable.
 */
public class ItemSalvageSigil extends Item {

    public ItemSalvageSigil() {
        setMaxStackSize(1);
        setMaxDamage(16);
        setNoRepair();
        setTextureName("apogtnh:salvage_sigil");
    }

    // ---- container-item mechanic (durability instead of consumption) -----------

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true; // always returns a damaged copy
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setItemDamage(copy.getItemDamage() + 1);
        if (copy.getItemDamage() >= copy.getMaxDamage()) {
            return null; // hammer breaks -- no container item, it's gone
        }
        return copy;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
        return false; // stays in the grid like a bucket
    }

    // ---- durability display ----------------------------------------------------

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return stack.isItemDamaged();
    }
}
