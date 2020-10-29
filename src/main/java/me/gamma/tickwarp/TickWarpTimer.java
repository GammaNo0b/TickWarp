package me.gamma.tickwarp;

import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TickWarpTimer {

	private TickWarp tickwarp;
	private MinecraftServer server;
	private long tickWarpLength;
	private long ticksToWarp;
	private long timeStarted;
	private long timeNeeded;
	private boolean warpPaused;

	public TickWarpTimer(TickWarp instance, MinecraftServer server) {
		tickwarp = instance;
		this.server = server;
		tickWarpLength = 0;
		ticksToWarp = 0;
		timeStarted = 0;
		timeNeeded = 0;
		warpPaused = false;
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if(event.phase == Phase.END) {
			if(isTimeWarping() && !isPaused()) {
				if(--ticksToWarp <= 0) {
					tickwarp.updateServerTickRate(TickWarp.DEFAULT_TICK_LENGTH);
					long length = timeNeeded + System.currentTimeMillis() - timeStarted;
					server.getPlayerList().sendMessage(new StringTextComponent(String.format("§aFinished Tickwarp with length §b%d §3ticks §ain §b%d§3ms with §b%.2f§3tps", tickWarpLength, length, tickWarpLength * 1000.0D / (double) length)));
				}
			}
		}
	}

	public void startWarp(long warpLength, CommandSource source) {
		if (!isTimeWarping()) {
			tickwarp.updateServerTickRate(TickWarp.MIN_TICK_LENGTH);
			this.tickWarpLength = warpLength;
			this.ticksToWarp = warpLength;
			this.timeStarted = System.currentTimeMillis();
			this.timeNeeded = 0;
			this.warpPaused = false;
			source.sendErrorMessage(new StringTextComponent(String.format("§aStarted new Tickwarp with length §b%d §3ticks", warpLength)));
		} else {
			source.sendErrorMessage(new StringTextComponent("§eServer is already Tickwarping."));
		}
	}
	
	public void pauseWarp(CommandSource source) {
		if(isTimeWarping()) {
			if(!isPaused()) {
				tickwarp.updateServerTickRate(TickWarp.DEFAULT_TICK_LENGTH);
				timeNeeded += System.currentTimeMillis() - timeStarted;
				warpPaused = true;
				long progress = tickWarpLength - ticksToWarp;
				source.sendErrorMessage(new StringTextComponent(String.format("§aPaused warp with length §b%d §3ticks §aafter §b%d §3ticks §aor §b%d§3ms", tickWarpLength, progress, timeNeeded)));
			} else {
				source.sendErrorMessage(new StringTextComponent("§eTimewarp already paused."));
			}
		} else {
			source.sendErrorMessage(new StringTextComponent("§eNo active Timewarp found."));
		}
	}
	
	public void continueWarp(CommandSource source) {
		if(isTimeWarping()) {
			if(isPaused()) {
				tickwarp.updateServerTickRate(TickWarp.MIN_TICK_LENGTH);
				timeStarted = System.currentTimeMillis();
				warpPaused = false;
				long progress = tickWarpLength - ticksToWarp;
				source.sendErrorMessage(new StringTextComponent(String.format("§aContinue warp with length §b%d §3ticks §aat §b%d §3ticks §aor §b%d§3ms", tickWarpLength, progress, timeNeeded)));
			} else {
				source.sendErrorMessage(new StringTextComponent("§eTimewarp not paused."));
			}
		} else {
			source.sendErrorMessage(new StringTextComponent("§eNo active Timewarp found."));
		}
	}

	public void stopWarp(CommandSource source) {
		if (isTimeWarping()) {
			tickwarp.updateServerTickRate(TickWarp.DEFAULT_TICK_LENGTH);
			if(!isPaused()) {
				timeNeeded += System.currentTimeMillis() - timeStarted;
			}
			long progress = tickWarpLength - ticksToWarp;
			source.sendErrorMessage(new StringTextComponent(String.format("§aStopped warp with length §b%d §3ticks §aafter §b%d §3ticks §aor §b%d§3ms", tickWarpLength, progress, timeNeeded)));
			this.ticksToWarp = 0;
		} else {
			source.sendErrorMessage(new StringTextComponent("§eNo active Timewarp found."));
		}
	}
	
	public void printWarpInfo(CommandSource source) {
		if(isTimeWarping()) {
			long progress = tickWarpLength - ticksToWarp;
			if(!isPaused()) {
				timeNeeded += System.currentTimeMillis() - timeStarted;
			}
			source.sendErrorMessage(new StringTextComponent("§9Current Timewarp:"));
			source.sendErrorMessage(new StringTextComponent(String.format("  §3Length:     §b%d", tickWarpLength)));
			source.sendErrorMessage(new StringTextComponent(String.format("  §3Completed:  §b%d", progress)));
			source.sendErrorMessage(new StringTextComponent(String.format("  §3Time:       §b%d", timeNeeded)));
			source.sendErrorMessage(new StringTextComponent(String.format("  §3TPS:        §b%.2f", progress * 1000.0D / timeNeeded)));
		} else {
			source.sendErrorMessage(new StringTextComponent("§eNo active Timewarp found."));
		}
	}

	public boolean isTimeWarping() {
		return ticksToWarp > 0;
	}

	public boolean isPaused() {
		return warpPaused;
	}

}
