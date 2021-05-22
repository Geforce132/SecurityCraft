package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityAllowlistOnly;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockReinforcedPressurePlate extends BlockPressurePlate implements IReinforcedBlock
{
	private final Block vanillaBlock;

	public BlockReinforcedPressurePlate(Material material, Sensitivity sensitivity, SoundType soundType, Block vanillaBlock)
	{
		super(material, sensitivity);

		setSoundType(soundType);
		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		int redstoneStrength = getRedstoneStrength(state);

		if(!world.isRemote && redstoneStrength == 0 && entity instanceof EntityPlayer)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if(tileEntity instanceof TileEntityAllowlistOnly)
			{
				if(isAllowedToPress(world, pos, (TileEntityAllowlistOnly)tileEntity, (EntityPlayer)entity))
					updateState(world, pos, state, redstoneStrength);
			}
		}
	}

	@Override
	protected int computeRedstoneStrength(World world, BlockPos pos)
	{
		AxisAlignedBB aabb = PRESSURE_AABB.offset(pos);
		List<? extends Entity> list;

		list = world.getEntitiesWithinAABBExcludingEntity(null, aabb);

		if(!list.isEmpty())
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if(tileEntity instanceof TileEntityAllowlistOnly)
			{
				for(Entity entity : list)
				{
					if(entity instanceof EntityPlayer && isAllowedToPress(world, pos, (TileEntityAllowlistOnly)tileEntity, (EntityPlayer)entity))
						return 15;
				}
			}
		}

		return 0;
	}

	public boolean isAllowedToPress(World world, BlockPos pos, TileEntityAllowlistOnly te, EntityPlayer entity)
	{
		return te.getOwner().isOwner(entity) || ModuleUtils.isAllowed(te, entity);
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(vanillaBlock);
	}

	@Override
	public int getAmount()
	{
		return 1;
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityAllowlistOnly();
	}
}
