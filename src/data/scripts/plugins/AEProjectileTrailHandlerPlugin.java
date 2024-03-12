// shamelessly stolen by me, originally made by Nicke535. Comments are his
package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class AEProjectileTrailHandlerPlugin extends BaseEveryFrameCombatPlugin {

    //A map of all the trail sprites used (note that all the sprites must be under AE_fx): ensure this one has the same keys as the other maps
    private static final Map<String, String> TRAIL_SPRITES = new HashMap<String, String>();
    static {
        TRAIL_SPRITES.put("ae_cannonlance", "projectile_trail_fuzzy");
        TRAIL_SPRITES.put("ae_blacksmith_lance", "projectile_trail_fuzzy");
        TRAIL_SPRITES.put("ae_edict_shot", "projectile_trail_weave");

    }

    //A map for known projectiles and their IDs: should be cleared in init
    private Map<DamagingProjectileAPI, Float> projectileTrailIDs = new WeakHashMap<>();

    //Used when doing dual-core sprites
    private Map<DamagingProjectileAPI, Float> projectileTrailIDs2 = new WeakHashMap<>();

    //Used for the Equity
    private Map<DamagingProjectileAPI, Float> projectileTrailIDs3 = new WeakHashMap<>();

    //--------------------------------------THESE ARE ALL MAPS FOR DIFFERENT VISUAL STATS FOR THE TRAILS: THEIR NAMES ARE FAIRLY SELF_EXPLANATORY---------------------------------------------------
    private static final Map<String, Float> TRAIL_DURATIONS_IN = new HashMap<String, Float>();
    static {
        TRAIL_DURATIONS_IN.put("ae_cannonlance", 0f);
        TRAIL_DURATIONS_IN.put("ae_blacksmith_lance", 0f);
        TRAIL_DURATIONS_IN.put("ae_edict_shot", 0.1f);
    }
    private static final Map<String, Float> TRAIL_DURATIONS_MAIN = new HashMap<String, Float>();
    static {
        TRAIL_DURATIONS_MAIN.put("ae_cannonlance", 0f);
        TRAIL_DURATIONS_MAIN.put("ae_blacksmith_lance", 0f);
        TRAIL_DURATIONS_MAIN.put("ae_edict_shot", 0.1f);
    }
    private static final Map<String, Float> TRAIL_DURATIONS_OUT = new HashMap<String, Float>();
    static {
        TRAIL_DURATIONS_OUT.put("ae_cannonlance", 0.4f);
        TRAIL_DURATIONS_OUT.put("ae_blacksmith_lance", 0.4f);
        TRAIL_DURATIONS_OUT.put("ae_edict_shot", 0.4f);
    }
    private static final Map<String, Float> START_SIZES = new HashMap<String, Float>();
    static {
        START_SIZES.put("ae_cannonlance", 20f);
        START_SIZES.put("ae_blacksmith_lance", 20f);
        START_SIZES.put("ae_edict_shot", 30f);
    }
    private static final Map<String, Float> END_SIZES = new HashMap<String, Float>();
    static {
        END_SIZES.put("ae_cannonlance", 15f);
        END_SIZES.put("ae_blacksmith_lance", 15f);
        END_SIZES.put("ae_edict_shot", 15f);
    }
    private static final Map<String, Color> TRAIL_START_COLORS = new HashMap<String, Color>();
    static {
        TRAIL_START_COLORS.put("ae_cannonlance", new Color(255,125,60));
        TRAIL_START_COLORS.put("ae_blacksmith_lance", new Color(255,125,60));
        TRAIL_START_COLORS.put("ae_edict_shot", new Color(255,255,255));
    }
    private static final Map<String, Color> TRAIL_END_COLORS = new HashMap<String, Color>();
    static {
        TRAIL_END_COLORS.put("ae_cannonlance", new Color(235,110,40));
        TRAIL_END_COLORS.put("ae_blacksmith_lance", new Color(235,110,40));
        TRAIL_END_COLORS.put("ae_edict_shot", new Color(200,125,255));
    }
    private static final Map<String, Float> TRAIL_OPACITIES = new HashMap<String, Float>();
    static {
        TRAIL_OPACITIES.put("ae_cannonlance", 0.8f);
        TRAIL_OPACITIES.put("ae_blacksmith_lance", 0.8f);
        TRAIL_OPACITIES.put("ae_edict_shot", 0.4f);
    }
    private static final Map<String, Integer> TRAIL_BLEND_SRC = new HashMap<String, Integer>();
    static {
        TRAIL_BLEND_SRC.put("ae_cannonlance", GL_SRC_ALPHA);
        TRAIL_BLEND_SRC.put("ae_blacksmith_lance", GL_SRC_ALPHA);
        TRAIL_BLEND_SRC.put("ae_edict_shot", GL_SRC_ALPHA);
    }
    private static final Map<String, Integer> TRAIL_BLEND_DEST = new HashMap<String, Integer>();
    static {
        TRAIL_BLEND_DEST.put("ae_cannonlance", GL_ONE);
        TRAIL_BLEND_DEST.put("ae_blacksmith_lance", GL_ONE);
        TRAIL_BLEND_DEST.put("ae_edict_shot", GL_ONE);
    }
    private static final Map<String, Float> TRAIL_LOOP_LENGTHS = new HashMap<String, Float>();
    static {
        TRAIL_LOOP_LENGTHS.put("ae_cannonlance", -1f);
        TRAIL_LOOP_LENGTHS.put("ae_blacksmith_lance", -1f);
        TRAIL_LOOP_LENGTHS.put("ae_edict_shot", -1f);
    }
    private static final Map<String, Float> TRAIL_SCROLL_SPEEDS = new HashMap<String, Float>();
    static {
        TRAIL_SCROLL_SPEEDS.put("ae_cannonlance", 0f);
        TRAIL_SCROLL_SPEEDS.put("ae_blacksmith_lance", 0f);
        TRAIL_SCROLL_SPEEDS.put("ae_edict_shot", 0f);
    }

    @Override
    public void init(CombatEngineAPI engine) {
        //Reinitialize the lists
        projectileTrailIDs.clear();
    }

    @Override
    public void advance (float amount, List<InputEventAPI> events) {
        if (Global.getCombatEngine() == null || Global.getCombatEngine().isPaused()) {
            return;
        }
        CombatEngineAPI engine = Global.getCombatEngine();

        //Runs once on each projectile that matches one of the IDs specified in our maps
        for (DamagingProjectileAPI proj : engine.getProjectiles()) {
            //Ignore already-collided projectiles, and projectiles that don't match our IDs
            if (proj.getProjectileSpecId() == null || proj.didDamage()) {
                continue;
            }

            //-------------------------------------------For visual effects---------------------------------------------
            if (!TRAIL_SPRITES.keySet().contains(proj.getProjectileSpecId())) {
                continue;
            }
            String specID = proj.getProjectileSpecId();
            SpriteAPI spriteToUse = Global.getSettings().getSprite("AE_fx", TRAIL_SPRITES.get(specID));

            //If we haven't already started a trail for this projectile, get an ID for it
            if (projectileTrailIDs.get(proj) == null) {
                projectileTrailIDs.put(proj, MagicTrailPlugin.getUniqueID());
            }

            //Then, actually spawn a trail
            //MagicTrailPlugin.AddTrailMemberAdvanced(proj, projectileTrailIDs.get(proj), spriteToUse, proj.getLocation(), 0f, 0f, proj.getFacing() - 180f,
            //0f, 0f, START_SIZES.get(specID), END_SIZES.get(specID), TRAIL_START_COLORS.get(specID), TRAIL_END_COLORS.get(specID),
            //            TRAIL_OPACITIES.get(specID), TRAIL_DURATIONS_IN.get(specID), TRAIL_DURATIONS_MAIN.get(specID), TRAIL_DURATIONS_OUT.get(specID), TRAIL_BLEND_SRC.get(specID),
            //            TRAIL_BLEND_DEST.get(specID), TRAIL_LOOP_LENGTHS.get(specID), TRAIL_SCROLL_SPEEDS.get(specID));

            MagicTrailPlugin.AddTrailMemberAdvanced(proj, projectileTrailIDs.get(proj), spriteToUse, proj.getLocation(), 0f, 0f, proj.getFacing() - 180f,
            0f, 0f, START_SIZES.get(specID), END_SIZES.get(specID), TRAIL_START_COLORS.get(specID), TRAIL_END_COLORS.get(specID),
                        TRAIL_OPACITIES.get(specID), TRAIL_DURATIONS_IN.get(specID), TRAIL_DURATIONS_MAIN.get(specID), TRAIL_DURATIONS_OUT.get(specID), TRAIL_BLEND_SRC.get(specID),
                        TRAIL_BLEND_DEST.get(specID), TRAIL_LOOP_LENGTHS.get(specID), TRAIL_SCROLL_SPEEDS.get(specID), new Vector2f(0f, 0f), null);

            if (specID.contains("ae_edict_shot")) {
                //If we haven't already started a second trail for this projectile, get an ID for it
                if (projectileTrailIDs2.get(proj) == null) {
                    projectileTrailIDs2.put(proj, MagicTrailPlugin.getUniqueID());
                }
                MagicTrailPlugin.AddTrailMemberAdvanced(proj, projectileTrailIDs2.get(proj), spriteToUse, proj.getLocation(), 0f, 0f, proj.getFacing() - 180f,
                        0f, 0f, START_SIZES.get(specID), END_SIZES.get(specID), TRAIL_START_COLORS.get(specID), TRAIL_END_COLORS.get(specID),
                        TRAIL_OPACITIES.get(specID), TRAIL_DURATIONS_IN.get(specID), TRAIL_DURATIONS_MAIN.get(specID), TRAIL_DURATIONS_OUT.get(specID), TRAIL_BLEND_SRC.get(specID),
                        TRAIL_BLEND_DEST.get(specID), TRAIL_LOOP_LENGTHS.get(specID), TRAIL_SCROLL_SPEEDS.get(specID), new Vector2f(0f, 0f), null);
            }

        }
    }
}