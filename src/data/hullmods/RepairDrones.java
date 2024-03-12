package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import java.util.HashMap;
import java.util.Map;

public class RepairDrones extends BaseHullMod
{
  public static final double MAX_REPAIR = 0.4D;
  private static Map mag = new HashMap();

  public RepairDrones() {}

  static { mag.put(HullSize.FIGHTER, Float.valueOf(10.0F));
    mag.put(HullSize.FRIGATE, Float.valueOf(1.0F));
    mag.put(HullSize.DESTROYER, Float.valueOf(0.75F));
    mag.put(HullSize.CRUISER, Float.valueOf(0.6F));
    mag.put(HullSize.CAPITAL_SHIP, Float.valueOf(0.4F)); }
  
  public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
    stats.getHullCombatRepairRatePercentPerSecond().modifyFlat(id, ((Float)mag.get(hullSize)).floatValue());
    stats.getMaxCombatHullRepairFraction().modifyFlat(id, 0.4F);
  }
  
  public String getDescriptionParam(int index, HullSize hullSize) {
    if (index == 0) return "10";
    if (index == 1) return "1";
    if (index == 2) return "0.75";
    if (index == 3) return "0.6";
    if (index == 4) return "0.4";
    return null;
  }
}
