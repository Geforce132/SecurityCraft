package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedLecternBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

public class ReinforcedLecternBlock extends LecternBlock implements IReinforcedBlock {
	public ReinforcedLecternBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, IBlockReader level, BlockPos pos, PlayerEntity player) {
		return ConfigHandler.SERVER.alwaysDrop.get() || super.canHarvestBlock(state, level, pos, player);
	}

	@Override
	public ToolType getHarvestTool(BlockState state) {
		return getVanillaBlock().getHarvestTool(convertToVanilla(null, null, state));
	}

	@Override
	public int getHarvestLevel(BlockState state) {
		return getVanillaBlock().getHarvestLevel(convertToVanilla(null, null, state));
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!level.isClientSide) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof ReinforcedLecternBlockEntity) {
				ReinforcedLecternBlockEntity be = (ReinforcedLecternBlockEntity) te;

				if (be.isOwnedBy(player) || be.isAllowed(player))
					return super.use(state, level, pos, player, hand, hit);
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (isMoving) {
				TileEntity be = level.getBlockEntity(pos);

				if (be instanceof ReinforcedLecternBlockEntity)
					((ReinforcedLecternBlockEntity) be).clearContent(); //Clear the items from the block before it is moved by a piston to prevent duplication
			}

			level.updateNeighbourForOutputSignal(pos, this);
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void openScreen(World level, BlockPos pos, PlayerEntity player) {
		TileEntity be = level.getBlockEntity(pos);

		if (be instanceof ReinforcedLecternBlockEntity) {
			NetworkHooks.openGui((ServerPlayerEntity) player, (ReinforcedLecternBlockEntity) be, pos);
			player.awardStat(Stats.INTERACT_WITH_LECTERN);
		}
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader level) {
		return new ReinforcedLecternBlockEntity();
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.LECTERN;
	}
}
