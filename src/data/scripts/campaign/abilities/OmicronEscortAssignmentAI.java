// AI given to Omicron when summoned by Request Assistance. Fairly simple - escort the player for 60 days, then leave
package data.scripts.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseAssignmentAI;
import com.fs.starfarer.api.util.Misc;

public class OmicronEscortAssignmentAI extends BaseAssignmentAI {

	protected StarSystemAPI system;

	public static final float FOLLOW_DURATION_PASSIVE = 60;

	protected final JumpPointAPI inner;
	protected final JumpPointAPI outer;

	public OmicronEscortAssignmentAI(CampaignFleetAPI fleet, StarSystemAPI system, JumpPointAPI inner, JumpPointAPI outer) {
		super();
		this.fleet = fleet;
		this.system = system;
		this.inner = inner;
		this.outer = outer;
		
		giveInitialAssignments();
	}

	// go to the outer jump point of the system, then follow the player
	@Override
	protected void giveInitialAssignments() {
		if (fleet.isInHyperspace()) {
			fleet.addAssignment(FleetAssignment.GO_TO_LOCATION, outer, 20f);
		}
		fleet.addAssignmentAtStart(FleetAssignment.ORBIT_AGGRESSIVE, Global.getSector().getPlayerFleet(), FOLLOW_DURATION_PASSIVE, "escorting your fleet", null);
	}

	@Override
	protected void pickNext() {
		MemoryAPI memory = fleet.getMemoryWithoutUpdate();
		memory.unset("$omicronescort");
		Misc.setFlagWithReason(fleet.getMemoryWithoutUpdate(), MemFlags.ENTITY_MISSION_IMPORTANT,
    			   			   "$omicronescort", false, 1000f);
		Misc.giveStandardReturnToSourceAssignments(fleet);
	}

}












