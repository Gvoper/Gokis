package gvoper.gokis.neoforge;

import gvoper.gokis.GokiSkills;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(GokiSkills.MOD_ID)
public final class GokiNeoForge {
    public GokiNeoForge(IEventBus modBus, ModContainer modContainer) {
        GokiSkills.init();
    }
}
