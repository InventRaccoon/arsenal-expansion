// "Why don't we make it shoot MORE bullets?"
package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public class HailfyreAutoloader extends com.fs.starfarer.api.combat.BaseHullMod
{
  public static final float ROF_BONUS = 40.0F;
  public static final float FLUX_REDUCTION = 30.0F;
  public static final float RECOIL_REDUCTION = 30.0F;
  public static final float DAMAGE_REDUCTION = 25.0F;
  
  public void applyEffectsBeforeShipCreation(com.fs.starfarer.api.combat.ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id)
  {
    stats.getBallisticRoFMult().modifyMult(id, 1.4F);
    stats.getBallisticWeaponFluxCostMod().modifyPercent(id, -30.0F);
    stats.getBallisticWeaponDamageMult().modifyPercent(id, -25.0F);
    stats.getRecoilPerShotMult().modifyPercent(id, -30.0F);
  }
  
  public String getDescriptionParam(int index, com.fs.starfarer.api.combat.ShipAPI.HullSize hullSize)
  {
    if (index == 0) return "40";
    if (index == 1) return "30";
    if (index == 2) return "25";
    return null;
  }
}
