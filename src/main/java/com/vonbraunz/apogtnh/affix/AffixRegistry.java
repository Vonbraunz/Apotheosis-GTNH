package com.vonbraunz.apogtnh.affix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.vonbraunz.apogtnh.affix.impl.AffixAquatic;
import com.vonbraunz.apogtnh.affix.impl.AffixArmor;
import com.vonbraunz.apogtnh.affix.impl.AffixAutoSmelt;
import com.vonbraunz.apogtnh.affix.impl.AffixBeheading;
import com.vonbraunz.apogtnh.affix.impl.AffixCleaving;
import com.vonbraunz.apogtnh.affix.impl.AffixCropGrowth;
import com.vonbraunz.apogtnh.affix.impl.AffixDamage;
import com.vonbraunz.apogtnh.affix.impl.AffixExecute;
import com.vonbraunz.apogtnh.affix.impl.AffixFeather;
import com.vonbraunz.apogtnh.affix.impl.AffixFire;
import com.vonbraunz.apogtnh.affix.impl.AffixFireResist;
import com.vonbraunz.apogtnh.affix.impl.AffixFortify;
import com.vonbraunz.apogtnh.affix.impl.AffixFortuneBoost;
import com.vonbraunz.apogtnh.affix.impl.AffixGravity;
import com.vonbraunz.apogtnh.affix.impl.AffixGuardian;
import com.vonbraunz.apogtnh.affix.impl.AffixHaste;
import com.vonbraunz.apogtnh.affix.impl.AffixHeart;
import com.vonbraunz.apogtnh.affix.impl.AffixKnockback;
import com.vonbraunz.apogtnh.affix.impl.AffixLacerating;
import com.vonbraunz.apogtnh.affix.impl.AffixLifesteal;
import com.vonbraunz.apogtnh.affix.impl.AffixMending;
import com.vonbraunz.apogtnh.affix.impl.AffixMovement;
import com.vonbraunz.apogtnh.affix.impl.AffixReach;
import com.vonbraunz.apogtnh.affix.impl.AffixStepAssist;
import com.vonbraunz.apogtnh.affix.impl.AffixTelekinesis;
import com.vonbraunz.apogtnh.affix.impl.AffixThorns;
import com.vonbraunz.apogtnh.affix.impl.AffixUnbreakingBoost;

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
        // Weapon affixes
        register(new AffixDamage()); // +flat hearts
        register(new AffixLifesteal()); // % heal on hit (sword/ranged)
        register(new AffixKnockback()); // extra knockback
        register(new AffixLacerating()); // % chance double damage
        register(new AffixFire()); // set target on fire
        register(new AffixMending()); // self-repair
        register(new AffixBeheading()); // bonus head/skull drop chance
        register(new AffixExecute()); // instakill below HP threshold
        register(new AffixCleaving()); // hit multiple enemies per swing

        // Tool affixes
        register(new AffixHaste()); // haste potion effect
        register(new AffixAutoSmelt()); // scaffold: mined blocks drop smelted result
        register(new AffixTelekinesis()); // scaffold: drops go straight to inventory
        register(new AffixFortuneBoost()); // scaffold: bonus fortune levels
        register(new AffixUnbreakingBoost()); // scaffold: chance to refund durability
        register(new AffixCropGrowth()); // scaffold: hoe-only instant crop growth
        register(new AffixReach()); // bonus block interaction range

        // Armor affixes
        register(new AffixArmor()); // flat damage reduction
        register(new AffixThorns()); // % reflect on melee hit
        register(new AffixGuardian()); // flat reduction on all armor slots
        register(new AffixHeart()); // +max HP on chest/legs
        register(new AffixFortify()); // resistance potion effect
        register(new AffixAquatic()); // water breathing on helmet

        // Boot affixes
        register(new AffixMovement()); // +speed on boots
        register(new AffixFeather()); // -fall damage on boots
        register(new AffixGravity()); // jump boost on boots
        register(new AffixStepAssist()); // auto-step up blocks
        register(new AffixFireResist()); // fire immunity on boots
    }
}
