package me.gamma.tickwarp;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Timer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod(TickWarp.MOD_ID)
public class TickWarp {

	public static final String MOD_ID = "tickwarp";

	public static final long DEFAULT_TICK_LENGTH = 50L;
	public static final long MIN_TICK_LENGTH = 1L;

	public static long TICK_LENGTH = DEFAULT_TICK_LENGTH;

	private MinecraftServer server;
	private TickWarpTimer timer;

	public TickWarp() {
		timer = new TickWarpTimer(this, server);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(timer);
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		this.server = event.getServer();
		TickWarpCommand.register(this, event.getCommandDispatcher());
	}

	public void updateTickRate(long tickLength) {
		this.updateClientTickRate(tickLength);
		this.updateServerTickRate(tickLength);
	}

	public void updateClientTickRate(long tickLength) {
		Timer timer = ReflectionUtils.getValueFromFirstFieldByType(Minecraft.class, Minecraft.getInstance(), Timer.class);
		if (timer != null) {
			ReflectionUtils.setValueToFirstFinalField(Timer.class, timer, (float) tickLength);
		}
	}

	public void updateServerTickRate(long tickLength) {
		TICK_LENGTH = tickLength;
	}

	public TickWarpTimer getTimer() {
		return timer;
	}
}
