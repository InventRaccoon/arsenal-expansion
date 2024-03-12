package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.State;
import java.awt.Color;

public class EvasiveManeuverStats extends com.fs.starfarer.api.impl.combat.BaseShipSystemScript
{
  public static final Color JITTER_COLOR = new Color(255, 175, 255, 255);
  
  public static final float JITTER_FADE_TIME = 0.5F;
  
  public static final float SHIP_ALPHA_MULT = 0.25F;
  
  public static final float VULNERABLE_FRACTION = 0.0F;
  
  public static final float INCOMING_DAMAGE_MULT = 0.25F;
  public static final float MAX_TIME_MULT = 1.0F;
  protected Object STATUSKEY1 = new Object();
  protected Object STATUSKEY2 = new Object();
  protected Object STATUSKEY3 = new Object();
  protected Object STATUSKEY4 = new Object();
  
  public EvasiveManeuverStats() {}
  
  public static float getMaxTimeMult(MutableShipStatsAPI stats) { return 1.0F + 0.0F * stats.getDynamic().getValue("phase_time_mult"); }
  
  public void apply1(MutableShipStatsAPI stats, String id, State state, float effectLevel)
  {
    stats.getMaxSpeed().modifyFlat(id, 50.0F);
    stats.getAcceleration().modifyPercent(id, 200.0F * effectLevel);
    stats.getDeceleration().modifyPercent(id, 200.0F * effectLevel);
    stats.getTurnAcceleration().modifyFlat(id, 30.0F * effectLevel);
    stats.getTurnAcceleration().modifyPercent(id, 200.0F * effectLevel);
    stats.getMaxTurnRate().modifyFlat(id, 15.0F);
    stats.getMaxTurnRate().modifyPercent(id, 100.0F);
  }
  
  protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
    float level = effectLevel;
    float f = 0.0F;
    
    ShipSystemAPI cloak = playerShip.getPhaseCloak();
    if (cloak == null) cloak = playerShip.getSystem();
    if (cloak == null) { return;
    }
    if (level > f) {
      Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2, 
        cloak.getSpecAPI().getIconSpriteName(), cloak.getDisplayName(), "time flow altered", false);
    }
  }
  


  public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel)
  {
    ShipAPI ship = null;
    boolean player = false;
    if ((stats.getEntity() instanceof ShipAPI)) {
      ship = (ShipAPI)stats.getEntity();
      player = ship == Global.getCombatEngine().getPlayerShip();
      id = id + "_" + ship.getId();
    } else {
      return;
    }
    
    if (player) {
      maintainStatus(ship, state, effectLevel);
    }
    
    if (Global.getCombatEngine().isPaused()) {
      return;
    }
    
    if ((state == State.COOLDOWN) || (state == State.IDLE)) {
      unapply(stats, id);
      return;
    }
    
    float level = effectLevel;
    
    float levelForAlpha = level;
    
    ShipSystemAPI cloak = ship.getPhaseCloak();
    if (cloak == null) { cloak = ship.getSystem();
    }
    
    if ((state == State.IN) || (state == State.ACTIVE)) {
      ship.setPhased(true);
      levelForAlpha = level;
    } else if (state == State.OUT) {
      ship.setPhased(true);
      levelForAlpha = level;
      
      ship.setExtraAlphaMult(1.0F - 0.75F * levelForAlpha);
      ship.setApplyExtraAlphaToEngines(true);
      


      float shipTimeMult = 1.0F + (getMaxTimeMult(stats) - 1.0F) * levelForAlpha;
      stats.getTimeMult().modifyMult(id, shipTimeMult);
      if (player) {
        Global.getCombatEngine().getTimeMult().modifyMult(id, 1.0F / shipTimeMult);
      } else {
        Global.getCombatEngine().getTimeMult().unmodify(id);
      }
    }
  }
  



  public void unapply(MutableShipStatsAPI stats, String id)
  {
    ShipAPI ship = null;
    
    if ((stats.getEntity() instanceof ShipAPI)) {
      ship = (ShipAPI)stats.getEntity();
    }
    else {
      return;
    }
    
    Global.getCombatEngine().getTimeMult().unmodify(id);
    stats.getTimeMult().unmodify(id);
    
    ship.setPhased(false);
    ship.setExtraAlphaMult(1.0F);
    
    stats.getMaxSpeed().unmodify(id);
    stats.getMaxTurnRate().unmodify(id);
    stats.getTurnAcceleration().unmodify(id);
    stats.getAcceleration().unmodify(id);
    stats.getDeceleration().unmodify(id);
    stats.getMaxTurnRate().unmodify(id);
    stats.getMaxTurnRate().unmodify(id);
  }
  
  public StatusData getStatusData(int index, State state, float effectLevel)
  {
    return null;
  }
}
