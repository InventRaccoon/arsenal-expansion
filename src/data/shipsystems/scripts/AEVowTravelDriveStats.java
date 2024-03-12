package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual;
import com.fs.starfarer.api.impl.combat.RiftCascadeMineExplosion;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.fs.starfarer.api.impl.combat.NegativeExplosionVisual.NEParams;

public class AEVowTravelDriveStats extends BaseShipSystemScript {

    private List<ShipAPI> have_phased = new ArrayList<>();

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            id = id + "_" + ship.getId();
        } else {
            return;
        }

        if (Global.getCombatEngine().isPaused() || state == State.IDLE) {
            return;
        }

        if (state == State.OUT) {
            have_phased.remove(ship);
            ship.getSystem().setCooldownRemaining(2f);
        }

        float level = effectLevel;
        float levelForAlpha = level;

        if (state == State.IN && !have_phased.contains(ship) && Global.getCombatEngine().getTotalElapsedTime(false) > 1f) {
            NEParams p = RiftCascadeMineExplosion.createStandardRiftParams(new Color(200,125,255,255), ship.getShieldRadiusEvenIfNoShield() + 5f);
            p.fadeOut = 0.25f;
            p.withHitGlow = false;
            p.noiseMag = 2f;
            CombatEntityAPI e = Global.getCombatEngine().addLayeredRenderingPlugin(new NegativeExplosionVisual(p));
            e.getLocation().set(ship.getLocation());

            have_phased.add(ship);
        }

        if (state == State.IN || state == State.ACTIVE) {
            ship.setPhased(true);
            levelForAlpha = level;
            stats.getMaxSpeed().modifyFlat(id, 600f * effectLevel);
            stats.getAcceleration().modifyFlat(id, 600f * effectLevel);

            if (level > 0.5f) {
                ship.setPhased(true);
            } else {
                ship.setPhased(false);
            }
            levelForAlpha = level;
        } else if (state == ShipSystemStatsScript.State.OUT) {
            if (!have_phased.contains(ship) && level < 1f) {
                NEParams p = RiftCascadeMineExplosion.createStandardRiftParams(new Color(200,125,255,255), ship.getShieldRadiusEvenIfNoShield() + 5f);
                p.fadeOut = 0.25f;
                p.withHitGlow = false;
                p.noiseMag = 2f;
                CombatEntityAPI e = Global.getCombatEngine().addLayeredRenderingPlugin(new NegativeExplosionVisual(p));
                e.getLocation().set(ship.getLocation());

                have_phased.add(ship);
            }
            stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
        }

        ship.setExtraAlphaMult(1f - (1f) * levelForAlpha);

        float shipTimeMult = 1f + (2f) * levelForAlpha;
        stats.getTimeMult().modifyMult(id, shipTimeMult);
    }
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = null;
        //boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            //player = ship == Global.getCombatEngine().getPlayerShip();
            //id = id + "_" + ship.getId();
        } else {
            return;
        }

        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);

        ship.setPhased(false);
        ship.setExtraAlphaMult(1f);
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("increased engine power", false);
        }
        return null;
    }

}
