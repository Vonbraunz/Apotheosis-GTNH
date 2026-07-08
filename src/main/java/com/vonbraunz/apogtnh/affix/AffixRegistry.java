package com.vonbraunz.apogtnh.affix;

import com.vonbraunz.apogtnh.affix.impl.AffixArmor;
import com.vonbraunz.apogtnh.affix.impl.AffixDamage;
import com.vonbraunz.apogtnh.affix.impl.AffixLifesteal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * HashMap-backed registry for affixes. No IForgeRegistry (doesn't exist in 1.7.10).
 * Registration order is not significant; lookups use the id string.
 */
public class AffixRegistry {

    private static final Map<String, Affix> BY_ID = new HashMap<String, Affix>();

    public static void register(Affix affix) {
        if (affix == null || affix.id == null) return;
        BY_ID.put(affix.id, affix);
    }

    public static Affix get(String id) {
        return BY_ID.get(id);
    }

    public static java.util.Collection<Affix> all() {
        return BY_ID.values();
    }

    /** Filter registered affixes to those legal for the given category. */
    public static List<Affix> forCategory(LootCategory cat) {
        List<Affix> out = new ArrayList<Affix>();
        for (Affix a : BY_ID.values()) {
            if (a.canApplyTo(cat)) out.add(a);
        }
        return out;
    }

    /** Pick N distinct affixes for a category. Returns fewer than N if the pool is small. */
    public static List<Affix> pickDistinct(Random rand, LootCategory cat, int count) {
        List<Affix> pool = new ArrayList<Affix>(forCategory(cat));
        List<Affix> picked = new ArrayList<Affix>();
        while (!pool.isEmpty() && picked.size() < count) {
            picked.add(pool.remove(rand.nextInt(pool.size())));
        }
        return picked;
    }

    /**
     * Register the starter affix set. Add your own here as you build them out.
     * Kept small in the scaffold on purpose — extend once the wiring is proven.
     */
    public static void bootstrap() {
        register(new AffixDamage());
        register(new AffixLifesteal());
        register(new AffixArmor());
        // TODO(scaffold): add more affixes:
        //   AffixMovementSpeed, AffixKnockback, AffixThorns, AffixFireAspect,
        //   AffixMaxHealth, AffixAttackSpeed, AffixLooting, AffixMending, etc.
    }
}
