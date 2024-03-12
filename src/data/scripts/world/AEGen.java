// handles everything that is always present, specifically frontier secrets and Omicron
package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import data.scripts.campaign.ids.AEIDs;
import data.scripts.world.systems.AEEyrie;

import java.util.List;

public class AEGen implements SectorGeneratorPlugin
{
    @Override
    public void generate(SectorAPI sector)
    {
        initFactionRelationships(sector);
    }

    public void ix(SectorAPI sector)
    {
        new AEEyrie().generate(sector);

        Global.getSector().getFaction(AEIDs.IX_FACTION).setShowInIntelTab(true);

        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI ix = sector.getFaction(AEIDs.IX_FACTION);
        FactionAPI independent = sector.getFaction(Factions.INDEPENDENT);

        // WE ARE THE DOMAIN AND WE HAVE DECREED YOUR END
        List<FactionAPI> factionList = sector.getAllFactions();
        factionList.remove(ix);
        factionList.remove(independent);
        factionList.remove(pirates);
        for (FactionAPI faction : factionList) {
            ix.setRelationship(faction.getId(), RepLevel.VENGEFUL);
        }
        ix.setRelationship("player", RepLevel.VENGEFUL);
    }

    public static void initFactionRelationships(SectorAPI sector) {
        FactionAPI player = sector.getPlayerFaction();
        FactionAPI omicron = sector.getFaction(AEIDs.OMICRON_FACTION);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI remnants = sector.getFaction(Factions.REMNANTS);

        player.setRelationship(omicron.getId(), RepLevel.FRIENDLY);
        // sees you as a potential asset
        omicron.setRelationship(player.getId(), RepLevel.FRIENDLY);
        // AI-friendly
        omicron.setRelationship("sylphon", RepLevel.FAVORABLE);
        // a danger to stability in the Sector
        omicron.setRelationship(pirates.getId(), RepLevel.HOSTILE);
        // being an AI, safe to say he hates them
        omicron.setRelationship(path.getId(), RepLevel.HOSTILE);
        // either foes or potential minions
        omicron.setRelationship(remnants.getId(), RepLevel.HOSTILE);
        // disapproves of their thinly-veiled terrorism and extortion of the downtrodden
        omicron.setRelationship("cabal", RepLevel.HOSTILE);
        // mostly just because the player wants him to help kill them
        omicron.setRelationship("famous_bounty", RepLevel.HOSTILE);
        // omnicidal maniacs, a threat to human life in the Sector
        omicron.setRelationship("templars", RepLevel.HOSTILE);

        pirates.setRelationship(omicron.getId(),RepLevel.HOSTILE);
        path.setRelationship(omicron.getId(),RepLevel.HOSTILE);
        remnants.setRelationship(omicron.getId(),RepLevel.HOSTILE);
    }

}
