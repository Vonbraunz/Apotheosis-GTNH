package com.vonbraunz.apogtnh.reforge.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.AffixRegistry;

/**
 * Creative-mode item that applies a specific affix at level 1 to an item in the anvil.
 * Metadata 0-34 maps to the 35 registered affixes sorted alphabetically by ID.
 */
public class ItemAffixBook extends Item {

    /** sorted list of affixes, populated at bootstrap time after all are registered. */
    private static List<Affix> ordered = new ArrayList<Affix>();

    public ItemAffixBook() {
        setMaxStackSize(1);
        setHasSubtypes(true);
        setMaxDamage(0);
        setTextureName("apogtnh:affix_book");
    }

    /** Call after AffixRegistry.bootstrap() to freeze the ordering. */
    public static void initOrder() {
        ordered.clear();
        ordered.addAll(AffixRegistry.all());
        Collections.sort(ordered, new Comparator<Affix>() {

            @Override
            public int compare(Affix a, Affix b) {
                return a.id.compareTo(b.id);
            }
        });
    }

    public static Affix getAffix(int meta) {
        if (meta < 0 || meta >= ordered.size()) return null;
        return ordered.get(meta);
    }

    public static int getMetaForAffix(Affix affix) {
        return ordered.indexOf(affix);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < ordered.size(); i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Affix a = getAffix(stack.getItemDamage());
        if (a != null) return "item.apogtnh.affix_book." + a.id.replace(':', '.');
        return getUnlocalizedName();
    }
}
