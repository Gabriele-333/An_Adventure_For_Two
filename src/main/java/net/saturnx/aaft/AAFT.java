package net.saturnx.aaft;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.saturnx.aaft.event.PlayerJoinHandler;
import net.saturnx.aaft.event.RemindSecondPlayerEvent;
import net.saturnx.aaft.event.SingleplayerWorldTracker;
import net.saturnx.aaft.network.aaftNetwork;
import org.slf4j.Logger;


public abstract class AAFT {

    public static final String MOD_ID = "aaft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AAFT(IEventBus modEventBus) {
        modEventBus.addListener(aaftNetwork::init);


        NeoForge.EVENT_BUS.register(SingleplayerWorldTracker.class);
        NeoForge.EVENT_BUS.register(PlayerJoinHandler.class);
        NeoForge.EVENT_BUS.register(RemindSecondPlayerEvent.class);

    }

}
