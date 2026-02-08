package net.saturnx.aaft;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.saturnx.aaft.command.AAFTCommandModify;
import net.saturnx.aaft.event.server.*;
import net.saturnx.aaft.effect.AAFTEffects;
import net.saturnx.aaft.event.server.tick.CompassCooldownTickEvent;
import net.saturnx.aaft.event.server.tick.DistanceTickEvent;
import net.saturnx.aaft.event.server.tick.HealthTickEvent;
import net.saturnx.aaft.event.server.tick.LontananzaTickEvent;
import net.saturnx.aaft.event.server.tick.XpTickEvent;
import net.saturnx.aaft.event.server.xp.ExperiencePlayerDeathHandler;
import net.saturnx.aaft.event.server.xp.ExperienceSyncHandler;
import net.saturnx.aaft.item.AAFTCreativeTab;
import net.saturnx.aaft.item.AAFTItem;
import net.saturnx.aaft.network.aaftNetwork;
import org.slf4j.Logger;


public abstract class AAFT {

    public static final String MOD_ID = "aaft";
    public static final Logger LOGGER = LogUtils.getLogger();


    public AAFT(IEventBus modEventBus, ModContainer modContainer) {


        modEventBus.addListener(aaftNetwork::init);
        AAFTItem.register(modEventBus);
        AAFTCreativeTab.register(modEventBus);
        AAFTEffects.register(modEventBus);


        NeoForge.EVENT_BUS.register(XpTickEvent.class);
        NeoForge.EVENT_BUS.register(HealthTickEvent.class);
        NeoForge.EVENT_BUS.register(LontananzaTickEvent.class);
        NeoForge.EVENT_BUS.register(DistanceTickEvent.class);
        NeoForge.EVENT_BUS.register(CompassCooldownTickEvent.class);

        NeoForge.EVENT_BUS.register(SingleplayerWorldTracker.class);
        NeoForge.EVENT_BUS.register(PlayerJoinHandler.class);
        NeoForge.EVENT_BUS.register(CompassCooldownEvents.class);
        NeoForge.EVENT_BUS.register(RemindSecondPlayerEvent.class);
        NeoForge.EVENT_BUS.register(ExperienceSyncHandler.class);
        NeoForge.EVENT_BUS.register(ExperiencePlayerDeathHandler.class);


    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        AAFTCommandModify.register(event.getDispatcher());
    }



}
