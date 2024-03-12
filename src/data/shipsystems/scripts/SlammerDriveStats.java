package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public class SlammerDriveStats extends com.fs.starfarer.api.impl.combat.BaseShipSystemScript
{
  public static final float INCOMING_DAMAGE_MULT = 0.25F;
  
  public SlammerDriveStats() {}
  
  public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
    if (state == State.OUT) {
      stats.getMaxSpeed().unmodify(id);
    } else {
      effectLevel = 1.0F;
      stats.getHullDamageTakenMult().modifyMult(id, 1.0F - 0.75F * effectLevel);
      stats.getArmorDamageTakenMult().modifyMult(id, 1.0F - 0.75F * effectLevel);
      stats.getEmpDamageTakenMult().modifyMult(id, 1.0F - 0.75F * effectLevel);
      stats.getMaxSpeed().modifyFlat(id, 100.0F * effectLevel);
      stats.getAcceleration().modifyFlat(id, 100.0F * effectLevel);
    }
  }
  
  public void unapply(MutableShipStatsAPI stats, String id) {
    stats.getMaxSpeed().unmodify(id);
    stats.getMaxTurnRate().unmodify(id);
    stats.getTurnAcceleration().unmodify(id);
    stats.getAcceleration().unmodify(id);
    stats.getDeceleration().unmodify(id);
    stats.getHullDamageTakenMult().unmodify(id);
    stats.getArmorDamageTakenMult().unmodify(id);
    stats.getEmpDamageTakenMult().unmodify(id);
  }
  
  public StatusData getStatusData(int index, State state, float effectLevel) {
    if (index == 0) {
      return new StatusData("+speed, -damage taken", false);
    }
    return null;
  }
}
