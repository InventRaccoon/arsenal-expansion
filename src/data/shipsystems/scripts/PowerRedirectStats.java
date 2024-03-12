package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public class PowerRedirectStats extends com.fs.starfarer.api.impl.combat.BaseShipSystemScript {
  public static final float EN_DAMAGE_MULT = 0.75F;
  public static final float KE_DAMAGE_MULT = 0.65F;
  public static final float HE_DAMAGE_MULT = 0.85F;
  public static final float SHIELD_DAMAGE_MULT = 0.85F;
  public static final float SPEED_MULT = 0.7F;
  
  public PowerRedirectStats() {}
  
  public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
    effectLevel = 1.0F;
    stats.getShieldDamageTakenMult().modifyMult(id, SHIELD_DAMAGE_MULT * effectLevel);
    stats.getEnergyShieldDamageTakenMult().modifyMult(id, EN_DAMAGE_MULT * effectLevel);
    stats.getKineticShieldDamageTakenMult().modifyMult(id, KE_DAMAGE_MULT * effectLevel);
    stats.getHighExplosiveShieldDamageTakenMult().modifyMult(id, HE_DAMAGE_MULT * effectLevel);
    stats.getMaxSpeed().modifyMult(id, SPEED_MULT * effectLevel);

  }
  
  public void unapply(MutableShipStatsAPI stats, String id) {
    stats.getShieldDamageTakenMult().unmodify(id);
    stats.getEnergyShieldDamageTakenMult().unmodify(id);
    stats.getKineticShieldDamageTakenMult().unmodify(id);
    stats.getHighExplosiveShieldDamageTakenMult().unmodify(id);
    stats.getMaxSpeed().unmodify(id);
  }
  
  public StatusData getStatusData(int index, State state, float effectLevel) {
    if (index == 0) return new StatusData("shield strengthened, speed reduced", false);
    return null;
  }
}
