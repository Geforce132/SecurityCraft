package net.geforcemods.securitycraft.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.impl.FillCommand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ConvertCommand {
	private static final SimpleCommandExceptionType ERROR_SET_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.securitycraft.convert.set.failed"));
	private static final SimpleCommandExceptionType ERROR_FILL_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.securitycraft.convert.fill.failed"));

	private ConvertCommand() {}

	public static ArgumentBuilder<CommandSource, ?> register() {
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

	private static int set(CommandSource source, ConversionMode mode, BlockPos pos) throws CommandSyntaxException {
		ServerWorld level = source.getLevel();

		if (!mode.convert(level.getBlockState(pos), level, pos))
			throw ERROR_SET_FAILED.create();

		source.sendSuccess(new TranslationTextComponent("commands.securitycraft.convert.set.success", pos.getX(), pos.getY(), pos.getZ()), true);
		return 1;
	}

	private static int fill(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		ConversionMode mode = ctx.getArgument("mode", ConversionMode.class);
		MutableBoundingBox area = new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(ctx, "from"), BlockPosArgument.getLoadedBlockPos(ctx, "to"));
		CommandSource source = ctx.getSource();
		ServerWorld level = source.getLevel();
		int blockCount = area.getXSpan() * area.getYSpan() * area.getZSpan();

		if (blockCount > 32768)
			throw FillCommand.ERROR_AREA_TOO_LARGE.create(32768, blockCount);
		else {
			int blocksModified = 0;

			for (BlockPos pos : BlockPos.betweenClosed(area.x0, area.y0, area.z0, area.x1, area.y1, area.z1)) {
				BlockState state = level.getBlockState(pos);

				if (mode.convert(state, level, pos))
					blocksModified++;
			}

			if (blocksModified == 0)
				throw ERROR_FILL_FAILED.create();
			else {
				int finalBlocksModified = blocksModified;

				source.sendSuccess(new TranslationTextComponent("commands.securitycraft.convert.fill.success", finalBlocksModified), true);
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

		private final TriPredicate<BlockState, World, BlockPos> converter;

		ConversionMode(TriPredicate<BlockState, World, BlockPos> converter) {
			this.converter = converter;
		}

		public boolean convert(BlockState state, World level, BlockPos pos) {
			return converter.test(state, level, pos);
		}
	}

	@FunctionalInterface
	private interface TriPredicate<T, U, V> {
		public boolean test(T t, U u, V v);
	}
}
