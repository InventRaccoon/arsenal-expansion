// this is pretty self-explanatory
package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public class NoVenting extends com.fs.starfarer.api.combat.BaseHullMod
{
  public NoVenting() {}
  
  public void applyEffectsBeforeShipCreation(com.fs.starfarer.api.combat.ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
    stats.getVentRateMult().modifyMult(id, 0.0F);
  }
}
