package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityWhitelistOnly;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		int redstoneStrength = getRedstoneStrength(state);

		if(!world.isRemote && redstoneStrength == 0 && entity instanceof EntityPlayer)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if(tileEntity instanceof TileEntityWhitelistOnly)
			{
				if(isAllowedToPress(world, pos, (TileEntityWhitelistOnly)tileEntity, (EntityPlayer)entity))
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

			if(tileEntity instanceof TileEntityWhitelistOnly)
			{
				for(Entity entity : list)
				{
					if(entity instanceof EntityPlayer && isAllowedToPress(world, pos, (TileEntityWhitelistOnly)tileEntity, (EntityPlayer)entity))
						return 15;
				}
			}
		}

		return 0;
	}

	public boolean isAllowedToPress(World world, BlockPos pos, TileEntityWhitelistOnly te, EntityPlayer entity)
	{
		return te.getOwner().isOwner(entity) || ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.WHITELIST).contains(entity.getName().toLowerCase());
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
		return new TileEntityWhitelistOnly();
	}
}
