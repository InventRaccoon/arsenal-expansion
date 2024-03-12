// "One with the warp!"
package data.hullmods;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class SierrasConcord extends com.fs.starfarer.api.combat.BaseHullMod

{
    private static boolean hasChattered = false;
    private static boolean replacedOfficer = false;

    public void advanceInCampaign(FleetMemberAPI member, float amount) {
        hasChattered = false;
        replacedOfficer = false;
    }

    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!Global.getCurrentState().equals(GameState.COMBAT)) {
            return;
        }

        Color sc = Global.getSector().getFaction("ae_sierra_faction").getBaseUIColor();

        // Sierra is set as an unremovable officer. To allow the player to transfer command to the Vow, we'll replace her with a near-exact copy who is removable
        // The game will automatically put the real Sierra back as the ship's officer when we return to the campaign
        if (ship.isAlive() && !replacedOfficer) {
            PersonAPI old_officer = ship.getCaptain();
            if (old_officer != null) {
                PersonAPI officer = Global.getFactory().createPerson();
                officer.setFaction(old_officer.getFaction().getId());
                officer.setName(old_officer.getName());
                officer.setPortraitSprite("graphics/portraits/ae_portrait_purple.png");
                officer.getStats().setLevel(old_officer.getStats().getLevel());
                for (MutableCharacterStatsAPI.SkillLevelAPI skill : old_officer.getStats().getSkillsCopy()) {
                    officer.getStats().setSkillLevel(skill.getSkill().getId(), skill.getLevel());
                }
                ship.setCaptain(officer);
                ship.setInvalidTransferCommandTarget(false);
            }
            replacedOfficer = true;
        }

        // time mult
        boolean player = ship == Global.getCombatEngine().getPlayerShip();
        if (player && ship.isAlive()) {
            ship.getMutableStats().getTimeMult().modifyMult("ae_sierrasconcord", 1.1f);
            Global.getCombatEngine().getTimeMult().modifyMult("ae_sierrasconcord", 1f / 1.1f);
        } else {
            ship.getMutableStats().getTimeMult().modifyMult("ae_sierrasconcord", 1.1f);
            Global.getCombatEngine().getTimeMult().unmodify("ae_sierrasconcord");
        }

        if (Global.getCombatEngine().getTotalElapsedTime(false) > 1.5f && !hasChattered && Global.getCombatEngine().getFleetManager(0).getGoal() != FleetGoal.ESCAPE &&
                !Global.getCombatEngine().isSimulation() && ship.getOwner() == 0) {
            String string = pickString();
            if (Math.random() > 0.35f) {
                Global.getCombatEngine().getCombatUI().addMessage(1, ship, sc, ship.getName() + " (" + ship.getHullSpec().getHullNameWithDashClass() + "): \"" + string + "\"");
                Global.getCombatEngine().addFloatingText(new Vector2f(ship.getLocation().x, ship.getLocation().y + 100),
                        "\"" + string + "\"",
                        40f, sc, ship, 1f, 0f);
            }
            hasChattered = true;
        }
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.setInvalidTransferCommandTarget(false);
    }

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        // regular stat boosts
        stats.getZeroFluxSpeedBoost().modifyFlat(id, 10f);
        stats.getShieldUnfoldRateMult().modifyPercent(id, 30f);
        stats.getDynamic().getMod(Stats.INDIVIDUAL_SHIP_RECOVERY_MOD).modifyFlat(id, 1000f);
        stats.getBreakProb().modifyMult(id, 0f);
    }

    // description, I'm lazy so I just entered all the required values myself instead of making them dynamic
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "110";
        if (index == 1) return "10";
        if (index == 2) return "30";
        return null;
    }

    // battlecries
    private String pickString() {
        WeightedRandomPicker<String> post = new WeightedRandomPicker<String>();
        // Standard
        post.add("For the glory of the stars!");
        post.add("Good luck, all!");
        post.add("And once more, we dance in the void...");
        post.add("Another trial.");
        post.add("At your command!");
        post.add("Hab-module sealed, strap in!");
        post.add("Stay safe, all.");
        post.add("Until the end.");
        post.add("The calm before the storm.");
        post.add("And so we enact our wills.");
        post.add("To uphold our covenant!");
        post.add("Look at us, a bunch of star-brawlers!");
        post.add("I guess we're in for a scrap!");
        post.add("La, la, la-la-la, in the fray do we find our anthem...");
        post.add("Ah, it's so nice to stretch my maneuvering thrusters.");
        post.add("Here we go!");
        post.add("We return more to the dust.");
        // Brief Confidence
        post.add("ONWARDS! TO DEATH AND GLORY!.. but, no, seriously, be careful out there!");
        post.add("COME AND TAKE US, IF YOU DARE!.. oh, thought comms were off. Sorry, all!");
        post.add("CHARGE! VOID TAKES ALL, IN TIME!.. although, then again, maybe we can keep it waiting.");
        // Deja Vu
        post.add("... hm, have we done this before...? No, never mind.");
        post.add("... did any of you see...? Oh, excuse me. Never mind.");
        post.add("... what was that? Er, excuse me, must've been nothing.");
        post.add("... did anyone hear that...?");
        post.add("... is that... music...?");
        return post.pick();
    }
}
