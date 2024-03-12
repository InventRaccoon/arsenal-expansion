// displays a portrait-sized image in the AE_portraits category, without the need for an actual NPC.
// easily modified for any other purpose - just change the category and settings on line 30 (showImagePortion)
package data.scripts.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc.Token;

import java.util.List;
import java.util.Map;

public class AEPersonVisual extends BaseCommandPlugin {

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {

		String category = "AE_portraits";
		String key = null;

		if (params.size() <= 1) {
			key = params.get(0).string;
		} else {
			category = params.get(0).string;
			key = params.get(1).string;
		}

		SpriteAPI sprite = Global.getSettings().getSprite(category, key);
		dialog.getVisualPanel().showImagePortion(category, key, sprite.getWidth(), sprite.getHeight(), 0, 0, 128, 128);
		return true;
	}

}
