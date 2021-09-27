package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityKeyPanel;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockKeyPanel extends BlockOwnable
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockKeyPanel(Material material)
	{
		super(material);

		setSoundType(SoundType.METAL);
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return NULL_AABB;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(state.getValue(POWERED))
			return false;
		else
		{
			TileEntityKeyPanel te = (TileEntityKeyPanel)world.getTileEntity(pos);

			if(ModuleUtils.isDenied(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return true;
			}

			if(ModuleUtils.isAllowed(te, player)){
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				activate(state, world, pos, te.getSignalLength());
				return true;
			}

			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker, hand))
				te.openPasswordGUI(player);
		}

		return true;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
	{
		world.setBlockState(pos, state.withProperty(POWERED, false));
		updateNeighbours(state, world, pos);
	}

	public void activate(IBlockState state, World world, BlockPos pos, int signalLength)
	{
		world.setBlockState(pos, state.withProperty(POWERED, true));
		updateNeighbours(state, world, pos);
		world.scheduleUpdate(pos, this, signalLength);
	}

	private void updateNeighbours(IBlockState state, World world, BlockPos pos)
	{
		world.notifyNeighborsOfStateChange(pos, this, false);
		world.notifyNeighborsOfStateChange(pos.offset(getConnectedDirection(state).getOpposite()), this, false);
	}

	protected abstract EnumFacing getConnectedDirection(IBlockState state);

	@Override
	public boolean canProvidePower(IBlockState state)
	{
		return true;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return state.getValue(POWERED) && getConnectedDirection(state) == side ? 15 : 0;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		return true;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
	{
		return world.getBlockState(pos).isSideSolid(world, pos, side);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		return Arrays.asList(new ItemStack(SCContent.keyPanel));
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		return new ItemStack(SCContent.keyPanel);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityKeyPanel();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror)
	{
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}
}
