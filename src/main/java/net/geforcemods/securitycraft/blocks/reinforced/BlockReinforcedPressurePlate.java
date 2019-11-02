package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedPressurePlate;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockReinforcedPressurePlate extends BlockPressurePlate implements IReinforcedBlock
{
	public BlockReinforcedPressurePlate()
	{
		super(Sensitivity.MOBS, Block.Properties.create(Material.ROCK).doesNotBlockMovement().hardnessAndResistance(-1.0F, 6000000.0F));
	}

	@Override
	public void onEntityCollision(IBlockState state, World world, BlockPos pos, Entity entity)
	{
		int redstoneStrength = getRedstoneStrength(state);

		if(!world.isRemote && redstoneStrength == 0 && entity instanceof EntityPlayer)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if(tileEntity instanceof TileEntityReinforcedPressurePlate)
			{
				if(isAllowedToPress(world, pos, (TileEntityReinforcedPressurePlate)tileEntity, (EntityPlayer)entity))
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

			if(tileEntity instanceof TileEntityReinforcedPressurePlate)
			{
				for(Entity entity : list)
				{
					if(entity instanceof EntityPlayer && isAllowedToPress(world, pos, (TileEntityReinforcedPressurePlate)tileEntity, (EntityPlayer)entity))
						return 15;
				}
			}
		}

		return 0;
	}

	public boolean isAllowedToPress(World world, BlockPos pos, TileEntityReinforcedPressurePlate te, EntityPlayer entity)
	{
		return te.getOwner().isOwner(entity) || ModuleUtils.getPlayersFromModule(world, pos, EnumCustomModules.WHITELIST).contains(entity.getName().getUnformattedComponentText().toLowerCase());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public Block getVanillaBlock()
	{
		return Blocks.STONE_PRESSURE_PLATE;
	}

	@Override
	public IBlockState getConvertedState(IBlockState vanillaState)
	{
		return getDefaultState();
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(IBlockState state, IBlockReader world)
	{
		return new TileEntityReinforcedPressurePlate();
	}
}
