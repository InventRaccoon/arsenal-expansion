package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class EnhancedSensors extends BaseHullMod {

	public static float BONUS = 40;

	public EnhancedSensors() {}
	
//	private static Map mag = new HashMap();
//	static {
//		mag.put(HullSize.FRIGATE, 300f);
//		mag.put(HullSize.DESTROYER, 200f);
//		mag.put(HullSize.CRUISER, 100f);
//		mag.put(HullSize.CAPITAL_SHIP, 100f);
//	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//stats.getSensorStrength().modifyPercent(id, ((Float) mag.get(hullSize)).intValue());
		stats.getSensorStrength().modifyFlat(id, BONUS);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) BONUS;
//		if (index == 0) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue();
//		if (index == 1) return "" + ((Float) mag.get(HullSize.DESTROYER)).intValue();
//		if (index == 2) return "" + ((Float) mag.get(HullSize.CRUISER)).intValue();
		//if (index == 3) return "" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue();
		return null;
	}


}
