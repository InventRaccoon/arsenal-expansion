// there used to be a disclaimer here that I was bad at mod plugins but actually I'm okay at them now
package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.thoughtworks.xstream.XStream;
import data.scripts.ai.WillowAi;
import data.scripts.campaign.abilities.OmicronCallAbility;
import data.scripts.campaign.intel.bar.events.PromiseBarEventCreator;
import data.scripts.plugins.AECampaignPluginImpl;
import data.scripts.world.AEGen;
import org.apache.log4j.Level;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AEmodplugin extends BaseModPlugin {

    private static final String AE_SETTINGS = "ae_settings.ini";
    public static boolean IX_OFF;

    public static final String willow_ID = "willow_pdm";

    public static final boolean isExerelin;

    // check for Nexerelin
    static
    {
        boolean foundExerelin;
        try
        {
            Global.getSettings().getScriptClassLoader().loadClass("data.scripts.world.ExerelinGen");
            foundExerelin = true;
        }
        catch (ClassNotFoundException ex)
        {
            foundExerelin = false;
        }

        isExerelin = foundExerelin;
    }

    // are AE's custom events being tracked? If not, add them
    public void syncAEScripts() {
        addBarEvents();
    }

    // on loading, run the code above.
    public void onGameLoad(boolean newGame) {
        syncAEScripts();
    }

    // on new game, always initialise faction relationships. If the Eyrie is disabled, don't generate it
    public void onNewGame()
    {
        initAE();

        if (IX_OFF)
        {
            return;
        }

        initFrontier();
    }

    private static void loadAESettings() throws IOException, JSONException {
        JSONObject setting = Global.getSettings().loadJSON(AE_SETTINGS);
        IX_OFF = setting.getBoolean("noEyrie");
    }

    private static void initFrontier() {
        new AEGen().ix(Global.getSector());
    }

    // generate frontier secrets
    private static void initAE()
    {
        new AEGen().generate(Global.getSector());
    }

    protected void addBarEvents() {
        BarEventManager bar = BarEventManager.getInstance();
        if (!bar.hasEventCreator(PromiseBarEventCreator.class)) {
            bar.addEventCreator(new PromiseBarEventCreator());
        }
    }

    // override missile AIs with new ones
    @Override
    public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
        switch (missile.getProjectileSpecId()) {
            case willow_ID:
                return new PluginPick<MissileAIPlugin>(new WillowAi(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            default:
        }
        return null;
    }

    // I don't know what this is but it's important
    public void configureXStream(XStream x) {
        x.alias("OmicronCallAbility", OmicronCallAbility.class);
        x.alias("AECampaignPluginImpl", AECampaignPluginImpl.class);
    }

    // load graphicslib stuff and the settings ini
    public void onApplicationLoad()
    {
        boolean hasGraphicsLib = Global.getSettings().getModManager().isModEnabled("shaderLib");
        if (hasGraphicsLib) {
          ShaderLib.init();
          LightData.readLightDataCSV("data/lights/arsenalexp_light_data.csv");
        }
        try {
            loadAESettings();
        } catch (IOException | JSONException e) {
            Global.getLogger(AEmodplugin.class).log(Level.ERROR, "Failed to load ae_settings.ini" + e.getMessage());
        }
    }
}
