// "Scratch one. Scratch two. Scratch all."
package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public class AEAntiFighterTargeting extends com.fs.starfarer.api.combat.BaseHullMod {

    public static final float DAMAGE_BOOST = 50f;

    public AEAntiFighterTargeting() {}

    public void applyEffectsBeforeShipCreation(com.fs.starfarer.api.combat.ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDamageToFighters().modifyPercent(id, DAMAGE_BOOST);
    }
}