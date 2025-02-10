package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.KeycardLockBlockEntity;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.items.UniversalKeyChangerItem;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class KeycardLockBlock extends OwnableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = KeycardReaderBlock.POWERED;

	protected KeycardLockBlock(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
		setHardness(2.0F);
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		return KeycardReaderBlock.<KeycardLockBlockEntity>onBlockActivated(state, world, pos, player, hand, (stack, be) -> {
			if (!be.isSetUp()) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(KeycardLockBlock.this), Utils.localize("messages.securitycraft:keycard_lock.not_set_up"), TextFormatting.RED);
				return;
			}

			if (stack.getItem() instanceof KeycardItem) {
				boolean hasTag = stack.hasTagCompound();

				if (!hasTag || !stack.getTagCompound().getBoolean("linked"))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(KeycardLockBlock.this), Utils.localize("messages.securitycraft:keycard_lock.unlinked_keycard"), TextFormatting.RED);
				else if (hasTag) {
					NBTTagCompound tag = stack.getTagCompound();
					Owner keycardOwner = new Owner(tag.getString("ownerName"), tag.getString("ownerUUID"));

					if (!TeamUtils.areOnSameTeam(be.getOwner(), keycardOwner) || !be.getOwner().getUUID().equals(keycardOwner.getUUID()))
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(KeycardLockBlock.this), Utils.localize("messages.securitycraft:keycard_lock.different_owner"), TextFormatting.RED);
				}

				return;
			}

			if (stack.getItem() instanceof UniversalKeyChangerItem) {
				if (be.isOwnedBy(player)) {
					be.reset();
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(KeycardLockBlock.this), Utils.localize("messages.securitycraft:keycard_lock.reset"), TextFormatting.GREEN);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(KeycardLockBlock.this), Utils.localize("messages.securitycraft:notOwned", be.getOwner().getName()), TextFormatting.RED);
			}
		});
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (state.getValue(POWERED)) {
			world.setBlockState(pos, state.withProperty(POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, this, getConnectedDirection(state).getOpposite());
		}
	}

	public void activate(IBlockState state, World world, BlockPos pos, int signalLength) {
		world.setBlockState(pos, state.cycleProperty(POWERED));
		BlockUtils.updateIndirectNeighbors(world, pos, this, getConnectedDirection(state).getOpposite());

		if (signalLength > 0)
			world.scheduleUpdate(pos, this, signalLength);
	}

	protected abstract EnumFacing getConnectedDirection(IBlockState state);

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (!ConfigHandler.vanillaToolBlockBreaking && te instanceof IModuleInventory)
			((IModuleInventory) te).dropAllModules();

		if (state.getValue(POWERED)) {
			world.notifyNeighborsOfStateChange(pos, this, false);
			BlockUtils.updateIndirectNeighbors(world, pos, this);
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) && getConnectedDirection(state) == side ? 15 : 0;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return world.getBlockState(pos).isSideSolid(world, pos, side);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		EnumFacing connectedDirection = getConnectedDirection(state);

		if (!canPlaceBlockOnSide(world, pos.offset(connectedDirection.getOpposite()), connectedDirection)) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return Arrays.asList(new ItemStack(SCContent.keycardLock));
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(SCContent.keycardLock);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new KeycardLockBlockEntity();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}
}
