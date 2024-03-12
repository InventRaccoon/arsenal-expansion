package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public class HunterTranceStats extends com.fs.starfarer.api.impl.combat.BaseShipSystemScript
{
  public static final float ROF_BONUS = 1.3F;
  public static final float RANGE_BONUS = 1.8F;
  public static final float FLUX_REDUCTION = 30.0F;
  
  public HunterTranceStats() {}
  
  public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
    float mult = 0.0F + 1.3F * effectLevel;
    stats.getBallisticRoFMult().modifyMult(id, mult);
    stats.getBallisticWeaponRangeBonus().modifyMult(id, 1.8F);
    stats.getMaxSpeed().modifyPercent(id, 50.0F);
    stats.getBallisticWeaponFluxCostMod().modifyPercent(id, -30.0F);
  }
  
  public void unapply(MutableShipStatsAPI stats, String id) { stats.getBallisticRoFMult().unmodify(id);
    stats.getBallisticWeaponRangeBonus().unmodify(id);
    stats.getMaxSpeed().unmodify(id);
    stats.getBallisticWeaponFluxCostMod().unmodify(id);
  }
  
  public StatusData getStatusData(int index, State state, float effectLevel) {
    if (index == 0) {
      return new StatusData("+range, +fire rate, -speed", false);
    }
    return null;
  }
}
