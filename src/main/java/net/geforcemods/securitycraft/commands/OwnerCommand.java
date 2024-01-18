package net.geforcemods.securitycraft.commands;

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
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class OwnerCommand {
	private static final SimpleCommandExceptionType ERROR_SET_FAILED = new SimpleCommandExceptionType(Component.translatableWithFallback("commands.securitycraft.owner.set.failed", "There is no ownable block at the given position or it is already owned by the given player"));
	private static final SimpleCommandExceptionType ERROR_FILL_FAILED = new SimpleCommandExceptionType(Component.translatableWithFallback("commands.securitycraft.owner.fill.failed", "There are no ownable blocks in the given area or they are already owned by the given player"));

	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		//@formatter:off
		return Commands.literal("owner")
                .requires(ctx -> ctx.hasPermission(2))
                .then(Commands.literal("set")
                		.then(Commands.argument("pos", BlockPosArgument.blockPos())
                				.then(Commands.literal("reset")
                						.executes(ctx -> setOwner(ctx.getSource(), BlockPosArgument.getLoadedBlockPos(ctx, "pos"), "ownerUUID", "owner")))
                				.then(Commands.literal("random")
                						.executes(ctx -> setRandomOwner(ctx.getSource(), BlockPosArgument.getLoadedBlockPos(ctx, "pos"))))
                				.then(Commands.literal("player")
                						.then(Commands.argument("owner", SingleGameProfileArgument.singleGameProfile())
                								.executes(ctx -> setOwner(ctx.getSource(), BlockPosArgument.getLoadedBlockPos(ctx, "pos"), SingleGameProfileArgument.getGameProfile(ctx, "owner")))))))
                .then(Commands.literal("fill")
                		.then(Commands.argument("from", BlockPosArgument.blockPos())
                				.then(Commands.argument("to", BlockPosArgument.blockPos())
                						.then(Commands.literal("reset")
                        						.executes(ctx -> fillOwner(ctx, "ownerUUID", "owner")))
                        				.then(Commands.literal("random")
                        						.executes(ctx -> fillRandomOwner(ctx)))
                        				.then(Commands.literal("player")
                        						.then(Commands.argument("owner", SingleGameProfileArgument.singleGameProfile())
                        								.executes(ctx -> fillOwner(ctx, SingleGameProfileArgument.getGameProfile(ctx, "owner"))))))));
		//@formatter:on
	}

	private static int setRandomOwner(CommandSourceStack source, BlockPos pos) throws CommandSyntaxException {
		return setOwner(source, pos, UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(10));
	}

	private static int setOwner(CommandSourceStack source, BlockPos pos, GameProfile gameProfile) throws CommandSyntaxException {
		return setOwner(source, pos, gameProfile.getId().toString(), gameProfile.getName());
	}

	private static int setOwner(CommandSourceStack source, BlockPos pos, String uuid, String name) throws CommandSyntaxException {
		ServerLevel level = source.getLevel();

		if (!(level.getBlockEntity(pos) instanceof IOwnable ownable))
			throw ERROR_SET_FAILED.create();

		Owner previousOwner = ownable.getOwner();

		if (!previousOwner.getUUID().equals(uuid) || !previousOwner.getName().equals(name)) {
			ownable.setOwner(uuid, name);
			source.sendSuccess(() -> Component.translatableWithFallback("commands.securitycraft.owner.set.success", "Set the owner at %s, %s, %s", pos.getX(), pos.getY(), pos.getZ()), true);
			return 1;
		}
		else
			throw ERROR_SET_FAILED.create();
	}

	private static int fillRandomOwner(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		return fillOwner(ctx, UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(10));
	}

	private static int fillOwner(CommandContext<CommandSourceStack> ctx, GameProfile gameProfile) throws CommandSyntaxException {
		return fillOwner(ctx, gameProfile.getId().toString(), gameProfile.getName());
	}

	private static int fillOwner(CommandContext<CommandSourceStack> ctx, String uuid, String name) throws CommandSyntaxException {
		BoundingBox area = BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(ctx, "from"), BlockPosArgument.getLoadedBlockPos(ctx, "to"));
		CommandSourceStack source = ctx.getSource();
		ServerLevel level = source.getLevel();
		int blockCount = area.getXSpan() * area.getYSpan() * area.getZSpan();
		int commandModificationBlockLimit = level.getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);

		if (blockCount > commandModificationBlockLimit)
			throw FillCommand.ERROR_AREA_TOO_LARGE.create(commandModificationBlockLimit, blockCount);
		else {
			int blocksModified = 0;

			for (BlockPos pos : BlockPos.betweenClosed(area.minX(), area.minY(), area.minZ(), area.maxX(), area.maxY(), area.maxZ())) {
				if (level.getBlockEntity(pos) instanceof IOwnable ownable) {
					Owner previousOwner = ownable.getOwner();

					if (!previousOwner.getUUID().equals(uuid) || !previousOwner.getName().equals(name)) {
						ownable.setOwner(uuid, name);
						blocksModified++;
					}
				}
			}

			if (blocksModified == 0)
				throw ERROR_FILL_FAILED.create();
			else {
				int finalBlocksModified = blocksModified;

				source.sendSuccess(() -> Component.translatableWithFallback("commands.securitycraft.owner.fill.success", "Successfully set the owner of %s block(s)", finalBlocksModified), true);
				return blocksModified;
			}
		}
	}
}
