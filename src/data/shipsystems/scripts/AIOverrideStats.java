package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;

public class AIOverrideStats extends com.fs.starfarer.api.impl.combat.BaseShipSystemScript
{
  public final Object KEY_JITTER = new Object();
  
  public final float DAMAGE_INCREASE_PERCENT = 35.0F;
  
  public final float SPEED_INCREASE_PERCENT = 35.0F;
  public final Color JITTER_UNDER_COLOR = new Color(255, 50, 0, 125);
  public final Color JITTER_COLOR = new Color(255, 50, 0, 75);
  
  public AIOverrideStats() {}
  
  public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) { ShipAPI ship = null;
    if ((stats.getEntity() instanceof ShipAPI)) {
      ship = (ShipAPI)stats.getEntity();
    } else {
      return;
    }
    

    if (effectLevel > 0.0F) {
      float jitterLevel = effectLevel;
      float maxRangeBonus = 5.0F;
      float jitterRangeBonus = jitterLevel * maxRangeBonus;
      for (ShipAPI fighter : getFighters(ship))
        if (!fighter.isHulk()) {
          MutableShipStatsAPI fStats = fighter.getMutableStats();
          



          fStats.getDamageToFighters().modifyMult(id, 1.0F + 0.35F * effectLevel);
          fStats.getMaxSpeed().modifyMult(id, 1.0F + 0.35F * effectLevel);
          fStats.getAcceleration().modifyMult(id, 1.0F + 0.35F * effectLevel);
          fStats.getDeceleration().modifyMult(id, 1.0F + 0.35F * effectLevel);
          fStats.getTurnAcceleration().modifyMult(id, 1.0F + 0.35F * effectLevel);
          
          if (jitterLevel > 0.0F)
          {
            fighter.setWeaponGlow(effectLevel, Misc.setAlpha(JITTER_UNDER_COLOR, 255), EnumSet.allOf(com.fs.starfarer.api.combat.WeaponAPI.WeaponType.class));
            
            fighter.setJitterUnder(KEY_JITTER, JITTER_COLOR, jitterLevel, 3, 0.0F, jitterRangeBonus);
            fighter.setJitter(KEY_JITTER, JITTER_UNDER_COLOR, jitterLevel, 2, 0.0F, 0.0F + jitterRangeBonus * 1.0F);
            Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1.0F, 1.0F, fighter.getLocation(), fighter.getVelocity());
          }
        }
    }
  }
  
  private List<ShipAPI> getFighters(ShipAPI carrier) {
    List<ShipAPI> result = new java.util.ArrayList();

    for (ShipAPI ship : Global.getCombatEngine().getShips()) {
      if ((ship.isFighter()) && 
        (ship.getWing() != null) && 
        (ship.getWing().getSourceShip() == carrier)) {
        result.add(ship);
      }
    }
    
    return result;
  }
  
  public void unapply(MutableShipStatsAPI stats, String id)
  {
    ShipAPI ship = null;
    if ((stats.getEntity() instanceof ShipAPI)) {
      ship = (ShipAPI)stats.getEntity();
    } else {
      return;
    }
    for (ShipAPI fighter : getFighters(ship)) {
      if (!fighter.isHulk()) {
        MutableShipStatsAPI fStats = fighter.getMutableStats();
        fStats.getDamageToFighters().unmodify(id);
        fStats.getMaxSpeed().unmodify(id);
        fStats.getAcceleration().unmodify(id);
        fStats.getDeceleration().unmodify(id);
        fStats.getTurnAcceleration().unmodify(id);
      }
    }
  }
  
  public StatusData getStatusData(int index, State state, float effectLevel)
  {
    if (index == 0)
    {
      return new StatusData(Misc.getRoundedValueMaxOneAfterDecimal(1.0F + 35.0F * effectLevel * 0.01F) + "x fighter damage", false);
    }
    return null;
  }
}
