package gvoper.gokis.neoforge;

import gvoper.gokis.GokiSkills;
import gvoper.gokis.client.GokiSkillsClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = GokiSkills.MOD_ID, value = Dist.CLIENT)
public final class GokiNeoForgeClient {

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(GokiSkillsClient.CATEGORY);
        event.register(GokiSkillsClient.OPEN_MENU_KEY);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        GokiSkillsClient.init();
    }
}
