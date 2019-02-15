package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSecretSign extends BlockContainer
{
	protected static final AxisAlignedBB SIGN_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);

	public BlockSecretSign()
	{
		super(Material.WOOD);
		setSoundType(SoundType.WOOD);
	}

	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
	{
		if(world.getBlockState(pos).getBlock() instanceof BlockSecretSign)
		{
			manager.addBlockDestroyEffects(pos, Blocks.PLANKS.getDefaultState());
			return true;
		}
		else return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return SIGN_AABB;
	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos)
	{
		return NULL_AABB;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasCustomBreakingProgress(IBlockState state)
	{
		return true;
	}

	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos)
	{
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean canSpawnInBlock()
	{
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntitySecretSign();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return SCContent.secretSignItem;
	}

	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state)
	{
		return new ItemStack(SCContent.secretSignItem);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			return true;
		}
		else
		{
			if(player.getHeldItem(hand).getItem() == SCContent.adminTool)
				SCContent.adminTool.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);

			TileEntity tileentity = world.getTileEntity(pos);
			return tileentity instanceof TileEntitySecretSign ? ((TileEntitySecretSign)tileentity).executeCommand(player) : false;
		}
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		return !hasInvalidNeighbor(world, pos) && super.canPlaceBlockAt(world, pos);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}
}
