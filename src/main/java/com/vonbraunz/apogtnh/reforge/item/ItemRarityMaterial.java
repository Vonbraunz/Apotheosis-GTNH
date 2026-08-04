package com.vonbraunz.apogtnh.reforge.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.vonbraunz.apogtnh.affix.LootRarity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Meta-item for reforge/augment cost materials. Damage value 0-4 maps to LootRarity ordinal
 * (Common..Mythic).
 */
public class ItemRarityMaterial extends Item {

    // exposed for getTabIconItem in the creative tab
    public static final String[] ICON_NAMES = { "common", "uncommon", "rare", "epic", "mythic" };

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemRarityMaterial() {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public static LootRarity rarityFromStack(ItemStack stack) {
        if (stack == null) return null;
        int dmg = stack.getItemDamage();
        LootRarity[] values = LootRarity.values();
        if (dmg < 0 || dmg >= values.length) return null;
        return values[dmg];
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (LootRarity rarity : LootRarity.values()) {
            list.add(new ItemStack(item, 1, rarity.ordinal()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int dmg = stack.getItemDamage();
        if (dmg >= 0 && dmg < ICON_NAMES.length) {
            return "item.apogtnh.rarity_material." + ICON_NAMES[dmg];
        }
        return getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        this.icons = new IIcon[ICON_NAMES.length];
        for (int i = 0; i < ICON_NAMES.length; i++) {
            this.icons[i] = reg.registerIcon("apogtnh:rarity_material_" + ICON_NAMES[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int dmg) {
        if (dmg >= 0 && dmg < icons.length) return icons[dmg];
        return icons != null && icons.length > 0 ? icons[0] : null;
    }
}
