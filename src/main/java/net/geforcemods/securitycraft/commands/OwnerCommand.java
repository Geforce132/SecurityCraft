package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.impl.FillCommand;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class OwnerCommand {
	private static final SimpleCommandExceptionType ERROR_SET_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.securitycraft.owner.set.failed"));
	private static final SimpleCommandExceptionType ERROR_FILL_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.securitycraft.owner.fill.failed"));

	private OwnerCommand() {}

	public static ArgumentBuilder<CommandSource, ?> register() {
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

	private static int setRandomOwner(CommandSource source, BlockPos pos) throws CommandSyntaxException {
		return setOwner(source, pos, UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(10));
	}

	private static int setOwner(CommandSource source, BlockPos pos, GameProfile gameProfile) throws CommandSyntaxException {
		return setOwner(source, pos, gameProfile.getId().toString(), gameProfile.getName());
	}

	private static int setOwner(CommandSource source, BlockPos pos, String uuid, String name) throws CommandSyntaxException {
		ServerWorld level = source.getLevel();
		TileEntity te = level.getBlockEntity(pos);

		if (!(te instanceof IOwnable))
			throw ERROR_SET_FAILED.create();

		IOwnable ownable = (IOwnable) te;
		Owner previousOwner = ownable.getOwner();

		if (!previousOwner.getUUID().equals(uuid) || !previousOwner.getName().equals(name)) {
			BlockState state = te.getBlockState();
			Owner oldOwner = ownable.getOwner().copy();

			ownable.setOwner(uuid, name);
			ownable.onOwnerChanged(state, level, pos, null, oldOwner, ownable.getOwner());
			ownable.getOwner().setValidated(true);
			level.sendBlockUpdated(pos, state, state, 3);
			source.sendSuccess(new TranslationTextComponent("commands.securitycraft.owner.set.success", pos.getX(), pos.getY(), pos.getZ()), true);
			return 1;
		}
		else
			throw ERROR_SET_FAILED.create();
	}

	private static int fillRandomOwner(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		return fillOwner(ctx, UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(10));
	}

	private static int fillOwner(CommandContext<CommandSource> ctx, GameProfile gameProfile) throws CommandSyntaxException {
		return fillOwner(ctx, gameProfile.getId().toString(), gameProfile.getName());
	}

	private static int fillOwner(CommandContext<CommandSource> ctx, String uuid, String name) throws CommandSyntaxException {
		MutableBoundingBox area = new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(ctx, "from"), BlockPosArgument.getLoadedBlockPos(ctx, "to"));
		CommandSource source = ctx.getSource();
		ServerWorld level = source.getLevel();
		int blockCount = area.getXSpan() * area.getYSpan() * area.getZSpan();

		if (blockCount > 32768)
			throw FillCommand.ERROR_AREA_TOO_LARGE.create(32768, blockCount);
		else {
			List<OwnerChange> modifiedBlocks = new ArrayList<>();

			for (BlockPos pos : BlockPos.betweenClosed(area.x0, area.y0, area.z0, area.x1, area.y1, area.z1)) {
				TileEntity te = level.getBlockEntity(pos);

				if (te instanceof IOwnable) {
					IOwnable ownable = (IOwnable) te;
					Owner previousOwner = ownable.getOwner();

					if (!previousOwner.getUUID().equals(uuid) || !previousOwner.getName().equals(name)) {
						Owner oldOwner = ownable.getOwner().copy();

						ownable.setOwner(uuid, name);
						modifiedBlocks.add(new OwnerChange((TileEntity) ownable, oldOwner));
					}
				}
			}

			int blocksModified = modifiedBlocks.size();

			if (blocksModified == 0)
				throw ERROR_FILL_FAILED.create();
			else {
				for (OwnerChange ownerChange : modifiedBlocks) {
					TileEntity be = ownerChange.be;
					BlockPos pos = be.getBlockPos();
					BlockState state = be.getBlockState();
					IOwnable ownable = (IOwnable) be;

					ownable.onOwnerChanged(state, level, pos, null, ownerChange.oldOwner, ownable.getOwner());
					ownable.getOwner().setValidated(true);
					level.sendBlockUpdated(pos, state, state, 3);
				}

				source.sendSuccess(new TranslationTextComponent("commands.securitycraft.owner.fill.success", blocksModified), true);
				return blocksModified;
			}
		}
	}

	private static class OwnerChange {
		private final TileEntity be;
		private final Owner oldOwner;

		private OwnerChange(TileEntity be, Owner oldOwner) {
			this.be = be;
			this.oldOwner = oldOwner;
		}
	}
}
