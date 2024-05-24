package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedHopperBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedHopperBlock extends HopperBlock implements IReinforcedBlock {
	public ReinforcedHopperBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));

		super.setPlacedBy(level, pos, state, placer, stack);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!level.isClientSide) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof ReinforcedHopperBlockEntity) {
				ReinforcedHopperBlockEntity be = (ReinforcedHopperBlockEntity) te;

				//only allow the owner or players on the allowlist to access a reinforced hopper
				if (be.isOwnedBy(player) || be.isAllowed(player))
					player.openMenu(be);
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof ReinforcedHopperBlockEntity) {
				if (isMoving)
					((ReinforcedHopperBlockEntity) te).clearContent();

				level.updateNeighbourForOutputSignal(pos, this);
			}
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof ReinforcedHopperBlockEntity)
			((ReinforcedHopperBlockEntity) te).entityInside(entity);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader level) {
		return new ReinforcedHopperBlockEntity();
	}

	@Override
	public boolean is(Block block) {
		return block == this || block == Blocks.HOPPER;
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.HOPPER;
	}

	public static class ExtractionBlock implements IExtractionBlock {
		@Override
		public boolean canExtract(IOwnable ownable, World world, BlockPos pos, BlockState state) {
			ReinforcedHopperBlockEntity hopperBe = (ReinforcedHopperBlockEntity) world.getBlockEntity(pos);

			if (!hopperBe.getOwner().isValidated())
				return false;
			else if (!ownable.getOwner().owns(hopperBe)) {
				if (ownable instanceof IModuleInventory)
					return ((IModuleInventory) ownable).isAllowed(hopperBe.getOwner().getName()); //hoppers can extract out of e.g. chests if the hopper's owner is on the chest's allowlist module

				return false;
			}
			else
				return true;
		}

		@Override
		public Block getBlock() {
			return SCContent.REINFORCED_HOPPER.get();
		}
	}
}
