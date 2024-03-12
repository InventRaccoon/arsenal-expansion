package data.scripts.campaign.intel.bar.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.AddedEntity;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PromiseIntel extends BaseIntelPlugin {

	public static enum PromiseStage {
		FIND_MINION,
		GO_TO_OMICRON,
		FIND_SIERRA,
		DONE,
		;
	}
	
	public static int FINISHED_XP = 20000;

	protected SectorEntityToken minion;
	protected SectorEntityToken omicron;
	protected SectorEntityToken station;
	protected StarSystemAPI targetsystem1;
	protected StarSystemAPI targetsystem2;
	protected PromiseBarEvent event;
	public String icon;
	
	protected PromiseStage stage;

	// initial task
	public PromiseIntel(AddedEntity minion, PromiseBarEvent event, AddedEntity omicron, AddedEntity station) {
		this.minion = minion.entity;
		this.event = event;
		this.omicron = omicron.entity;
		this.station = station.entity;

		Misc.makeImportant(minion.entity, "promise");
		minion.entity.getMemoryWithoutUpdate().set("$promise_eventRef", this);
		omicron.entity.getMemoryWithoutUpdate().set("$promise_eventRef", this);
		
		stage = PromiseStage.FIND_MINION;
	}
	
	@Override
	protected void notifyEnded() {
		super.notifyEnded();
		Global.getSector().removeScript(this);

		Misc.makeUnimportant(omicron, "promise");
	}

	// this is triggered by a CallEvent rulecommand in rules.csv when interacting with the disabled frigate
	@Override
	public boolean callEvent(String ruleId, InteractionDialogAPI dialog,
							 List<Token> params, Map<String, MemoryAPI> memoryMap) {
		String action = params.get(0).getString(memoryMap);

		if (action.equals("investigate")) {
			Misc.makeUnimportant(minion, "promise");
			stage = PromiseStage.GO_TO_OMICRON;
			sendUpdate(PromiseStage.GO_TO_OMICRON, dialog.getTextPanel());
			Misc.makeImportant(omicron, "promise");
		}
		else if (action.equals("whoAreYou")) {
			Misc.makeUnimportant(omicron, "promise");
			stage = PromiseStage.FIND_SIERRA;
			sendUpdate(PromiseStage.FIND_SIERRA, dialog.getTextPanel());
			station.removeTag("PromiseUnavailable");
		}
		return true;
	}
	
	@Override
	public void endAfterDelay() {
		stage = PromiseStage.DONE;
		Misc.makeUnimportant(omicron, "promise");
		super.endAfterDelay();
	}

	@Override
	protected void notifyEnding() {
		super.notifyEnding();
	}

	// popup when a new stage begins
	protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode) {
		
		Color h = Misc.getHighlightColor();
		Color g = Misc.getGrayColor();
		float pad = 3f;
		float opad = 10f;
		
		float initPad = pad;
		if (mode == ListInfoMode.IN_DESC) initPad = opad;
		
		Color tc = getBulletColorForMode(mode);
		
		bullet(info);
		boolean isUpdate = getListInfoParam() != null;

		targetsystem1 = PromiseBarEvent.getSystem1();
		targetsystem2 = PromiseBarEvent.getSystem2();

		if (stage == PromiseStage.FIND_MINION) {
			info.addPara("Find the ship in the %s", initPad, h, targetsystem1.getName());
		}
		if (stage == PromiseStage.GO_TO_OMICRON) {
			info.addPara("Explore %s", initPad, h, targetsystem2.getName());
		}
		if (stage == PromiseStage.FIND_SIERRA) {
			info.addPara("Find Sierra", initPad);
		}
		initPad = 0f;
		
		unindent(info);
	}
	
	// setup for intel screen
	@Override
	public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
		Color c = getTitleColor(mode);
		info.setParaSmallInsignia();
		info.addPara(getName(), c, 0f);
		info.setParaFontDefault();
		addBulletPoints(info, mode);
		
	}

	// intel screen description
	@Override
	public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
		Color h = Misc.getHighlightColor();
		Color g = Misc.getGrayColor();
		Color tc = Misc.getTextColor();
		float pad = 3f;
		float opad = 10f;

		StarSystemAPI targetsystem1 = PromiseBarEvent.getSystem1();
		StarSystemAPI targetsystem2 = PromiseBarEvent.getSystem2();

		if (stage == PromiseStage.FIND_MINION) {
			info.addPara("An independent patrol commander told you about an unusual ship " +
					"engaged by a patrol in the %s. It may be worth going there and investigating.", opad, h, targetsystem1.getName());
		} else if (stage == PromiseStage.GO_TO_OMICRON) {
			info.addPara("After investigating the AI ship, you retrieved navigational data " +
						 "leading toward a system named %s.", opad, h, targetsystem2.getName());
		}
		else if (stage == PromiseStage.FIND_SIERRA) {
			info.addPara("You met an AI by the name of Omicron who tasked you with retrieving " +
					"someone named \"Sierra\" from a station within this system.", opad);
		}
		else {
			info.addPara("You returned Sierra to Omicron and received your reward.", opad);
		}
		if (Global.getSettings().isDevMode()) {
			info.addPara("DEVMODE: Omicron is in " + targetsystem2.getName() + "", opad);
		}

		addBulletPoints(info, ListInfoMode.IN_DESC);
		
	}

	// gets an icon depending on the stage of the quest
	@Override
	public String getIcon() {
		if (stage ==PromiseStage.FIND_MINION) {
			return Global.getSettings().getSpriteName("intel", "promise_stage1");
		}
		else if (stage ==PromiseStage.GO_TO_OMICRON) {
			return Global.getSettings().getSpriteName("intel", "promise_stage2");
		}
		else if (stage ==PromiseStage.FIND_SIERRA) {
			return Global.getSettings().getSpriteName("intel", "promise_stage3");
		}
		else if (stage ==PromiseStage.DONE) {
			return Global.getSettings().getSpriteName("intel", "promise_stage3");
		}
		return icon;
	}

	// tags in the intel screen
	@Override
	public Set<String> getIntelTags(SectorMapAPI map) {
		Set<String> tags = super.getIntelTags(map);
		tags.add(Tags.INTEL_STORY);
		tags.add(Tags.INTEL_EXPLORATION);
		return tags;
	}
	
	@Override
	public IntelSortTier getSortTier() {
		return IntelSortTier.TIER_2;
	}

	public String getSortString() {
		return "A Promise";
	}

	// what it's called, with a different name once completed
	public String getName() {
		if (isEnded() || isEnding()) {
			return "A Promise - Completed";
		}
		return "A Promise";
	}
	
	@Override
	public FactionAPI getFactionForUIColors() {
		return super.getFactionForUIColors();
	}

	public String getSmallDescriptionTitle() {
		return getName();
	}

	// where the intel screen points to. if it's the first stage, point towards the disabled ship. otherwise, Omicron.
	@Override
	public SectorEntityToken getMapLocation(SectorMapAPI map) {
		if (stage == PromiseStage.FIND_MINION) {
			return minion;
		}
		return targetsystem2.getStar();
	}
	
	@Override
	public boolean shouldRemoveIntel() {
		return super.shouldRemoveIntel();
	}

	// what noise is made when a new message shows up. api/util/Misc is where this is from
	@Override
	public String getCommMessageSound() {
		return getSoundMajorPosting();
	}
		
}







