package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;

public class ReinforcedButtonBlock extends AbstractButtonBlock implements IReinforcedBlock {
	private final Block vanillaBlock;
	private final boolean isWoodenButton;

	public ReinforcedButtonBlock(boolean isWooden, AbstractBlock.Properties properties, Block vb) {
		super(isWooden, properties);
		this.isWoodenButton = isWooden;
		this.vanillaBlock = vb;
	}

	@Override
	public SoundEvent getSound(boolean powered) {
		if (isWoodenButton)
			return powered ? SoundEvents.WOODEN_BUTTON_CLICK_ON : SoundEvents.WOODEN_BUTTON_CLICK_OFF;
		else
			return powered ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
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
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace) {
		if (isAllowedToPress((AllowlistOnlyBlockEntity) level.getBlockEntity(pos), player))
			return super.use(state, level, pos, player, hand, rayTrace);

		return ActionResultType.FAIL;
	}

	@Override
	public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (!ConfigHandler.SERVER.vanillaToolBlockBreaking.get() && te instanceof IModuleInventory)
				((IModuleInventory) te).dropAllModules();

			if (!isMoving && state.getValue(POWERED)) {
				level.updateNeighborsAt(pos, this);
				level.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
			}
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	public boolean isAllowedToPress(AllowlistOnlyBlockEntity be, PlayerEntity entity) {
		return be.isOwnedBy(entity) || be.isAllowed(entity);
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK; //Can't be PushReaction.NORMAL because buttons rely on scheduled ticks which don't support moving the block
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new AllowlistOnlyBlockEntity();
	}
}
