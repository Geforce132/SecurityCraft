package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.util.TriPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.server.command.CommandTreeBase;

public class ConvertCommand extends CommandTreeBase {
	public ConvertCommand() {
		addSubcommand(new SetCommand());
		addSubcommand(new FillCommand());
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getName() {
		return "convert";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/securitycraft convert <set|fill>";
	}

	private static class SetCommand extends CommandBase {
		@Override
		public String getName() {
			return "set";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/securitycraft convert set <reinforce|unreinforce|passcode_protect|remove_passcode_protection> <x> <y> <z>";
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length < 4)
				throw new WrongUsageException(getUsage(sender));

			sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);

			World world = sender.getEntityWorld();
			BlockPos pos = parseBlockPos(sender, args, 1, false);
			ConversionMode mode;

			try {
				mode = Enum.valueOf(ConversionMode.class, args[0]);
			}
			catch (IllegalArgumentException e) {
				throw new WrongUsageException(getUsage(sender));
			}

			if (!world.isBlockLoaded(pos))
				throw new CommandException("commands.setblock.outOfWorld");

			if (!mode.convert(world.getBlockState(pos), world, pos))
				throw new CommandException("commands.securitycraft.convert.set.failed");

			sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
			notifyCommandListener(sender, this, "commands.securitycraft.convert.set.success", pos.getX(), pos.getY(), pos.getZ());
		}

		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
			if (args.length == 1)
				return getListOfStringsMatchingLastWord(args, "reinforce", "unreinforce", "passcode_protect", "remove_passcode_protection");
			else if (args.length >= 2 && args.length <= 4)
				return getTabCompletionCoordinate(args, 1, targetPos);
			else
				return new ArrayList<>();
		}
	}

	private static class FillCommand extends CommandBase {
		@Override
		public String getName() {
			return "fill";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/securitycraft convert fill <reinforce|unreinforce|passcode_protect|remove_passcode_protection> <x1> <y1> <z1> <x2> <y2> <z2>";
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length < 7)
				throw new WrongUsageException(getUsage(sender));

			World world = sender.getEntityWorld();
			BlockPos firstPosArgs = parseBlockPos(sender, args, 1, false);
			BlockPos secondPosArgs = parseBlockPos(sender, args, 4, false);
			BlockPos from = new BlockPos(Math.min(firstPosArgs.getX(), secondPosArgs.getX()), Math.min(firstPosArgs.getY(), secondPosArgs.getY()), Math.min(firstPosArgs.getZ(), secondPosArgs.getZ()));
			BlockPos to = new BlockPos(Math.max(firstPosArgs.getX(), secondPosArgs.getX()), Math.max(firstPosArgs.getY(), secondPosArgs.getY()), Math.max(firstPosArgs.getZ(), secondPosArgs.getZ()));
			int size = (to.getX() - from.getX() + 1) * (to.getY() - from.getY() + 1) * (to.getZ() - from.getZ() + 1);
			ConversionMode mode;

			sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);

			if (size > 32768)
				throw new CommandException("commands.fill.tooManyBlocks", size, 32768);

			if (from.getY() < 0 || to.getY() > 255)
				throw new CommandException("commands.fill.outOfWorld");

			try {
				mode = Enum.valueOf(ConversionMode.class, args[0].toUpperCase());
			}
			catch (IllegalArgumentException e) {
				throw new WrongUsageException(getUsage(sender));
			}

			for (int z = from.getZ(); z <= to.getZ(); z += 16) {
				for (int x = from.getX(); x <= to.getX(); x += 16) {
					if (!world.isBlockLoaded(new BlockPos(x, to.getY() - from.getY(), z)))
						throw new CommandException("commands.fill.outOfWorld");
				}
			}

			int blocksModified = 0;

			for (int z = from.getZ(); z <= to.getZ(); ++z) {
				for (int y = from.getY(); y <= to.getY(); ++y) {
					for (int x = from.getX(); x <= to.getX(); ++x) {
						BlockPos pos = new BlockPos(x, y, z);

						if (mode.convert(world.getBlockState(pos), world, pos))
							blocksModified++;
					}
				}
			}

			if (blocksModified == 0)
				throw new CommandException("commands.securitycraft.convert.fill.failed");
			else {
				sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, blocksModified);
				notifyCommandListener(sender, this, "commands.securitycraft.convert.fill.success", blocksModified);
			}
		}

		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
			if (args.length == 1)
				return getListOfStringsMatchingLastWord(args, "reinforce", "unreinforce", "passcode_protect", "remove_passcode_protection");
			else if (args.length >= 2 && args.length <= 4)
				return getTabCompletionCoordinate(args, 1, targetPos);
			else if (args.length >= 5 && args.length <= 7)
				return getTabCompletionCoordinate(args, 4, targetPos);
			else
				return new ArrayList<>();
		}
	}

	private enum ConversionMode {
		REINFORCE((state, level, pos) -> {
			Block block = state.getBlock();

			if (IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.containsKey(block)) {
				level.setBlockState(pos, ((IReinforcedBlock) IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(block)).convertToReinforcedState(state));
				return true;
			}

			return false;
		}),
		UNREINFORCE((state, level, pos) -> {
			Block block = state.getBlock();

			if (block instanceof IReinforcedBlock) {
				try {
					level.setBlockState(pos, ((IReinforcedBlock) block).convertToVanillaState(state));
				}
				catch (IllegalStateException e) {
					e.printStackTrace();
				}

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

		private final TriPredicate<IBlockState, World, BlockPos> converter;

		ConversionMode(TriPredicate<IBlockState, World, BlockPos> converter) {
			this.converter = converter;
		}

		public boolean convert(IBlockState state, World level, BlockPos pos) {
			return converter.test(state, level, pos);
		}
	}
}
