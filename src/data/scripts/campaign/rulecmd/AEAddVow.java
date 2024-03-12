// adds the Vow and stuff Sierra in its officer slot
package data.scripts.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class AEAddVow extends BaseCommandPlugin

{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap)
    {
        final MemoryAPI localMemory = memoryMap.get(MemKeys.LOCAL);
        if (localMemory == null) return false;
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

        PersonAPI sierra = Global.getFactory().createPerson();
        sierra.setName(new FullName("Sierra", "", FullName.Gender.FEMALE));
        sierra.setRankId(null);
        sierra.setPostId(null);
        sierra.setPortraitSprite("graphics/portraits/ae_portrait_purple.png");
        sierra.setPersonality(Personalities.STEADY);
        sierra.getStats().setLevel(8);
        sierra.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
        sierra.getStats().setSkillLevel(Skills.SHIELD_MODULATION, 2);
        sierra.getStats().setSkillLevel(Skills.PHASE_MASTERY, 2);
        sierra.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
        sierra.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
        sierra.getStats().setSkillLevel(Skills.ENERGY_WEAPON_MASTERY, 2);
        sierra.getStats().setSkillLevel(Skills.POINT_DEFENSE, 2);
        sierra.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
        playerFleet.getFleetData().addOfficer(sierra);
        sierra.setFaction("ae_sierra_faction");

        FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "vow_base");
        member.setShipName("Voidwitch");
        member.setCaptain(sierra);
        member.getVariant().addTag(Tags.SHIP_CAN_NOT_SCUTTLE);
        member.getVariant().addPermaMod(HullMods.GLITCHED_SENSORS);
        member.getVariant().addPermaMod(HullMods.COMP_STRUCTURE);

        playerFleet.getFleetData().addFleetMember(member);
        AddRemoveCommodity.addFleetMemberGainText(member, dialog.getTextPanel());
        Misc.setUnremovable(sierra, true);
        return true;
    }
}
