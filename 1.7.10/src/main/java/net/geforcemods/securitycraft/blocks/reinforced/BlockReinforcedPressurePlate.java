package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedPressurePlate;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedPressurePlate extends BlockPressurePlate implements IReinforcedBlock
{
	public BlockReinforcedPressurePlate()
	{
		super("stone", Material.rock, Sensitivity.players);

		setStepSound(soundTypeStone);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		int redstoneStrength = getPowerFromMeta(world.getBlockMetadata(x, y, z));

		if(!world.isRemote && redstoneStrength == 0 && entity instanceof EntityPlayer)
		{
			TileEntity tileEntity = world.getTileEntity(x, y, z);

			if(tileEntity instanceof TileEntityReinforcedPressurePlate)
			{
				if(isAllowedToPress(world, x, y, z, (TileEntityReinforcedPressurePlate)tileEntity, (EntityPlayer)entity))
					setStateIfMobInteractsWithPlate(world, x, y, z, redstoneStrength);
			}
		}
	}

	@Override
	protected int getPlateState(World world, int x, int y, int z)
	{
		AxisAlignedBB aabb = getSensitiveAABB(x, y, z);
		List<? extends Entity> list;

		list = world.getEntitiesWithinAABBExcludingEntity(null, aabb);

		if(!list.isEmpty())
		{
			TileEntity tileEntity = world.getTileEntity(x, y, z);

			if(tileEntity instanceof TileEntityReinforcedPressurePlate)
			{
				for(Entity entity : list)
				{
					if(entity instanceof EntityPlayer && isAllowedToPress(world, x, y, z, (TileEntityReinforcedPressurePlate)tileEntity, (EntityPlayer)entity))
						return 15;
				}
			}
		}

		return 0;
	}

	public boolean isAllowedToPress(World world, int x, int y, int z, TileEntityReinforcedPressurePlate te, EntityPlayer entity)
	{
		return te.getOwner().isOwner(entity) || ModuleUtils.getPlayersFromModule(world, x, y, z, EnumCustomModules.WHITELIST).contains(entity.getCommandSenderName().toLowerCase());
	}

	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z)
	{
		return 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta)
	{
		return 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0x999999;
	}

	@Override
	public boolean hasTileEntity(int metadata)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta)
	{
		return new TileEntityReinforcedPressurePlate();
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(Blocks.stone_pressure_plate);
	}

	@Override
	public int getAmount()
	{
		return 1;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}
}
