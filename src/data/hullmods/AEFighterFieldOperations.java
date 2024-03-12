package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import java.util.HashMap;
import java.util.Map;

public class AEFighterFieldOperations  extends BaseHullMod {

    public static float SPEED_REDUCTION = 0.30f;
    public static float DAMAGE_INCREASE = 0.5f;
    public static final int ALL_FIGHTER_COST_INCREASE = 5;
    public static final int BOMBER_COST_INCREASE = 10;
    public static final int SUPPORT_COST_INCREASE = 4;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getFighterWingRange().modifyPercent(id, 0.5f);
        stats.getDynamic().getMod(Stats.BOMBER_COST_MOD).modifyFlat(id, BOMBER_COST_INCREASE);
        stats.getDynamic().getMod(Stats.FIGHTER_COST_MOD).modifyFlat(id, ALL_FIGHTER_COST_INCREASE);
        stats.getDynamic().getMod(Stats.INTERCEPTOR_COST_MOD).modifyFlat(id, ALL_FIGHTER_COST_INCREASE);
        stats.getDynamic().getMod(Stats.SUPPORT_COST_MOD).modifyFlat(id, SUPPORT_COST_INCREASE);
    }

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        MutableShipStatsAPI stats = fighter.getMutableStats();

        stats.getMaxSpeed().modifyMult(id, 1f - SPEED_REDUCTION);

        stats.getArmorDamageTakenMult().modifyPercent(id, DAMAGE_INCREASE * 100f);
        stats.getShieldDamageTakenMult().modifyPercent(id, DAMAGE_INCREASE * 100f);
        stats.getHullDamageTakenMult().modifyPercent(id, DAMAGE_INCREASE * 100f);

        fighter.setLightDHullOverlay();
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "25%/50%/50%/75%";
        if (index == 1) return "support fighters";
        if (index == 2) return "fighters";
        if (index == 3) return "interceptors";
        if (index == 4) return "bombers";
        if (index == 5) return "2";
        if (index == 6) return "30" + "%";
        if (index == 7) return "50" + "%";
        return null;
    }

    @Override
    public boolean affectsOPCosts() {
        return true;
    }
}
