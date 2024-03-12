// "Bring it! I'm slightly more resilient than others!"
package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import java.util.HashMap;
import java.util.Map;

public class MicroFluxShunt extends BaseHullMod
{
  private static Map mag = new HashMap();
  
  static { mag.put(HullSize.FRIGATE, Float.valueOf(15.0F));
    mag.put(HullSize.DESTROYER, Float.valueOf(12.0F));
    mag.put(HullSize.CRUISER, Float.valueOf(10.0F));
    mag.put(HullSize.CAPITAL_SHIP, Float.valueOf(8.0F)); }
  
  public MicroFluxShunt() {}
  
  public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) { stats.getHardFluxDissipationFraction().modifyFlat(id, ((Float)mag.get(hullSize)).floatValue() * 0.01F); }
  
  public String getDescriptionParam(int index, HullSize hullSize)
  {
    if (index == 0) return "15";
    if (index == 1) return "12";
    if (index == 2) return "10";
    if (index == 3) return "8";
    return null;
  }
  
  public boolean isApplicableToShip(ShipAPI ship)
  {
    return !ship.getVariant().getHullMods().contains("fluxshunt");
  }
  
  public String getUnapplicableReason(ShipAPI ship) {
    if (ship.getVariant().getHullMods().contains("fluxshunt")) {
      return "This ship already has a flux shunt!";
    }
    return null;
  }
}
