package me.gamma.tickwarp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import net.minecraft.command.CommandSource;

public class TickWarpCommand {
	
	public static void register(TickWarp instance, CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			literal("tickwarp")
			.then(
					literal("rate")
					.then(
							argument("rate", createTickRateArgument())
							.executes(context -> {
								instance.updateTickRate(LongArgumentType.getLong(context, "rate"));
								return 0;
							})
							.then(
									literal("client")
									.executes(context -> {
										instance.updateClientTickRate(LongArgumentType.getLong(context, "rate"));
										return 0;
									})
							)
							.then(
									literal("server")
									.executes(context -> {
										instance.updateServerTickRate(LongArgumentType.getLong(context, "rate"));
										return 0;
									})
							)
					)
			).then(
					literal("warp")
					.then(
							literal("start")
							.then(
									argument("ticks", LongArgumentType.longArg(0))
									.executes(context -> {
										instance.getTimer().startWarp(LongArgumentType.getLong(context, "ticks"), context.getSource());
										return 0;
									})
							)
					).then(
							literal("pause")
							.executes(context -> {
								instance.getTimer().pauseWarp(context.getSource());
								return 0;
							})
					).then(
							literal("continue")
							.executes(context -> {
								instance.getTimer().continueWarp(context.getSource());
								return 0;
							})
					).then(
							literal("break")
							.executes(context -> {
								instance.getTimer().stopWarp(context.getSource());
								return 0;
							})
					).then(
							literal("info")
							.executes(context -> {
								instance.getTimer().printWarpInfo(context.getSource());
								return 0;
							})
					)
			)
		);
	}
	
	private static LongArgumentType createTickRateArgument() {
		return LongArgumentType.longArg(TickWarp.MIN_TICK_LENGTH);
	}
	
	private static LiteralArgumentBuilder<CommandSource> literal(String literal) {
		return LiteralArgumentBuilder.literal(literal);
	}
	
	private static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
	
}
