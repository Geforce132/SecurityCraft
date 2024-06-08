package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import com.mojang.authlib.GameProfile;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.server.command.CommandTreeBase;

public class OwnerCommand extends CommandTreeBase {
	public OwnerCommand() {
		addSubcommand(new SetCommand());
		addSubcommand(new FillCommand());
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getName() {
		return "owner";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/securitycraft owner <set|fill>";
	}

	private static class SetCommand extends CommandBase {
		@Override
		public String getName() {
			return "set";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			//securitycraft owner set <x> <y> <z> <player|random|reset> [player]
			return "commands.securitycraft.owner.set.usage";
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length < 4 || args[3].equals("player") && args.length < 5)
				throw new WrongUsageException(getUsage(sender));

			World world = sender.getEntityWorld();
			BlockPos pos = parseBlockPos(sender, args, 0, false);

			sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);

			if (!world.isBlockLoaded(pos))
				throw new CommandException("commands.setblock.outOfWorld");

			switch (args[3]) {
				case "player":
					setOwner(sender, world, pos, server.getPlayerProfileCache().getGameProfileForUsername(args[4]));
					break;
				case "random":
					setRandomOwner(sender, world, pos);
					break;
				case "reset":
					setOwner(sender, world, pos, "ownerUUID", "owner");
					break;
				default:
					throw new WrongUsageException(getUsage(sender));
			}
		}

		private void setRandomOwner(ICommandSender sender, World world, BlockPos pos) throws CommandException {
			setOwner(sender, world, pos, UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(10));
		}

		private void setOwner(ICommandSender sender, World world, BlockPos pos, GameProfile gameProfile) throws CommandException {
			setOwner(sender, world, pos, gameProfile.getId().toString(), gameProfile.getName());
		}

		private void setOwner(ICommandSender sender, World world, BlockPos pos, String uuid, String name) throws CommandException {
			TileEntity te = world.getTileEntity(pos);

			if (!(te instanceof IOwnable))
				throw new CommandException("commands.securitycraft.owner.set.failed");

			IOwnable ownable = (IOwnable) te;
			Owner previousOwner = ownable.getOwner();

			if (!previousOwner.getUUID().equals(uuid) || !previousOwner.getName().equals(name)) {
				IBlockState state = world.getBlockState(pos);
				Owner oldOwner = ownable.getOwner().copy();

				ownable.setOwner(uuid, name);
				ownable.onOwnerChanged(state, world, pos, null, oldOwner, ownable.getOwner());
				ownable.getOwner().setValidated(true);
				world.notifyBlockUpdate(pos, state, state, 3);
				sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
				notifyCommandListener(sender, this, "commands.securitycraft.owner.set.success", pos.getX(), pos.getY(), pos.getZ());
			}
			else
				throw new CommandException("commands.securitycraft.owner.set.failed");
		}

		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
			if (args.length >= 1 && args.length <= 3)
				return getTabCompletionCoordinate(args, 0, targetPos);
			else if (args.length == 4)
				return getListOfStringsMatchingLastWord(args, "player", "random", "reset");
			else if (isUsernameIndex(args, 4))
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			else
				return new ArrayList<>();
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index) {
			return args.length == 5 && args[3].equals("player") && index == 4;
		}
	}

	private static class FillCommand extends CommandBase {
		@Override
		public String getName() {
			return "fill";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			//securitycraft owner fill <x1> <y1> <z1> <x2> <y2> <z2> <random|reset|player> [player]
			return "commands.securitycraft.owner.fill.usage";
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length < 7 || args[6].equals("player") && args.length < 8)
				throw new WrongUsageException(getUsage(sender));

			World world = sender.getEntityWorld();
			BlockPos firstPosArgs = parseBlockPos(sender, args, 0, false);
			BlockPos secondPosArgs = parseBlockPos(sender, args, 3, false);
			BlockPos from = new BlockPos(Math.min(firstPosArgs.getX(), secondPosArgs.getX()), Math.min(firstPosArgs.getY(), secondPosArgs.getY()), Math.min(firstPosArgs.getZ(), secondPosArgs.getZ()));
			BlockPos to = new BlockPos(Math.max(firstPosArgs.getX(), secondPosArgs.getX()), Math.max(firstPosArgs.getY(), secondPosArgs.getY()), Math.max(firstPosArgs.getZ(), secondPosArgs.getZ()));
			int size = (to.getX() - from.getX() + 1) * (to.getY() - from.getY() + 1) * (to.getZ() - from.getZ() + 1);

			sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);

			if (size > 32768)
				throw new CommandException("commands.fill.tooManyBlocks", size, 32768);

			if (from.getY() < 0 || to.getY() > 255)
				throw new CommandException("commands.fill.outOfWorld");

			for (int z = from.getZ(); z <= to.getZ(); z += 16) {
				for (int x = from.getX(); x <= to.getX(); x += 16) {
					if (!world.isBlockLoaded(new BlockPos(x, to.getY() - from.getY(), z)))
						throw new CommandException("commands.fill.outOfWorld");
				}
			}

			switch (args[6]) {
				case "player":
					fillOwner(sender, world, from, to, server.getPlayerProfileCache().getGameProfileForUsername(args[7]));
					break;
				case "random":
					fillRandomOwner(sender, world, from, to);
					break;
				case "reset":
					fillOwner(sender, world, from, to, "ownerUUID", "owner");
					break;
				default:
					throw new WrongUsageException(getUsage(sender));
			}
		}

		private void fillRandomOwner(ICommandSender sender, World world, BlockPos from, BlockPos to) throws CommandException {
			fillOwner(sender, world, from, to, UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(10));
		}

		private void fillOwner(ICommandSender sender, World world, BlockPos from, BlockPos to, GameProfile gameProfile) throws CommandException {
			fillOwner(sender, world, from, to, gameProfile.getId().toString(), gameProfile.getName());
		}

		private void fillOwner(ICommandSender sender, World world, BlockPos from, BlockPos to, String uuid, String name) throws CommandException {
			List<OwnerChange> modifiedBlocks = new ArrayList<>();

			for (int z = from.getZ(); z <= to.getZ(); ++z) {
				for (int y = from.getY(); y <= to.getY(); ++y) {
					for (int x = from.getX(); x <= to.getX(); ++x) {
						BlockPos pos = new BlockPos(x, y, z);
						TileEntity te = world.getTileEntity(pos);

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
				}
			}

			int blocksModified = modifiedBlocks.size();

			if (blocksModified == 0)
				throw new CommandException("commands.securitycraft.owner.fill.failed");
			else {
				for (OwnerChange ownerChange : modifiedBlocks) {
					TileEntity be = ownerChange.be;
					BlockPos pos = be.getPos();
					IBlockState state = world.getBlockState(pos);
					IOwnable ownable = (IOwnable) be;

					((IOwnable) be).onOwnerChanged(state, world, pos, null, ownerChange.oldOwner, ownable.getOwner());
					((IOwnable) be).getOwner().setValidated(true);
					world.notifyBlockUpdate(pos, state, state, 3);
				}

				sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, blocksModified);
				notifyCommandListener(sender, this, "commands.securitycraft.owner.fill.success", blocksModified);
			}
		}

		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
			if (args.length >= 1 && args.length <= 3)
				return getTabCompletionCoordinate(args, 0, targetPos);
			else if (args.length >= 4 && args.length <= 6)
				return getTabCompletionCoordinate(args, 3, targetPos);
			else if (args.length == 7)
				return getListOfStringsMatchingLastWord(args, "player", "random", "reset");
			else if (isUsernameIndex(args, 7))
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			else
				return new ArrayList<>();
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index) {
			return args.length == 8 && args[6].equals("player") && index == 7;
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
