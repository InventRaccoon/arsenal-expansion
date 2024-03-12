// code for the Request Assistance ability. Creates a fleet (with optional set characteristics), spawns it, and gives it an AI
package data.scripts.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorEntityToken.VisibilityLevel;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.DelayedActionScript;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.TimeoutTracker;
import data.scripts.campaign.ids.AEIDs;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static com.fs.starfarer.api.impl.campaign.ids.FleetTypes.PATROL_MEDIUM;

public class OmicronCallAbility extends BaseDurationAbility {

	public static class AbilityUseData {
		public long timestamp;
		public Vector2f location;
		public AbilityUseData(long timestamp, Vector2f location) {
			this.timestamp = timestamp;
			this.location = location;
		}

	}

	protected boolean performed = false;

	protected TimeoutTracker<AbilityUseData> uses = new TimeoutTracker<AbilityUseData>();

	protected Object readResolve() {
		super.readResolve();
		if (uses == null) {
			uses = new TimeoutTracker<AbilityUseData>();
		}
		return this;
	}

	@Override
	protected void activateImpl() {
		if (entity.isInCurrentLocation()) {
			VisibilityLevel level = entity.getVisibilityLevelToPlayerFleet();
			if (level != VisibilityLevel.NONE) {
				Global.getSector().addPing(entity, AEIDs.OMICRON_CALL_PING);
			}

			performed = false;
		}

	}

	protected String getActivationText() {
		//return Misc.ucFirst(spec.getName().toLowerCase());
		return "Broadcasting...";
	}

	@Override
	protected void applyEffect(float amount, float level) {
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return;

		if (!performed) {
					float delay = 1f + 1f * (float) Math.random();
					addResponseScript(delay);
			performed = true;
		}
	}

	@Override
	public void advance(float amount) {
		super.advance(amount);

		float days = Global.getSector().getClock().convertToDays(amount);
		uses.advance(days);
	}

	protected void addResponseScript(float delayDays) {
		final CampaignFleetAPI player = getFleet();
		if (player == null) return;
		if (!(player.getContainingLocation() instanceof StarSystemAPI)) return;

		final StarSystemAPI system = (StarSystemAPI) player.getContainingLocation();

		final JumpPointAPI inner = Misc.getDistressJumpPoint(system);
		if (inner == null) return;

		JumpPointAPI outerTemp = null;
		if (inner.getDestinations().size() >= 1) {
			SectorEntityToken test = inner.getDestinations().get(0).getDestination();
			if (test instanceof JumpPointAPI) {
				outerTemp = (JumpPointAPI) test;
			}
		}
		final JumpPointAPI outer = outerTemp;
		if (outer == null) return;

		addHelpScript(delayDays, system, inner, outer);

	}

    // creates the fleet
	protected void addHelpScript(float delayDays,
								 final StarSystemAPI system,
								 final JumpPointAPI inner,
								 final JumpPointAPI outer) {
		Global.getSector().addScript(new DelayedActionScript(delayDays) {
			@Override
			public void doAction() {
				CampaignFleetAPI player = Global.getSector().getPlayerFleet();
				// get the player's fleet points - this is later used so that Omicron's fleet spawns with fleet points
				// that are the same as the player
				float points = player.getFleetPoints();
				if (player == null) return;
				if (points < 10) {
					points = 10;
				}

				String faction = AEIDs.OMICRON_FACTION;
				if (faction == null) return;

				// creates fleet
				FleetParamsV3 params = new FleetParamsV3(
						null,
						null,
						AEIDs.OMICRON_FACTION,
						null,
						PATROL_MEDIUM,
						points, // combatPts
						0f, // freighterPts
						0f, // tankerPts
						0f, // transportPts
						0f, // linerPts
						0f, // utilityPts
						// no source market means that we need to manually set the quality or his fleet will have d-mods
						0.75f // qualityMod
				);
				CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

				// sets up the fleet and commander's settings
				PersonAPI commander = fleet.getCommander();
				fleet.setName("Omicron Escort-Shard");
				fleet.setNoFactionInName(true);
				fleet.addTag("$omicronescort");
				FullName name = new FullName("Omicron Splinter-Mind", "", fleet.getCommander().getGender());
				commander.setName(name);
				commander.setPersonality(Personalities.STEADY);
				commander.getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 1);
				commander.getStats().setSkillLevel(Skills.ELECTRONIC_WARFARE, 1);
				commander.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
				commander.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
				commander.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
				commander.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
				commander.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
				commander.setFaction(AEIDs.OMICRON_FACTION);
				commander.setPostId(AEIDs.OMICRON_POST);
				commander.setRankId(AEIDs.OMICRON_RANK);
				commander.setPortraitSprite("graphics/portraits/ae_portrait_green.png");

				// stop Omicron bugging about transponders, give him a little ! icon
				fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PATROL_ALLOW_TOFF, true);
				// enables custom battle-joining behaviour
				fleet.getMemoryWithoutUpdate().set("$omicronescort", true);

				// spawns the fleet, probably
				Global.getSector().getHyperspace().addEntity(fleet);

				if (!player.isInHyperspace() &&
						(Global.getSector().getHyperspace().getDaysSinceLastPlayerVisit() > 5 ||
								player.getCargo().getFuel() <= 0)) {

					Vector2f loc = outer.getLocation();
					fleet.setLocation(loc.x, loc.y + fleet.getRadius() + 100f);
				} else {
					float dir = (float) Math.random() * 360f;
					if (player.isInHyperspace()) {
						dir = Misc.getAngleInDegrees(player.getLocation(), system.getLocation());
						dir += (float) Math.random() * 120f - 60f;
					}
					Vector2f loc = Misc.getUnitVectorAtDegreeAngle(dir);
					loc.scale(3000f + 1000f * (float) Math.random());
					Vector2f.add(system.getLocation(), loc, loc);
					fleet.setLocation(loc.x, loc.y + fleet.getRadius() + 100f);
				}

				Vector2f loc = Misc.getPointAtRadius(player.getLocation(), 400f + fleet.getRadius());
				SectorEntityToken token = player.getContainingLocation().createToken(loc.x, loc.y);
				JumpPointAPI.JumpDestination dest = new JumpPointAPI.JumpDestination(token, null);
				Global.getSector().doHyperspaceTransition(fleet, fleet, dest);

				// gives the fleet the proper AI
				fleet.addScript(new OmicronEscortAssignmentAI(fleet, system, inner, outer));
			}
		});
	}

	public boolean isUsable() {
		if (!super.isUsable()) return false;
		if (getFleet() == null) return false;

		CampaignFleetAPI fleet = getFleet();
		if (fleet.isInHyperspace() || fleet.isInHyperspaceTransition()) return false;

		return true;
	}


	@Override
	protected void deactivateImpl() {
		cleanupImpl();
	}

	@Override
	protected void cleanupImpl() {
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return;
	}

	@Override
	public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {

		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return;

		Color gray = Misc.getGrayColor();
		Color highlight = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();

		LabelAPI title = tooltip.addTitle(spec.getName());

		float pad = 10f;

		tooltip.addPara("Emit an encrypted communication pulse, calling for aid. " +
				"A splinter of Omicron's AI will arrive to the system via a transverse jump and escort you " +
				"for %s, joining you in combat against any fleet as long as he is within %s.", pad, highlight, "2 months", "1500 units");

		tooltip.addPara("He will bring with him a fleet %s. He is ready to take losses and will salvage more drones for the next " +
						"time you need him. ", pad, highlight,
				"similarly sized to your own");

		tooltip.addPara("Omicron will not join you against Independent merchants or defense fleets.", pad);

		tooltip.addPara("Time taken for broadcast, arrival, escort, and reinforcement " +
				"adds up to %s before another call can be made.", pad, highlight, "180 days");

		if (isOnCooldown()) {
			String cooldown_str = (int) getCooldownLeft() + " days";
			if ((int) getCooldownLeft() <= 1) {
				cooldown_str = "day";
			}
			tooltip.addPara("You cannot call for aid for another %s.", pad, bad, highlight, cooldown_str);
		}

		if (fleet.isInHyperspace()) {
			tooltip.addPara("Can not be used in hyperspace.", bad, pad);
		}

		tooltip.addPara("*2000 units = 1 map grid cell", gray, pad);

		addIncompatibleToTooltip(tooltip, expanded);

	}

	public boolean hasTooltip() {
		return true;
	}

}





