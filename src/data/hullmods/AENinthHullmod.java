// "Rolling Thunder"
package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class AENinthHullmod extends BaseHullMod {

	public AENinthHullmod() {}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getZeroFluxSpeedBoost().modifyFlat(id, 5);
		stats.getShieldUnfoldRateMult().modifyPercent(id, 35);
		stats.getShieldTurnRateMult().modifyPercent(id, 35);
		stats.getAcceleration().modifyPercent(id, 15);
		stats.getDeceleration().modifyPercent(id, 15);
		stats.getTurnAcceleration().modifyPercent(id, 15);
		stats.getCRLossPerSecondPercent().modifyPercent(id, 25);
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "5";
		if (index == 1) return "35";
		if (index == 2) return "15";
		if (index == 3) return "25";
		return null;
	}
}