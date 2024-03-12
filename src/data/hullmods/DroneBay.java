package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class DroneBay extends com.fs.starfarer.api.combat.BaseHullMod
{
  public DroneBay() {}
  
  public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id)
  {
    stats.getNumFighterBays().modifyFlat(id, 1.0F);
  }

}
