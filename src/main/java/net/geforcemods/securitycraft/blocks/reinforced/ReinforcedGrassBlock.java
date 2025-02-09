package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;

public class ReinforcedGrassBlock extends GrassBlock implements IReinforcedBlock {
	public ReinforcedGrassBlock(AbstractBlock.Properties properties) {
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
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		return facing == Direction.UP ? state.setValue(SNOWY, ReinforcedSnowyDirtBlock.isSnowySetting(facingState.getBlock())) : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return defaultBlockState().setValue(SNOWY, ReinforcedSnowyDirtBlock.isSnowySetting(ctx.getLevel().getBlockState(ctx.getClickedPos().above()).getBlock()));
	}

	@Override
	public void randomTick(BlockState state, ServerWorld level, BlockPos pos, Random random) {}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader level, BlockPos pos, Direction facing, IPlantable plantable) {
		return SCContent.REINFORCED_DIRT.get().canSustainPlant(state, level, pos, facing, plantable);
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.GRASS_BLOCK;
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new OwnableBlockEntity();
	}
}
