package net.geforcemods.securitycraft.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public class KeypadChestBlock extends DisguisableBlock {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	protected static final AxisAlignedBB NORTH_CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0D, 0.9375D, 0.875D, 0.9375D);
	protected static final AxisAlignedBB SOUTH_CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 1.0D);
	protected static final AxisAlignedBB WEST_CHEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);
	protected static final AxisAlignedBB EAST_CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 1.0D, 0.875D, 0.9375D);
	protected static final AxisAlignedBB NOT_CONNECTED_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);

	public KeypadChestBlock() {
		super(Material.IRON);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		setSoundType(SoundType.METAL);
		destroyTimeForOwner = 5.0F;
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockState actualState = getDisguisedBlockState(world.getTileEntity(pos));

		if (actualState != null && actualState.getBlock() != this)
			return actualState.getBoundingBox(world, pos);
		else {
			if (world.getBlockState(pos.north()).getBlock() == this)
				return NORTH_CHEST_AABB;
			else if (world.getBlockState(pos.south()).getBlock() == this)
				return SOUTH_CHEST_AABB;
			else if (world.getBlockState(pos.west()).getBlock() == this)
				return WEST_CHEST_AABB;
			else
				return world.getBlockState(pos.east()).getBlock() == this ? EAST_CHEST_AABB : NOT_CONNECTED_AABB;
		}
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		checkForSurroundingChests(world, pos, state);

		for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
			BlockPos offsetPos = pos.offset(facing);
			IBlockState offsetState = world.getBlockState(offsetPos);

			if (offsetState.getBlock() == this)
				checkForSurroundingChests(world, offsetPos, offsetState);
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			KeypadChestBlockEntity be = (KeypadChestBlockEntity) world.getTileEntity(pos);

			if (be.verifyPasscodeSet(world, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

					activate(world, pos, player);
				}
				else if (player.getHeldItem(hand).getItem() != SCContent.codebreaker)
					be.openPasscodeGUI(world, pos, player);
			}
		}

		return true;
	}

	public void activate(World world, BlockPos pos, EntityPlayer player) {
		if (!isBlocked(world, pos))
			player.displayGUIChest(getLockableContainer(world, pos));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		EnumFacing facing = EnumFacing.byHorizontalIndex(MathHelper.floor(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3).getOpposite();
		state = state.withProperty(FACING, facing);
		BlockPos northPos = pos.north();
		BlockPos southPos = pos.south();
		BlockPos westPos = pos.west();
		BlockPos eastPos = pos.east();
		boolean isNorthPPC = this == world.getBlockState(northPos).getBlock();
		boolean isSouthPPC = this == world.getBlockState(southPos).getBlock();
		boolean isWestPPC = this == world.getBlockState(westPos).getBlock();
		boolean isEastPPC = this == world.getBlockState(eastPos).getBlock();
		BlockPos otherChestPos = null;

		if (!isNorthPPC && !isSouthPPC && !isWestPPC && !isEastPPC)
			world.setBlockState(pos, state, 3);
		else if (facing.getAxis() != EnumFacing.Axis.X || !isNorthPPC && !isSouthPPC) {
			if (facing.getAxis() == EnumFacing.Axis.Z && (isWestPPC || isEastPPC)) {
				if (isWestPPC) {
					world.setBlockState(westPos, state, 3);
					otherChestPos = westPos;
				}
				else {
					world.setBlockState(eastPos, state, 3);
					otherChestPos = eastPos;
				}

				world.setBlockState(pos, state, 3);
			}
		}
		else {
			if (isNorthPPC) {
				world.setBlockState(northPos, state, 3);
				otherChestPos = northPos;
			}
			else {
				world.setBlockState(southPos, state, 3);
				otherChestPos = southPos;
			}

			world.setBlockState(pos, state, 3);
		}

		KeypadChestBlockEntity thisBe = (KeypadChestBlockEntity) world.getTileEntity(pos);

		if (stack.hasDisplayName())
			thisBe.setCustomName(stack.getDisplayName());

		if (entity instanceof EntityPlayer) {
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer) entity));

			if (otherChestPos != null) {
				TileEntity otherTe = world.getTileEntity(otherChestPos);

				if (otherTe instanceof KeypadChestBlockEntity && thisBe.getOwner().owns((KeypadChestBlockEntity) otherTe)) {
					KeypadChestBlockEntity be = (KeypadChestBlockEntity) otherTe;

					for (ModuleType type : be.getInsertedModules()) {
						thisBe.insertModule(be.getModule(type), false);
					}

					thisBe.setSendsAllowlistMessage(be.sendsAllowlistMessage());
					thisBe.setSendsDenylistMessage(be.sendsDenylistMessage());

					if (be.getSaltKey() != null)
						thisBe.setSaltKey(SaltData.copySaltToNewKey(be.getSaltKey()));

					thisBe.setPasscode(be.getPasscode());
				}
			}
		}
	}

	public IBlockState checkForSurroundingChests(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote) {
			IBlockState northState = world.getBlockState(pos.north());
			IBlockState southState = world.getBlockState(pos.south());
			IBlockState westState = world.getBlockState(pos.west());
			IBlockState eastState = world.getBlockState(pos.east());
			EnumFacing facing = state.getValue(FACING);

			if (northState.getBlock() != this && southState.getBlock() != this) {
				boolean isNorthFullBlock = northState.isFullBlock();
				boolean isSouthFullBlock = southState.isFullBlock();

				if (westState.getBlock() == this || eastState.getBlock() == this) {
					BlockPos otherPos = westState.getBlock() == this ? pos.west() : pos.east();
					IBlockState otherNorthState = world.getBlockState(otherPos.north());
					IBlockState otherSouthState = world.getBlockState(otherPos.south());
					EnumFacing otherFacing;

					facing = EnumFacing.SOUTH;

					if (westState.getBlock() == this)
						otherFacing = westState.getValue(FACING);
					else
						otherFacing = eastState.getValue(FACING);

					if (otherFacing == EnumFacing.NORTH)
						facing = EnumFacing.NORTH;

					if ((isNorthFullBlock || otherNorthState.isFullBlock()) && !isSouthFullBlock && !otherSouthState.isFullBlock())
						facing = EnumFacing.SOUTH;

					if ((isSouthFullBlock || otherSouthState.isFullBlock()) && !isNorthFullBlock && !otherNorthState.isFullBlock())
						facing = EnumFacing.NORTH;
				}
			}
			else {
				BlockPos otherPos = northState.getBlock() == this ? pos.north() : pos.south();
				IBlockState otherWestState = world.getBlockState(otherPos.west());
				IBlockState otherEastState = world.getBlockState(otherPos.east());
				EnumFacing otherFacing;

				facing = EnumFacing.EAST;

				if (northState.getBlock() == this)
					otherFacing = northState.getValue(FACING);
				else
					otherFacing = southState.getValue(FACING);

				if (otherFacing == EnumFacing.WEST)
					facing = EnumFacing.WEST;

				if ((westState.isFullBlock() || otherWestState.isFullBlock()) && !eastState.isFullBlock() && !otherEastState.isFullBlock())
					facing = EnumFacing.EAST;

				if ((eastState.isFullBlock() || otherEastState.isFullBlock()) && !westState.isFullBlock() && !otherWestState.isFullBlock())
					facing = EnumFacing.WEST;
			}

			state = state.withProperty(FACING, facing);
			world.setBlockState(pos, state, 3);
		}

		return state;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		int surroundingChests = 0;
		BlockPos westPos = pos.west();
		BlockPos eastPos = pos.east();
		BlockPos northPos = pos.north();
		BlockPos southPos = pos.south();

		if (world.getBlockState(westPos).getBlock() == this) {
			if (isDoubleChest(world, westPos))
				return false;

			surroundingChests++;
		}

		if (world.getBlockState(eastPos).getBlock() == this && (isDoubleChest(world, eastPos) || ++surroundingChests > 1))
			return false;

		if (world.getBlockState(northPos).getBlock() == this && (isDoubleChest(world, northPos) || ++surroundingChests > 1))
			return false;

		if (world.getBlockState(southPos).getBlock() == this && (isDoubleChest(world, southPos) || ++surroundingChests > 1))
			return false;

		return surroundingChests <= 1;
	}

	public boolean isDoubleChest(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() == this) {
			for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
				if (world.getBlockState(pos.offset(facing)).getBlock() == this)
					return true;
			}
		}

		return false;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(world, pos, neighbor);

		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof KeypadChestBlockEntity)
			((KeypadChestBlockEntity) tileEntity).updateContainingBlockInfo();
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, world, pos, blockIn, fromPos);
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof KeypadChestBlockEntity)
			te.updateContainingBlockInfo();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof IInventory) {
			InventoryHelper.dropInventoryItems(world, pos, (IInventory) te);
			world.updateComparatorOutputLevel(pos, this);
		}

		if (te instanceof IPasscodeProtected)
			SaltData.removeSalt(((IPasscodeProtected) te).getSaltKey());

		super.breakBlock(world, pos, state);
	}

	public ILockableContainer getLockableContainer(World worldIn, BlockPos pos) {
		return getContainer(worldIn, pos, false);
	}

	public ILockableContainer getContainer(World world, BlockPos pos, boolean allowBlocking) {
		TileEntity te = world.getTileEntity(pos);

		if (!(te instanceof KeypadChestBlockEntity))
			return null;
		else {
			ILockableContainer container = (KeypadChestBlockEntity) te;

			if (!allowBlocking && isBlocked(world, pos))
				return null;
			else {
				for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
					BlockPos offsetPos = pos.offset(facing);
					Block offsetBlock = world.getBlockState(offsetPos).getBlock();

					if (offsetBlock == this) {
						if (!allowBlocking && isBlocked(world, offsetPos)) // Forge: fix MC-99321
							return null;

						TileEntity otherTE = world.getTileEntity(offsetPos);

						if (otherTE instanceof KeypadChestBlockEntity) {
							if (facing != EnumFacing.WEST && facing != EnumFacing.NORTH)
								container = new InventoryLargeChest("gui.securitycraft:keypadChestDouble", container, (TileEntityChest) otherTE);
							else
								container = new InventoryLargeChest("gui.securitycraft:keypadChestDouble", (TileEntityChest) otherTE, container);
						}
					}
				}

				return container;
			}
		}
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		if (!state.canProvidePower())
			return 0;
		else {
			TileEntity te = world.getTileEntity(pos);
			int numPlayersUsing = 0;

			if (te instanceof KeypadChestBlockEntity && ((KeypadChestBlockEntity) te).isModuleEnabled(ModuleType.REDSTONE))
				numPlayersUsing = ((KeypadChestBlockEntity) te).numPlayersUsing;

			return MathHelper.clamp(numPlayersUsing, 0, 15);
		}
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.UP ? state.getWeakPower(world, pos, side) : 0;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new KeypadChestBlockEntity();
	}

	public static boolean isBlocked(World world, BlockPos pos) {
		return isBelowSolidBlock(world, pos) || isOcelotSittingOnChest(world, pos);
	}

	private static boolean isBelowSolidBlock(World world, BlockPos pos) {
		return world.getBlockState(pos.up()).doesSideBlockChestOpening(world, pos.up(), EnumFacing.DOWN);
	}

	private static boolean isOcelotSittingOnChest(World world, BlockPos pos) {
		for (EntityOcelot ocelot : world.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1))) {
			if (ocelot.isSitting())
				return true;
		}

		return false;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		return Container.calcRedstoneFromInventory(this.getLockableContainer(worldIn, pos));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.byIndex(meta);

		if (facing.getAxis() == EnumFacing.Axis.Y)
			facing = EnumFacing.NORTH;

		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		return !isDoubleChest(world, pos) && super.rotateBlock(world, pos, axis);
	}

	public static class Convertible implements Function<Object, IPasscodeConvertible>, IPasscodeConvertible {
		@Override
		public IPasscodeConvertible apply(Object o) {
			return this;
		}

		@Override
		public boolean isUnprotectedBlock(IBlockState state) {
			List<ItemStack> chests = new ArrayList<>();

			chests.addAll(OreDictionary.getOres("chestWood"));
			chests.addAll(OreDictionary.getOres("chestTrapped"));
			return chests.stream().anyMatch(stack -> stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == state.getBlock());
		}

		@Override
		public boolean isProtectedBlock(IBlockState state) {
			return state.getBlock() == SCContent.keypadChest;
		}

		@Override
		public boolean protect(EntityPlayer player, World level, BlockPos pos) {
			convert(player, level, pos, true);
			return true;
		}

		@Override
		public boolean unprotect(EntityPlayer player, World level, BlockPos pos) {
			convert(player, level, pos, false);
			return true;
		}

		public void convert(EntityPlayer player, World world, BlockPos pos, boolean protect) {
			IBlockState oldChestState = world.getBlockState(pos);
			EnumFacing facing = oldChestState.getValue(FACING);
			EnumFacing doubleFacing = getDoubleChestFacing(oldChestState, world, pos);
			TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);

			if (!protect)
				((IModuleInventory) chest).dropAllModules();

			convertSingleChest(chest, player, world, pos, oldChestState, facing, protect);

			if (doubleFacing != EnumFacing.UP) {
				pos = pos.offset(doubleFacing);
				oldChestState = world.getBlockState(pos);
				convertSingleChest((TileEntityChest) world.getTileEntity(pos), player, world, pos, oldChestState, oldChestState.getValue(FACING), protect);
			}
		}

		private void convertSingleChest(TileEntityChest chest, EntityPlayer player, World world, BlockPos pos, IBlockState oldChestState, EnumFacing facing, boolean protect) {
			NBTTagCompound tag;
			TileEntity newTe;
			Block convertedBlock;

			if (protect)
				convertedBlock = SCContent.keypadChest;
			else {
				convertedBlock = ForgeRegistries.BLOCKS.getValue(((KeypadChestBlockEntity) chest).getPreviousChest());

				if (convertedBlock == Blocks.AIR)
					convertedBlock = Blocks.CHEST;
			}

			chest.fillWithLoot(player); //generate loot (if any), so items don't spill out when converting and no additional loot table is generated
			tag = chest.writeToNBT(new NBTTagCompound());
			chest.clear();
			world.setBlockState(pos, convertedBlock.getDefaultState().withProperty(FACING, facing));
			newTe = world.getTileEntity(pos);
			newTe.readFromNBT(tag);

			if (protect) {
				if (player != null)
					((IOwnable) newTe).setOwner(player.getUniqueID().toString(), player.getName());

				((KeypadChestBlockEntity) newTe).setPreviousChest(oldChestState.getBlock());
			}
		}

		private EnumFacing getDoubleChestFacing(IBlockState oldChestState, World world, BlockPos pos) {
			for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
				if (world.getBlockState(pos.offset(facing)).getBlock() == oldChestState.getBlock())
					return facing;
			}

			return EnumFacing.UP;
		}
	}
}
