package net.geforcemods.securitycraft.commands;

import org.apache.commons.lang3.function.TriFunction;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class ConvertCommand {
	private static final SimpleCommandExceptionType ERROR_SET_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.securitycraft.convert.set.failed"));
	private static final SimpleCommandExceptionType ERROR_FILL_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.securitycraft.convert.fill.failed"));

	private ConvertCommand() {}

	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		//@formatter:off
		return Commands.literal("convert")
                .requires(ctx -> ctx.hasPermission(2))
                .then(Commands.argument("mode", LowercasedEnumArgument.enumArgument(ConversionMode.class))
                		.then(Commands.literal("set")
                				.then(Commands.argument("pos", BlockPosArgument.blockPos())
                						.executes(ctx -> set(ctx.getSource(), ctx.getArgument("mode", ConversionMode.class), BlockPosArgument.getLoadedBlockPos(ctx, "pos")))))
                		.then(Commands.literal("fill")
                				.then(Commands.argument("from", BlockPosArgument.blockPos())
                						.then(Commands.argument("to", BlockPosArgument.blockPos())
                								.executes(ctx -> fill(ctx))))));
		//@formatter:on
	}

	private static int set(CommandSourceStack source, ConversionMode mode, BlockPos pos) throws CommandSyntaxException {
		ServerLevel level = source.getLevel();

		if (!mode.convert(level.getBlockState(pos), level, pos))
			throw ERROR_SET_FAILED.create();

		source.sendSuccess(new TranslatableComponent("commands.securitycraft.convert.set.success", pos.getX(), pos.getY(), pos.getZ()), true);
		return 1;
	}

	private static int fill(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		ConversionMode mode = ctx.getArgument("mode", ConversionMode.class);
		BoundingBox area = BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(ctx, "from"), BlockPosArgument.getLoadedBlockPos(ctx, "to"));
		CommandSourceStack source = ctx.getSource();
		ServerLevel level = source.getLevel();
		int blockCount = area.getXSpan() * area.getYSpan() * area.getZSpan();

		if (blockCount > 32768)
			throw FillCommand.ERROR_AREA_TOO_LARGE.create(32768, blockCount);
		else {
			int blocksModified = 0;

			for (BlockPos pos : BlockPos.betweenClosed(area.minX(), area.minY(), area.minZ(), area.maxX(), area.maxY(), area.maxZ())) {
				BlockState state = level.getBlockState(pos);

				if (mode.convert(state, level, pos))
					blocksModified++;
			}

			if (blocksModified == 0)
				throw ERROR_FILL_FAILED.create();
			else {
				int finalBlocksModified = blocksModified;

				source.sendSuccess(new TranslatableComponent("commands.securitycraft.convert.fill.success", finalBlocksModified), true);
				return blocksModified;
			}
		}
	}

	private enum ConversionMode {
		REINFORCE((state, level, pos) -> {
			Block block = state.getBlock();

			if (IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.containsKey(block)) {
				level.setBlockAndUpdate(pos, ((IReinforcedBlock) IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(block)).convertToReinforced(level, pos, state));
				return true;
			}

			return false;
		}),
		UNREINFORCE((state, level, pos) -> {
			Block block = state.getBlock();

			if (IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.containsKey(block)) {
				level.setBlockAndUpdate(pos, ((IReinforcedBlock) block).convertToVanilla(level, pos, state));
				return true;
			}

			return false;
		}),
		PASSCODE_PROTECT((state, level, pos) -> {
			for (IPasscodeConvertible convertible : SecurityCraftAPI.getRegisteredPasscodeConvertibles()) {
				if (convertible.isUnprotectedBlock(state))
					return convertible.protect(null, level, pos);
			}

			return false;
		}),
		REMOVE_PASSCODE_PROTECTION((state, level, pos) -> {
			for (IPasscodeConvertible convertible : SecurityCraftAPI.getRegisteredPasscodeConvertibles()) {
				if (convertible.isProtectedBlock(state))
					return convertible.unprotect(null, level, pos);
			}

			return false;
		});

		private final TriFunction<BlockState, Level, BlockPos, Boolean> converter;

		ConversionMode(TriFunction<BlockState, Level, BlockPos, Boolean> converter) {
			this.converter = converter;
		}

		public boolean convert(BlockState state, Level level, BlockPos pos) {
			return converter.apply(state, level, pos);
		}
	}
}
