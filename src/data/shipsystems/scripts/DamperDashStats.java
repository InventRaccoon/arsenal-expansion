package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public class DamperDashStats extends com.fs.starfarer.api.impl.combat.BaseShipSystemScript
{
  public static final float INCOMING_DAMAGE_MULT = 0.5F;
  
  public DamperDashStats() {}
  
  public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
    if (state == State.OUT) {
      stats.getMaxSpeed().unmodify(id);
      stats.getMaxTurnRate().unmodify(id);
    } else {
      stats.getMaxSpeed().modifyFlat(id, 50.0F);
      stats.getAcceleration().modifyPercent(id, 150.0F * effectLevel);
      stats.getDeceleration().modifyPercent(id, 150.0F * effectLevel);
      stats.getTurnAcceleration().modifyFlat(id, 20.0F * effectLevel);
      stats.getTurnAcceleration().modifyPercent(id, 150.0F * effectLevel);
      stats.getMaxTurnRate().modifyFlat(id, 15.0F);
      stats.getMaxTurnRate().modifyPercent(id, 100.0F);
      stats.getHullDamageTakenMult().modifyMult(id, 1.0F - 0.5F * effectLevel);
      stats.getArmorDamageTakenMult().modifyMult(id, 1.0F - 0.5F * effectLevel);
      stats.getEmpDamageTakenMult().modifyMult(id, 1.0F - 0.5F * effectLevel);
    }
  }
  
  public void unapply(MutableShipStatsAPI stats, String id) { stats.getMaxSpeed().unmodify(id);
    stats.getMaxTurnRate().unmodify(id);
    stats.getTurnAcceleration().unmodify(id);
    stats.getAcceleration().unmodify(id);
    stats.getDeceleration().unmodify(id);
    stats.getHullDamageTakenMult().unmodify(id);
    stats.getArmorDamageTakenMult().unmodify(id);
    stats.getEmpDamageTakenMult().unmodify(id);
  }
  
  public StatusData getStatusData(int index, State state, float effectLevel) {
    if (index == 0)
      return new StatusData("improved mobility", false);
    if (index == 1) {
      return new StatusData("incoming damage reduced", false);
    }
    return null;
  }
}
