// "BOOM!"
package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public class IncreasedExplosion extends com.fs.starfarer.api.combat.BaseHullMod
{
  public static final float RADIUS_MULT = 1.5F;
  public static final float DAMAGE_MULT = 1.5F;
  
  public IncreasedExplosion() {}
  
  public void applyEffectsBeforeShipCreation(com.fs.starfarer.api.combat.ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id)
  {
    stats.getDynamic().getStat("explosion_damage_mult").modifyMult(id, 1.5F);
    stats.getDynamic().getStat("explosion_radius_mult").modifyMult(id, 1.5F);
  }
  
  public String getDescriptionParam(int index, com.fs.starfarer.api.combat.ShipAPI.HullSize hullSize) {
    return null;
  }
}
