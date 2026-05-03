package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class OwnerCommand {
	private static final SimpleCommandExceptionType ERROR_SET_FAILED = new SimpleCommandExceptionType(Component.translatableWithFallback("commands.securitycraft.owner.set.failed", "There is no ownable block at the given position or it is already owned by the given player"));
	private static final SimpleCommandExceptionType ERROR_FILL_FAILED = new SimpleCommandExceptionType(Component.translatableWithFallback("commands.securitycraft.owner.fill.failed", "There are no ownable blocks in the given area or they are already owned by the given player"));

	private OwnerCommand() {}

	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		//@formatter:off
		return Commands.literal("owner")
                .requires(ctx -> ctx.hasPermission(2))
                .then(Commands.literal("set")
                		.then(Commands.argument("pos", BlockPosArgument.blockPos())
                				.then(Commands.literal("reset")
                						.executes(ctx -> setOwner(ctx, "ownerUUID", "owner", false))
										.then(Commands.literal("resetSettings")
												.executes(ctx -> setOwner(ctx, "ownerUUID", "owner", true))))
                				.then(Commands.literal("random")
                						.executes(ctx -> setRandomOwner(ctx, false))
										.then(Commands.literal("resetSettings")
												.executes(ctx -> setRandomOwner(ctx, true))))
                				.then(Commands.literal("player")
                						.then(Commands.argument("owner", SingleGameProfileArgument.singleGameProfile())
                								.executes(ctx -> setOwner(ctx, SingleGameProfileArgument.getGameProfile(ctx, "owner"), false))
												.then(Commands.literal("resetSettings")
														.executes(ctx -> setOwner(ctx, SingleGameProfileArgument.getGameProfile(ctx, "owner"), true)))))))
                .then(Commands.literal("fill")
                		.then(Commands.argument("from", BlockPosArgument.blockPos())
                				.then(Commands.argument("to", BlockPosArgument.blockPos())
                						.then(Commands.literal("reset")
                        						.executes(ctx -> fillOwner(ctx, "ownerUUID", "owner", false))
												.then(Commands.literal("resetSettings")
														.executes(ctx -> fillOwner(ctx, "ownerUUID", "owner", true))))
                        				.then(Commands.literal("random")
                        						.executes(ctx -> fillRandomOwner(ctx, false))
												.then(Commands.literal("resetSettings")
														.executes(ctx -> fillRandomOwner(ctx, true))))
                        				.then(Commands.literal("player")
                        						.then(Commands.argument("owner", SingleGameProfileArgument.singleGameProfile())
                        								.executes(ctx -> fillOwner(ctx, SingleGameProfileArgument.getGameProfile(ctx, "owner"), false))
														.then(Commands.literal("resetSettings")
																.executes(ctx -> fillOwner(ctx, SingleGameProfileArgument.getGameProfile(ctx, "owner"), true))))))));
		//@formatter:on
	}

	private static int setRandomOwner(CommandContext<CommandSourceStack> ctx, boolean resetSettings) throws CommandSyntaxException {
		return setOwner(ctx, UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(10), resetSettings);
	}

	private static int setOwner(CommandContext<CommandSourceStack> ctx, GameProfile gameProfile, boolean resetSettings) throws CommandSyntaxException {
		return setOwner(ctx, gameProfile.getId().toString(), gameProfile.getName(), resetSettings);
	}

	private static int setOwner(CommandContext<CommandSourceStack> ctx, String uuid, String name, boolean resetSettings) throws CommandSyntaxException {
		BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
		CommandSourceStack source = ctx.getSource();
		ServerLevel level = source.getLevel();

		if (!(level.getBlockEntity(pos) instanceof IOwnable ownable))
			throw ERROR_SET_FAILED.create();

		Owner previousOwner = ownable.getOwner();

		if (!previousOwner.getUUID().equals(uuid) || !previousOwner.getName().equals(name)) {
			BlockState state = ((BlockEntity) ownable).getBlockState();
			Owner oldOwner = ownable.getOwner().copy();

			ownable.setOwner(uuid, name);

			if (resetSettings)
				ownable.onOwnerChanged(state, level, pos, null, oldOwner, ownable.getOwner());

			level.sendBlockUpdated(pos, state, state, 3);
			level.getChunkSource().chunkMap.resendBiomesForChunks(List.of(level.getChunk(pos))); //Queues chunks with modified blocks to be sent to the client, so reinforced block tints are updated properly
			source.sendSuccess(() -> Component.translatableWithFallback("commands.securitycraft.owner.set.success", "Set the owner at %s, %s, %s", pos.getX(), pos.getY(), pos.getZ()), true);
			return 1;
		}
		else
			throw ERROR_SET_FAILED.create();
	}

	private static int fillRandomOwner(CommandContext<CommandSourceStack> ctx, boolean resetSettings) throws CommandSyntaxException {
		return fillOwner(ctx, UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(10), resetSettings);
	}

	private static int fillOwner(CommandContext<CommandSourceStack> ctx, GameProfile gameProfile, boolean resetSettings) throws CommandSyntaxException {
		return fillOwner(ctx, gameProfile.getId().toString(), gameProfile.getName(), resetSettings);
	}

	private static int fillOwner(CommandContext<CommandSourceStack> ctx, String uuid, String name, boolean resetSettings) throws CommandSyntaxException {
		BoundingBox area = BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(ctx, "from"), BlockPosArgument.getLoadedBlockPos(ctx, "to"));
		CommandSourceStack source = ctx.getSource();
		ServerLevel level = source.getLevel();
		Set<ChunkAccess> modifiedChunks = new HashSet<>();
		int blockCount = area.getXSpan() * area.getYSpan() * area.getZSpan();
		int commandModificationBlockLimit = level.getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);

		if (blockCount > commandModificationBlockLimit)
			throw FillCommand.ERROR_AREA_TOO_LARGE.create(commandModificationBlockLimit, blockCount);
		else {
			int blocksModified = 0;

			for (BlockPos pos : BlockPos.betweenClosed(area.minX(), area.minY(), area.minZ(), area.maxX(), area.maxY(), area.maxZ())) {
				BlockEntity be = level.getBlockEntity(pos);

				if (be instanceof IOwnable ownable) {
					Owner previousOwner = ownable.getOwner();

					if (!previousOwner.getUUID().equals(uuid) || !previousOwner.getName().equals(name)) {
						BlockState state = be.getBlockState();
						Owner oldOwner = ownable.getOwner().copy();

						ownable.setOwner(uuid, name);

						if (resetSettings)
							ownable.onOwnerChanged(state, level, pos, null, oldOwner, ownable.getOwner());

						level.sendBlockUpdated(pos, state, state, 3);
						modifiedChunks.add(level.getChunk(pos));
						blocksModified++;
					}
				}
			}

			if (blocksModified == 0)
				throw ERROR_FILL_FAILED.create();
			else {
				int finalBlocksModified = blocksModified;

				level.getChunkSource().chunkMap.resendBiomesForChunks(new ArrayList<>(modifiedChunks)); //Queues chunks with modified blocks to be sent to the client, so reinforced block tints are updated properly
				source.sendSuccess(() -> Component.translatableWithFallback("commands.securitycraft.owner.fill.success", "Successfully set the owner of %s block(s)", finalBlocksModified), true);
				return blocksModified;
			}
		}
	}
}
