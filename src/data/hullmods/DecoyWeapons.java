// "Fooled you."
package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public class DecoyWeapons extends com.fs.starfarer.api.combat.BaseHullMod {

    public static final float DAMAGE_REDUCTION = 100f;

    public DecoyWeapons() {}

    public void applyEffectsBeforeShipCreation(com.fs.starfarer.api.combat.ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBeamWeaponDamageMult().modifyPercent(id, 1f - (DAMAGE_REDUCTION));
    }
}