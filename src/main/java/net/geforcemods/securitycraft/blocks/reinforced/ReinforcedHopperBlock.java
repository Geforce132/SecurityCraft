package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.ReinforcedHopperTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedHopperBlock extends HopperBlock implements IReinforcedBlock
{
	public ReinforcedHopperBlock(Block.Properties properties)
	{
		super(properties);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));

		if(stack.hasCustomHoverName())
		{
			TileEntity te = world.getBlockEntity(pos);

			if(te instanceof ReinforcedHopperTileEntity)
				((ReinforcedHopperTileEntity)te).setCustomName(stack.getHoverName());
		}
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(!world.isClientSide)
		{
			TileEntity tileEntity = world.getBlockEntity(pos);

			if(tileEntity instanceof ReinforcedHopperTileEntity)
			{
				ReinforcedHopperTileEntity te = (ReinforcedHopperTileEntity)tileEntity;

				//only allow the owner or players on the allowlist to access a reinforced hopper
				if(te.getOwner().isOwner(player) || ModuleUtils.isAllowed(te, player))
					player.openMenu(te);
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(state.getBlock() != newState.getBlock())
		{
			TileEntity te = world.getBlockEntity(pos);

			if(te instanceof ReinforcedHopperTileEntity)
			{
				InventoryHelper.dropContents(world, pos, (ReinforcedHopperTileEntity)te);
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity)
	{
		TileEntity te = world.getBlockEntity(pos);

		if(te instanceof ReinforcedHopperTileEntity)
			((ReinforcedHopperTileEntity)te).onEntityCollision(entity);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world)
	{
		return new ReinforcedHopperTileEntity();
	}

	@Override
	public boolean is(Block block)
	{
		return block == this || block == Blocks.HOPPER;
	}

	@Override
	public Block getVanillaBlock()
	{
		return Blocks.HOPPER;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState().setValue(ENABLED, vanillaState.getValue(ENABLED)).setValue(FACING, vanillaState.getValue(FACING));
	}

	public static class ExtractionBlock implements IExtractionBlock
	{
		@Override
		public boolean canExtract(IOwnable te, World world, BlockPos pos, BlockState state)
		{
			ReinforcedHopperTileEntity hopperTe = (ReinforcedHopperTileEntity)world.getBlockEntity(pos);

			if(!te.getOwner().owns(hopperTe))
			{
				if(te instanceof IModuleInventory)
				{
					IModuleInventory inv = (IModuleInventory)te;

					//hoppers can extract out of e.g. chests if the hopper's owner is on the chest's allowlist module
					if(ModuleUtils.isAllowed(inv, hopperTe.getOwner().getName()))
						return true;
					//hoppers can extract out of e.g. chests whose owner is on the hopper's allowlist module
					else if(ModuleUtils.isAllowed(hopperTe, te.getOwner().getName()))
						return true;
				}

				return false;
			}
			else return true;
		}

		@Override
		public Block getBlock()
		{
			return SCContent.REINFORCED_HOPPER.get();
		}
	}
}
