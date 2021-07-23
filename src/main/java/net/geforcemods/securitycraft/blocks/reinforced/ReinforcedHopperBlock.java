package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.ReinforcedHopperTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedHopperBlock extends HopperBlock implements IReinforcedBlock
{
	public ReinforcedHopperBlock(Block.Properties properties)
	{
		super(properties);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof Player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (Player)placer));

		if(stack.hasCustomHoverName())
		{
			BlockEntity te = world.getBlockEntity(pos);

			if(te instanceof ReinforcedHopperTileEntity)
				((ReinforcedHopperTileEntity)te).setCustomName(stack.getHoverName());
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if(!world.isClientSide)
		{
			BlockEntity tileEntity = world.getBlockEntity(pos);

			if(tileEntity instanceof ReinforcedHopperTileEntity te)
			{
				//only allow the owner or players on the allowlist to access a reinforced hopper
				if(te.getOwner().isOwner(player) || ModuleUtils.isAllowed(te, player))
					player.openMenu(te);
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(state.getBlock() != newState.getBlock())
		{
			BlockEntity te = world.getBlockEntity(pos);

			if(te instanceof ReinforcedHopperTileEntity)
			{
				Containers.dropContents(world, pos, (ReinforcedHopperTileEntity)te);
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity)
	{
		BlockEntity te = world.getBlockEntity(pos);

		if(te instanceof ReinforcedHopperTileEntity)
			((ReinforcedHopperTileEntity)te).onEntityCollision(entity);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
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
		public boolean canExtract(IOwnable te, Level world, BlockPos pos, BlockState state)
		{
			ReinforcedHopperTileEntity hopperTe = (ReinforcedHopperTileEntity)world.getBlockEntity(pos);

			if(!te.getOwner().owns(hopperTe))
			{
				if(te instanceof IModuleInventory inv)
				{
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
