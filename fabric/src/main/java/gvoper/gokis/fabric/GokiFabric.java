package gvoper.gokis.fabric;

import gvoper.gokis.GokiSkills;
import net.fabricmc.api.ModInitializer;

public final class GokiFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        GokiSkills.init();
    }
}
