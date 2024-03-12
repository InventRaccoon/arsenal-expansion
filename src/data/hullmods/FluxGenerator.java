// "Is... is this safe?"
// "Mostly."
package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;

public class FluxGenerator extends com.fs.starfarer.api.combat.BaseHullMod
{
  private String GEN_MOD = "GEN_MOD";
  private final float FLUX_PENALTY = 0.3F;
  
  public FluxGenerator() {}
  
  public void advanceInCombat(ShipAPI ship, float amount) {
    if ((com.fs.starfarer.api.Global.getCombatEngine().isPaused()) || (ship == null) || (ship.getOriginalOwner() == -1)) {
      return;
    }
    if (!ship.isAlive()) {
      removeStats(ship);
      return;
    }
    
    float modules = 0.0F;
    float alive = 0.0F;
    for (ShipAPI s : ship.getChildModulesCopy()) {
      modules += 1.0F;
      if (s.isAlive()) {
        alive += 1.0F;
      }
    }
    
    if (modules != 0.0F)
    {
      float fluxRatio = 1.0F - alive / modules;
      applyStats(fluxRatio, ship);
    }
  }
  
  private void removeStats(ShipAPI ship) {
    ship.getMutableStats().getFluxDissipation().unmodify(GEN_MOD);
    ship.getMutableStats().getFluxCapacity().unmodify(GEN_MOD);
  }
  
  private void applyStats(float fluxRatio, ShipAPI ship) {
    ship.getMutableStats().getFluxDissipation().modifyMult(GEN_MOD, 1.0F - fluxRatio * 0.3F);
    ship.getMutableStats().getFluxCapacity().modifyMult(GEN_MOD, 1.0F - fluxRatio * 0.3F);
  }
  public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
    if (index == 0) return "30";
    if (index == 1) return "1250";
    if (index == 2) return "125";
    if (index == 3) return "1.0";
    return null;
  }
}
