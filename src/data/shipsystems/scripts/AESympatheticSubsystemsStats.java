package data.shipsystems.scripts;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

public class AESympatheticSubsystemsStats extends BaseShipSystemScript {

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
    }	

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        return null;
    }
}