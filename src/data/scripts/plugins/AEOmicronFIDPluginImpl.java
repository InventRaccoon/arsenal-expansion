// used to reliably pull Omicron into battles when he's nearby
package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.BattleAPI.BattleSide;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorEntityToken.VisibilityLevel;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.scripts.campaign.ids.AEIDs;

import java.util.HashSet;
import java.util.Set;

public class AEOmicronFIDPluginImpl extends FleetInteractionDialogPluginImpl {

    private static final Set<FactionAPI> omicronFriendlyFactions = new HashSet<>(3);
    private static final Set<FactionAPI> omicronNeverAttackFactions = new HashSet<>(3);

    static {
        omicronFriendlyFactions.add(Global.getSector().getFaction(Factions.INDEPENDENT));
        omicronNeverAttackFactions.add(Global.getSector().getFaction(Factions.PLAYER));
        omicronNeverAttackFactions.add(Global.getSector().getFaction(AEIDs.OMICRON_FACTION));
    }

    protected void pullInNearbyFleets() {
        BattleAPI b = context.getBattle();
        if (!ongoingBattle) {
            b.join(Global.getSector().getPlayerFleet());
        }

        BattleSide playerSide = b.pickSide(Global.getSector().getPlayerFleet());

        boolean hostile = otherFleet.getAI() != null && otherFleet.getAI().isHostileTo(playerFleet);
        if (ongoingBattle) hostile = true;

        //canDecline = otherFleet.getAI() != null && other

//		boolean someJoined = false;
        CampaignFleetAPI actualPlayer = Global.getSector().getPlayerFleet();
        CampaignFleetAPI actualOther = (CampaignFleetAPI) (dialog.getInteractionTarget());

        //textPanel.addParagraph("Projecting nearby fleet movements:");
        //textPanel.addParagraph("You encounter a ");
        pulledIn.clear();

        if (config.pullInStations && !b.isStationInvolved()) {
            SectorEntityToken closestEntity = null;
            CampaignFleetAPI closest = null;
            Pair<SectorEntityToken, CampaignFleetAPI> p = Misc.getNearestStationInSupportRange(actualOther);
            if (p != null) {
                closestEntity = p.one;
                closest = p.two;
            }

            if (closest != null) {
                BattleSide joiningSide = b.pickSide(closest, true);
                boolean canJoin = joiningSide != BattleSide.NO_JOIN;
                if (!config.pullInAllies && joiningSide == playerSide) {
                    canJoin = false;
                }
                if (!config.pullInEnemies && joiningSide != playerSide) {
                    canJoin = false;
                }
                if (b == closest.getBattle()) {
                    canJoin = false;
                }
                if (closest.getBattle() != null) {
                    canJoin = false;
                }

                if (canJoin) {
                    if (closestEntity != null) {
                        closestEntity.getMarket().reapplyIndustries(); // need to pick up station CR value, in some cases
                    }
                    b.join(closest);
                    pulledIn.add(closest);

                    if (!config.straightToEngage && config.showPullInText) {
                        if (b.getSide(playerSide) == b.getSideFor(closest)) {
                            textPanel.addParagraph(
                                    Misc.ucFirst(closest.getNameWithFactionKeepCase()) + ": supporting your forces.");//, FRIEND_COLOR);
                        } else {
                            if (hostile) {
                                textPanel.addParagraph(Misc.ucFirst(closest.getNameWithFactionKeepCase()) + ": supporting the enemy.");//, ENEMY_COLOR);
                            } else {
                                textPanel.addParagraph(Misc.ucFirst(closest.getNameWithFactionKeepCase()) + ": supporting the opposing side.");
                            }
                        }
                        textPanel.highlightFirstInLastPara(closest.getNameWithFactionKeepCase() + ":", closest.getFaction().getBaseUIColor());
                    }
                }
            }
        }


        for (CampaignFleetAPI fleet : actualPlayer.getContainingLocation().getFleets()) {
            if (b == fleet.getBattle()) continue;
            if (fleet.getBattle() != null) continue;

            if (fleet.isStationMode()) continue;

            float dist = Misc.getDistance(actualOther.getLocation(), fleet.getLocation());
            dist -= actualOther.getRadius();
            dist -= fleet.getRadius();

            if (fleet.getFleetData().getNumMembers() <= 0) continue;

            float baseSensorRange = playerFleet.getBaseSensorRangeToDetect(fleet.getSensorProfile());
            boolean visible = fleet.isVisibleToPlayerFleet();
            VisibilityLevel level = fleet.getVisibilityLevelToPlayerFleet();

            float joinRange = Misc.getBattleJoinRange();
            if (fleet.getFaction().isPlayerFaction() && !fleet.isStationMode()) {
                joinRange += Global.getSettings().getFloat("battleJoinRangePlayerFactionBonus");
            }

            if (fleet.getMemoryWithoutUpdate().contains("$omicronescort") && dist < 1500 && !omicronNeverAttackFactions.contains(actualOther.getFaction()) && (!omicronFriendlyFactions.contains(actualOther.getFaction()) || actualOther.getMemoryWithoutUpdate().contains(MemFlags.MEMORY_KEY_LOW_REP_IMPACT) || actualOther.getMemoryWithoutUpdate().contains(MemFlags.MEMORY_KEY_NO_REP_IMPACT)) && !fleet.isHostileTo(playerFleet)) {
                BattleSide joiningSide = b.pickSide(fleet, false);
                if (!config.pullInAllies && joiningSide == playerSide) continue;
                if (joiningSide != playerSide) continue;

                b.join(fleet);
                pulledIn.add(fleet);
                //if (b.isPlayerSide(b.getSideFor(fleet))) {
                if (!config.straightToEngage && config.showPullInText) {
                    if (b.getSide(playerSide) == b.getSideFor(fleet)) {
                        textPanel.addParagraph(Misc.ucFirst(fleet.getNameWithFactionKeepCase()) + ": supporting your forces. Electronic warfare employed to conceal identity.");//, FRIEND_COLOR);
                    }
                    textPanel.highlightFirstInLastPara(fleet.getNameWithFactionKeepCase() + ":", fleet.getFaction().getBaseUIColor());
                }
            }

            if (dist < joinRange &&
                    (dist < baseSensorRange || (visible && level != VisibilityLevel.SENSOR_CONTACT)) &&
                    ((fleet.getAI() != null && fleet.getAI().wantsToJoin(b, true)) || fleet.isStationMode()) && !pulledIn.contains(fleet)) {

                BattleSide joiningSide = b.pickSide(fleet, true);
                if (!config.pullInAllies && joiningSide == playerSide) continue;
                if (!config.pullInEnemies && joiningSide != playerSide) continue;

                b.join(fleet);
                pulledIn.add(fleet);
                //if (b.isPlayerSide(b.getSideFor(fleet))) {
                if (!config.straightToEngage && config.showPullInText) {
                    if (b.getSide(playerSide) == b.getSideFor(fleet)) {
                        textPanel.addParagraph(Misc.ucFirst(fleet.getNameWithFactionKeepCase()) + ": supporting your forces.");//, FRIEND_COLOR);
                    } else {
                        if (hostile) {
                            textPanel.addParagraph(Misc.ucFirst(fleet.getNameWithFactionKeepCase()) + ": joining the enemy.");//, ENEMY_COLOR);
                        } else {
                            textPanel.addParagraph(Misc.ucFirst(fleet.getNameWithFactionKeepCase()) + ": supporting the opposing side.");
                        }
                    }
                    textPanel.highlightFirstInLastPara(fleet.getNameWithFactionKeepCase() + ":", fleet.getFaction().getBaseUIColor());
                }
//				someJoined = true;
            }
        }

        if (otherFleet != null) otherFleet.inflateIfNeeded();
        for (CampaignFleetAPI curr : pulledIn) {
            curr.inflateIfNeeded();
        }

//		if (!someJoined) {
//			addText("No nearby fleets will join the battle.");
//		}
        if (!ongoingBattle) {
            b.genCombined();
            b.takeSnapshots();
            playerFleet = b.getPlayerCombined();
            otherFleet = b.getNonPlayerCombined();
            if (!config.straightToEngage) {
                showFleetInfo();
            }
        }

    }

}
