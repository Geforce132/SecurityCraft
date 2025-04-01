package net.geforcemods.securitycraft.blocks.reinforced;

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
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;

public class ReinforcedPistonHeadBlock extends PistonHeadBlock implements IReinforcedBlock {
	public ReinforcedPistonHeadBlock(AbstractBlock.Properties properties) {
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

		super.setPlacedBy(level, pos, state, placer, stack);
	}

	@Override
	public boolean isFittingBase(BlockState baseState, BlockState extendedState) {
		Block block = baseState.getValue(TYPE) == PistonType.DEFAULT ? SCContent.REINFORCED_PISTON.get() : SCContent.REINFORCED_STICKY_PISTON.get();

		return extendedState.is(block) && extendedState.getValue(PistonBlock.EXTENDED) && extendedState.getValue(FACING) == baseState.getValue(FACING);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		Block oppositeState = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).getBlock();

		return oppositeState == SCContent.REINFORCED_PISTON.get() || oppositeState == SCContent.REINFORCED_STICKY_PISTON.get() || oppositeState == SCContent.REINFORCED_MOVING_PISTON.get();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player) {
		return new ItemStack(state.getValue(TYPE) == PistonType.STICKY ? SCContent.REINFORCED_STICKY_PISTON.get() : SCContent.REINFORCED_PISTON.get());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new OwnableBlockEntity();
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.PISTON_HEAD;
	}
}
