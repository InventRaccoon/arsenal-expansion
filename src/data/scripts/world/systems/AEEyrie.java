package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class AEEyrie {

    public void generate(SectorAPI sector) {

        // make the system itself!
        StarSystemAPI system = sector.createStarSystem("The Eyrie");
        LocationAPI hyper = Global.getSector().getHyperspace();

        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");

        // create the star
        PlanetAPI star = system.initStar("theeyrie", // unique id for this star
                StarTypes.RED_DWARF,  // id in planets.json
                1100f,          // radius (in pixels at default zoom)
                500); // corona radius, from star edge
        system.setLightColor(new Color(255, 200, 210)); // light color in entire system, affects all entities

        // get rid of the hyperspace around the star
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);

        float minRadius = plugin.getTileSize() * 2f;
        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius * 0.5f, 0, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f, 0.25f);

        // add the important planet,
        PlanetAPI planet1 = system.addPlanet("eyrie_scorn", star, "Scorn", "terran", 235, 100, 3000, 100);
        planet1.setFaction("ae_ixbattlegroup");
        MarketAPI market = Global.getFactory().createMarket("ae_scorn_market", planet1.getName(), 6);
        market.setFactionId("ae_ixbattlegroup");

        market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        market.setPrimaryEntity(planet1);

        market.setFactionId("ae_ixbattlegroup");
        market.addCondition(Conditions.ORE_ABUNDANT);
        market.addCondition(Conditions.RARE_ORE_ABUNDANT);
        market.addCondition(Conditions.VOLATILES_ABUNDANT);
        market.addCondition(Conditions.FARMLAND_ADEQUATE);
        market.addCondition(Conditions.HABITABLE);
        market.addCondition(Conditions.POPULATION_6);

        market.addIndustry(Industries.POPULATION);
        market.addIndustry(Industries.LIGHTINDUSTRY);
        market.addIndustry(Industries.ORBITALWORKS, new ArrayList<>(Arrays.asList(Items.PRISTINE_NANOFORGE)));
        market.addIndustry(Industries.HEAVYBATTERIES);
        market.addIndustry(Industries.MEGAPORT, new ArrayList<>(Arrays.asList(Commodities.ALPHA_CORE)));
        market.addIndustry(Industries.HIGHCOMMAND);
        market.addIndustry(Industries.STARFORTRESS_HIGH, new ArrayList<>(Arrays.asList(Commodities.GAMMA_CORE)));

        market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        market.getTariff().modifyFlat("default_tariff", market.getFaction().getTariffFraction());

        planet1.setMarket(market);
        Global.getSector().getEconomy().addMarket(market, true);


        SectorEntityToken station = system.addCustomEntity("eyrie_roost", "The Roost", "station_side02", "ae_ixbattlegroup");
        station.setCircularOrbitPointingDown(system.getEntityById("eyrie_scorn"), 45, 1000, 50);

        MarketAPI market3 = Global.getFactory().createMarket("ae_roost_market", station.getName(), 5);
        market3.setFactionId("ae_ixbattlegroup");

        market3.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        market3.setPrimaryEntity(station);

        market3.setFactionId("ae_ixbattlegroup");
        market3.addCondition(Conditions.POPULATION_5);

        market3.addIndustry(Industries.POPULATION);
        market3.addIndustry(Industries.HEAVYBATTERIES);
        market3.addIndustry(Industries.MEGAPORT, new ArrayList<>(Arrays.asList(Commodities.ALPHA_CORE)));
        market3.addIndustry(Industries.HIGHCOMMAND);
        market3.addIndustry(Industries.WAYSTATION);
        // no there isn't actually anything in Industries for commerce... wow
        market3.addIndustry("commerce");
        market3.addIndustry(Industries.FUELPROD, new ArrayList<>(Arrays.asList(Items.SYNCHROTRON, Commodities.ALPHA_CORE)));
        market3.addIndustry(Industries.BATTLESTATION_HIGH);

        market3.addSubmarket(Submarkets.SUBMARKET_OPEN);
        market3.addSubmarket(Submarkets.SUBMARKET_BLACK);
        market3.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        market3.getTariff().modifyFlat("default_tariff", market3.getFaction().getTariffFraction());

        station.setMarket(market3);
        Global.getSector().getEconomy().addMarket(market3, true);

        // the other one,
        PlanetAPI planet2 = system.addPlanet("eyrie_wayward", star, "Wayward", "water", 70, 180, 6000, 150);
        planet2.setFaction("ae_ixbattlegroup");

        MarketAPI market2 = Global.getFactory().createMarket("ae_wayward_market", planet2.getName(), 5);
        market2.setFactionId("ae_ixbattlegroup");

        market2.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        market2.setPrimaryEntity(planet2);

        market2.setFactionId("ae_ixbattlegroup");
        market2.addCondition(Conditions.ORGANICS_ABUNDANT);
        market2.addCondition(Conditions.WATER_SURFACE);
        market2.addCondition(Conditions.HABITABLE);
        market2.addCondition(Conditions.POPULATION_5);

        market2.addIndustry(Industries.POPULATION);
        market2.addIndustry(Industries.AQUACULTURE);
        market2.addIndustry(Industries.HEAVYBATTERIES);
        market2.addIndustry(Industries.MEGAPORT, new ArrayList<>(Arrays.asList(Commodities.ALPHA_CORE)));
        market2.addIndustry(Industries.HIGHCOMMAND);
        market2.addIndustry(Industries.REFINING, new ArrayList<>(Arrays.asList(Commodities.ALPHA_CORE)));
        market2.addIndustry(Industries.BATTLESTATION_HIGH);

        market2.addSubmarket(Submarkets.SUBMARKET_OPEN);
        market2.addSubmarket(Submarkets.SUBMARKET_BLACK);
        market2.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        market2.getTariff().modifyFlat("default_tariff", market2.getFaction().getTariffFraction());

        planet2.setMarket(market2);
        Global.getSector().getEconomy().addMarket(market2, true);

        // add rings
        system.addAsteroidBelt(star, 50, 5200, 100, 30, 40, Terrain.ASTEROID_BELT, null);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 4500, 305f, null, null);
        system.addRingBand(planet1, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 1000, 305f, null, null);
        system.addRingBand(star, "misc", "rings_asteroids0", 256f, 1, Color.white, 256f, 4600, 295f, null, null);

        system.addAsteroidBelt(star, 40, 4600, 100, 30, 40, Terrain.ASTEROID_BELT, null);

        // autogenerate jump points
        system.autogenerateHyperspaceJumpPoints(true, true);

        // once all the important stuff is manually set, we can let the game fill the rest of the system with random planets!
        float radiusAfter = StarSystemGenerator.addOrbitingEntities(system, star, StarAge.AVERAGE,
                4, 4, // min/max entities to add
                6800, // radius to start adding at
                3, // name offset - next planet will be <system name> <roman numeral of this parameter + 1>
                true); // whether to use custom or system-name based names

        StarSystemGenerator.addSystemwideNebula(system, StarAge.AVERAGE);

        SectorEntityToken loc1 = system.addCustomEntity(null,null, "comm_relay",Factions.NEUTRAL);
        loc1.setCircularOrbitPointingDown(star, 50 + 60, 4000, 135);

        SectorEntityToken loc2 = system.addCustomEntity(null,null, "sensor_array",Factions.NEUTRAL);
        loc2.setCircularOrbitPointingDown(star, 50 + 130, 7000, 175);

        SectorEntityToken loc3 = system.addCustomEntity(null,null, "nav_buoy",Factions.NEUTRAL);
        loc3.setCircularOrbitPointingDown(star, 50 + 90, 5500, 155);
    }
}
