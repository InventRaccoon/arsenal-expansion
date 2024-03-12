// "Turning it to eleven."
// "... actually, it's probably more like four, these things still move like they're flying through molasses..."
package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class AEUIFighter extends BaseHullMod {

	private static final float RANGE_MULT = 0.85f;
	private static final float SPEED_BOOST = 75f;
	
	private static final float ACCELERATION_BONUS = 75f;

	public AEUIFighter() {}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().modifyFlat(id, SPEED_BOOST);
		stats.getBallisticWeaponRangeBonus().modifyMult(id, RANGE_MULT);
		stats.getEnergyWeaponRangeBonus().modifyMult(id, RANGE_MULT);
		
		stats.getAcceleration().modifyPercent(id, ACCELERATION_BONUS);
		stats.getDeceleration().modifyPercent(id, ACCELERATION_BONUS);
		stats.getMaxTurnRate().modifyPercent(id, ACCELERATION_BONUS);
		stats.getTurnAcceleration().modifyPercent(id, ACCELERATION_BONUS);
	}
}