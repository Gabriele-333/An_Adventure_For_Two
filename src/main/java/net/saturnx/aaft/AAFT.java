package net.saturnx.aaft;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.saturnx.aaft.client.screen.AAFTMenus;
import net.saturnx.aaft.command.AAFTCommandModify;
import net.saturnx.aaft.event.server.*;
import net.saturnx.aaft.event.server.xp.ExperiencePlayerDeathHandler;
import net.saturnx.aaft.event.server.xp.ExperienceSyncHandler;
import net.saturnx.aaft.item.AAFTCreativeTab;
import net.saturnx.aaft.item.AAFTItem;
import net.saturnx.aaft.network.aaftNetwork;
import org.checkerframework.checker.units.qual.N;
import org.slf4j.Logger;


public abstract class AAFT {

    public static final String MOD_ID = "aaft";
    public static final Logger LOGGER = LogUtils.getLogger();


    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, MOD_ID);

    public AAFT(IEventBus modEventBus, ModContainer modContainer) {


        modEventBus.addListener(aaftNetwork::init);
        AAFTMenus.registerAll(modEventBus);
        AAFTItem.register(modEventBus);
        AAFTCreativeTab.register(modEventBus);


        NeoForge.EVENT_BUS.register(TickEvent.class);
        NeoForge.EVENT_BUS.register(SingleplayerWorldTracker.class);
        NeoForge.EVENT_BUS.register(PlayerJoinHandler.class);
        NeoForge.EVENT_BUS.register(RemindSecondPlayerEvent.class);
        NeoForge.EVENT_BUS.register(ExperienceSyncHandler.class);
        NeoForge.EVENT_BUS.register(ExperiencePlayerDeathHandler.class);


    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        AAFTCommandModify.register(event.getDispatcher());
    }



}
