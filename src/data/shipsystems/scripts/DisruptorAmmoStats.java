package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public class DisruptorAmmoStats extends com.fs.starfarer.api.impl.combat.BaseShipSystemScript
{
  public static final float DISRUPT_WEPS = 150.0F;
  public static final float DISRUPT_ENGINES = 150.0F;
  
  public DisruptorAmmoStats() {}
  
  public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
    float dwep = 1.0F + 150.0F * effectLevel;
    float deng = 1.0F + 150.0F * effectLevel;
    
    stats.getDamageToTargetWeaponsMult().modifyMult(id, dwep);
    stats.getDamageToTargetEnginesMult().modifyMult(id, deng);
  }
  
  public void unapply(MutableShipStatsAPI stats, String id) {
    stats.getDamageToTargetWeaponsMult().unmodify(id);
    stats.getDamageToTargetEnginesMult().unmodify(id);
  }
  
  public StatusData getStatusData(int index, State state, float effectLevel)
  {
    if (index == 0) {
      return new StatusData("weapon disruption +150%", false);
    }
    if (index == 1) {
      return new StatusData("engine disruption +150%", false);
    }
    return null;
  }
}
