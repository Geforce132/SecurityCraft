package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.AllowlistOnlyTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedPressurePlateBlock extends PressurePlateBlock implements IReinforcedBlock
{
	private final Block vanillaBlock;

	public ReinforcedPressurePlateBlock(Sensitivity sensitivity, Block.Properties properties, Block vanillaBlock)
	{
		super(sensitivity, properties);

		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity)
	{
		int redstoneStrength = getSignalForState(state);

		if(!world.isClientSide && redstoneStrength == 0 && entity instanceof PlayerEntity)
		{
			TileEntity tileEntity = world.getBlockEntity(pos);

			if(tileEntity instanceof AllowlistOnlyTileEntity)
			{
				if(isAllowedToPress(world, pos, (AllowlistOnlyTileEntity)tileEntity, (PlayerEntity)entity))
					checkPressed(world, pos, state, redstoneStrength);
			}
		}
	}

	@Override
	protected int getSignalStrength(World world, BlockPos pos)
	{
		AxisAlignedBB aabb = TOUCH_AABB.move(pos);
		List<? extends Entity> list;

		list = world.getEntities(null, aabb);

		if(!list.isEmpty())
		{
			TileEntity tileEntity = world.getBlockEntity(pos);

			if(tileEntity instanceof AllowlistOnlyTileEntity)
			{
				for(Entity entity : list)
				{
					if(entity instanceof PlayerEntity && isAllowedToPress(world, pos, (AllowlistOnlyTileEntity)tileEntity, (PlayerEntity)entity))
						return 15;
				}
			}
		}

		return 0;
	}

	public boolean isAllowedToPress(World world, BlockPos pos, AllowlistOnlyTileEntity te, PlayerEntity entity)
	{
		return te.getOwner().isOwner(entity) || ModuleUtils.isAllowed(te, entity);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlock;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState();
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new AllowlistOnlyTileEntity();
	}
}
