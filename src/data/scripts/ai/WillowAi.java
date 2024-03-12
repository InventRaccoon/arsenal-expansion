// PD missile AI by Tartiflette
package data.scripts.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.GuidedMissileAI;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.WeaponAPI;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;


public class WillowAi
  implements MissileAIPlugin, GuidedMissileAI
{
  private CombatEngineAPI engine;
  private final MissileAPI missile;
  private CombatEntityAPI target;
  private Vector2f lead = new Vector2f();
  private float timer = 0.0F; private float delay = 0.05F;
  
  private final float MAX_SPEED;
  private final int SEARCH_RANGE = 500;
  private final float DAMPING = 0.2F;
  private final Color EXPLOSION_COLOR = new Color(100, 100, 100, 255);
  private final Color PARTICLE_COLOR = new Color(240, 200, 50, 255);
  private final int NUM_PARTICLES = 20;
  
  public WillowAi(MissileAPI missile, ShipAPI launchingShip) {
    this.missile = missile;
    MAX_SPEED = missile.getMaxSpeed();
  }
  

  public void advance(float amount)
  {
    if (engine != Global.getCombatEngine()) {
      engine = Global.getCombatEngine();
    }
    
    if ((Global.getCombatEngine().isPaused()) || (missile.isFading()) || (missile.isFizzling())) {
      return;
    }
    

    if ((target == null) || 
      (!Global.getCombatEngine().isEntityInPlay(target)) || 
      (target.getOwner() == missile.getOwner()))
    {
      missile.giveCommand(ShipCommand.ACCELERATE);
      setTarget(findRandomMissileWithinRange(missile));
      return;
    }
    
    timer += amount;
    if (timer > delay) {
      timer -= delay;
      
      float dist = MathUtils.getDistanceSquared(missile.getLocation(), target.getLocation());
      if (dist < 2500.0F) {
        proximityFuse();
        return;
      }
      lead = AIUtils.getBestInterceptPoint(
        missile.getLocation(), 
        MAX_SPEED, 
        target.getLocation(), 
        target.getVelocity());
      
      if (lead == null) {
        lead = target.getLocation();
      }
    }
    

    float correctAngle = VectorUtils.getAngle(
      missile.getLocation(), 
      lead);
    


    float offCourseAngle = MathUtils.getShortestRotation(
      VectorUtils.getFacing(missile.getVelocity()), 
      correctAngle);
    

    float correction = MathUtils.getShortestRotation(
      correctAngle, 
      VectorUtils.getFacing(missile.getVelocity()) + 180.0F) * 
      
      0.5F * 
      (float)FastTrig.sin(0.03490658503988659D * Math.min(Math.abs(offCourseAngle), 45.0F));
    

    correctAngle += correction;
    

    float aimAngle = MathUtils.getShortestRotation(missile.getFacing(), correctAngle);
    if (aimAngle < 0.0F) {
      missile.giveCommand(ShipCommand.TURN_RIGHT);
    } else {
      missile.giveCommand(ShipCommand.TURN_LEFT);
    }
    if (Math.abs(aimAngle) < 45.0F) {
      missile.giveCommand(ShipCommand.ACCELERATE);
    }
    

    if (Math.abs(aimAngle) < Math.abs(missile.getAngularVelocity()) * 0.2F) {
      missile.setAngularVelocity(aimAngle / 0.2F);
    }
  }
  
  private CombatEntityAPI findRandomMissileWithinRange(MissileAPI missile)
  {
    CombatEntityAPI theTarget = AIUtils.getNearestEnemyMissile(missile);
    

    if ((theTarget == null) || (!MathUtils.isWithinRange(theTarget, missile, 750.0F))) {
      theTarget = AIUtils.getNearestEnemy(missile);
      if ((theTarget == null) || (!MathUtils.isWithinRange(theTarget, missile, 750.0F))) {
        return null;
      }
      return theTarget;
    }
    


    WeaponAPI weapon = missile.getWeapon();
    Map<Integer, MissileAPI> PRIORITYLIST = new HashMap();
    Map<Integer, MissileAPI> OTHERSLIST = new HashMap();
    int i = 1;int u = 1;
    List<MissileAPI> potentialTargets = AIUtils.getNearbyEnemyMissiles(missile, 500.0F);
    
    for (MissileAPI m : potentialTargets)
    {







      if (Math.abs(MathUtils.getShortestRotation(weapon.getCurrAngle(), VectorUtils.getAngle(weapon.getLocation(), m.getLocation()))) < 10.0F)
      {
        PRIORITYLIST.put(Integer.valueOf(u), m);
        u++;
      } else {
        OTHERSLIST.put(Integer.valueOf(i), m);
        i++;
      }
    }
    
    if (!PRIORITYLIST.containsValue((MissileAPI)theTarget)) {
      if (!PRIORITYLIST.isEmpty()) {
        int chooser = Math.round((float)Math.random() * (i - 1) + 0.5F);
        theTarget = (CombatEntityAPI)PRIORITYLIST.get(Integer.valueOf(chooser));
      } else if (!OTHERSLIST.isEmpty()) {
        int chooser = Math.round((float)Math.random() * (u - 1) + 0.5F);
        theTarget = (CombatEntityAPI)OTHERSLIST.get(Integer.valueOf(chooser));
      }
    }
    return theTarget;
  }
  
  void proximityFuse() {
    engine.applyDamage(
      target, 
      target.getLocation(), 
      missile.getDamageAmount(), 
      DamageType.FRAGMENTATION, 
      0.0F, 
      false, 
      false, 
      missile.getSource());
    
    List<MissileAPI> closeMissiles = AIUtils.getNearbyEnemyMissiles(missile, 100.0F);
    for (MissileAPI cm : closeMissiles)
    {
      if (cm != target)
      {









        float dist = MathUtils.getDistanceSquared(missile.getLocation(), target.getLocation());
        engine.applyDamage(
          cm, 
          cm.getLocation(), 
          2.0F * missile.getDamageAmount() / 3.0F - missile.getDamageAmount() / 3.0F * ((float)FastTrig.cos(3000.0F / (dist + 1000.0F)) + 1.0F), 
          DamageType.FRAGMENTATION, 
          0.0F, 
          false, 
          true, 
          missile.getSource());
      }
    }
    
    engine.addHitParticle(
      missile.getLocation(), 
      new Vector2f(), 
      100.0F, 
      1.0F, 
      0.25F, 
      EXPLOSION_COLOR);
    
    for (int i = 0; i < 20; i++) {
      float axis = (float)Math.random() * 360.0F;
      float range = (float)Math.random() * 100.0F;
      engine.addHitParticle(
        MathUtils.getPointOnCircumference(missile.getLocation(), range / 5.0F, axis), 
        MathUtils.getPointOnCircumference(new Vector2f(), range, axis), 
        2.0F + (float)Math.random() * 2.0F, 
        1.0F, 
        1.0F + (float)Math.random(), 
        PARTICLE_COLOR);
    }
    
    engine.applyDamage(
      missile, 
      missile.getLocation(), 
      missile.getHitpoints() * 2.0F, 
      DamageType.FRAGMENTATION, 
      0.0F, 
      false, 
      false, 
      missile);
  }
  

  public CombatEntityAPI getTarget()
  {
    return target;
  }
  
  public void setTarget(CombatEntityAPI target)
  {
    this.target = target;
  }
  
  public void init(CombatEngineAPI engine) {}
}
