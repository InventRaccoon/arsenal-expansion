// simple rulecommand that ends a quest and gives the player some experience
package data.scripts.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc.Token;
import data.scripts.campaign.intel.bar.events.PromiseIntel;

import java.util.List;
import java.util.Map;

public class AEUpdateEndPromise extends BaseCommandPlugin {

    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    // replace with PlanetAPI if the interaction target is a planet
    protected SectorEntityToken omicron;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
    protected OptionPanelAPI options;
    protected CargoAPI playerCargo;
    protected MemoryAPI memory;
    protected MarketAPI market;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;
    protected FactionAPI faction;

    public AEUpdateEndPromise() {
    }

    public AEUpdateEndPromise(SectorEntityToken entity) {
        init(entity);
    }

    protected void init(SectorEntityToken entity) {
        memory = entity.getMemoryWithoutUpdate();
        this.entity = entity;
        // replace SectorEntityToken with PlanetAPI if interaction target is a planet
        omicron = (SectorEntityToken) entity;
        playerFleet = Global.getSector().getPlayerFleet();
        playerCargo = playerFleet.getCargo();

        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();

        faction = entity.getFaction();

        market = entity.getMarket();


    }

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        this.dialog = dialog;
        this.memoryMap = memoryMap;

        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        entity = dialog.getInteractionTarget();
        init(entity);

        memory = getEntityMemory(memoryMap);

        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();

        if (command.equals("nextStage")) {
            nextStage();
        }

        return true;
    }

    protected void nextStage() {
        OptionPanelAPI options = dialog.getOptionPanel();
        TextPanelAPI text = dialog.getTextPanel();

        PromiseIntel intel = (PromiseIntel) Global.getSector().getIntelManager().getFirstIntel(PromiseIntel.class);
        if (intel != null) {
            Global.getSector().addScript(intel);
            intel.endAfterDelay();
            intel.sendUpdate(PromiseIntel.PromiseStage.DONE, text);
            }
            long xp = PromiseIntel.FINISHED_XP;
        Global.getSector().getPlayerPerson().getStats().addXP(xp);

    }
}