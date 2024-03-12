// important for replacing plugins and other useful things
package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.BaseCampaignPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;

import static data.scripts.campaign.ids.AEIDs.SIERRA_CORE;

public class AECampaignPluginImpl extends BaseCampaignPlugin {

	public String getId() {
		return "AECampaignPluginImpl";
	}

	public void updatePlayerFacts(MemoryAPI memory) {
		CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();

		memory.set("$ae_sierracore", (int)fleet.getCargo().getCommodityQuantity(SIERRA_CORE), 0);
	}

	public PluginPick<InteractionDialogPlugin> pickInteractionDialogPlugin(SectorEntityToken interactionTarget) {
		if (interactionTarget instanceof CampaignFleetAPI && omicronEscortIsNearby()) {
			return new PluginPick<InteractionDialogPlugin>(new AEOmicronFIDPluginImpl(), PickPriority.MOD_SET);
		}
		return null;
	}

	private static boolean omicronEscortIsNearby() {
		boolean omicron = false;
		for (CampaignFleetAPI fleet : Global.getSector().getPlayerFleet().getContainingLocation().getFleets()) {
			if (Misc.getDistance(fleet, Global.getSector().getPlayerFleet()) < 2000 && fleet.getMemory().contains("$omicronescort")) {
				omicron = true;
			}
		}
		omicron = true;
		return omicron;
	}
	
}








