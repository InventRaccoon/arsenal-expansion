// just a simple on-hit effect for the Edict Stormblaster. Arcs that deal scaling damage and EMP.
package data.scripts.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class AEEdictOnHitEffect implements OnHitEffectPlugin {

    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
                      Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (!shieldHit && target instanceof ShipAPI) {

            float emp = projectile.getEmpAmount() * 1f;
            float dam = projectile.getDamageAmount() * 0.4f;

            engine.spawnEmpArc(projectile.getSource(), point, target, target,
                    DamageType.ENERGY,
                    dam,
                    emp, // emp
                    600f, // max range
                    "tachyon_lance_emp_impact",
                    15f, // thickness
                    new Color(155,75,200,200),
                    new Color(255,255,255,255)
            );

            //engine.spawnProjectile(null, null, "plasma", point, 0, new Vector2f(0, 0));
        }
    }
}