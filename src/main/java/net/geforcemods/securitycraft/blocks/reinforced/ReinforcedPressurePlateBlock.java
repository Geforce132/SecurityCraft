package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;

public class ReinforcedPressurePlateBlock extends PressurePlateBlock implements IReinforcedBlock {
	private final Block vanillaBlock;
	private final float destroyTimeForOwner;

	public ReinforcedPressurePlateBlock(Sensitivity sensitivity, AbstractBlock.Properties properties, Block vanillaBlock) {
		super(sensitivity, OwnableBlock.withReinforcedDestroyTime(properties));
		this.vanillaBlock = vanillaBlock;
		destroyTimeForOwner = OwnableBlock.getStoredDestroyTime();
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
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
	public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
		int redstoneStrength = getSignalForState(state);

		if (!level.isClientSide && redstoneStrength == 0 && entity instanceof PlayerEntity) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof AllowlistOnlyBlockEntity && isAllowedToPress((AllowlistOnlyBlockEntity) te, (PlayerEntity) entity))
				checkPressed(level, pos, state, redstoneStrength);
		}
	}

	@Override
	protected int getSignalStrength(World level, BlockPos pos) {
		AxisAlignedBB aabb = TOUCH_AABB.move(pos);
		List<? extends Entity> list;

		list = level.getEntities(null, aabb);

		if (!list.isEmpty()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof AllowlistOnlyBlockEntity) {
				for (Entity entity : list) {
					if (entity instanceof PlayerEntity && isAllowedToPress((AllowlistOnlyBlockEntity) te, (PlayerEntity) entity))
						return 15;
				}
			}
		}

		return 0;
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

			if (!isMoving && getSignalForState(state) > 0) {
				level.updateNeighborsAt(pos, this);
				level.updateNeighborsAt(pos.below(), this);
			}
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	public boolean isAllowedToPress(AllowlistOnlyBlockEntity be, PlayerEntity entity) {
		return be.isOwnedBy(entity) || be.isAllowed(entity);
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK; //Can't be PushReaction.NORMAL because pressure plates rely on scheduled ticks which don't support moving the block
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}

	@Override
	public BlockState convertToReinforced(World level, BlockPos pos, BlockState vanillaState) {
		return defaultBlockState();
	}

	@Override
	public BlockState convertToVanilla(World level, BlockPos pos, BlockState reinforcedState) {
		return getVanillaBlock().defaultBlockState();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new AllowlistOnlyBlockEntity();
	}

	public static class DoorActivator implements IDoorActivator {
		//@formatter:off
		private final List<Block> blocks = Arrays.asList(
				SCContent.REINFORCED_STONE_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_OAK_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_CRIMSON_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_WARPED_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE.get());
		//@formatter:on

		@Override
		public boolean isPowering(World level, BlockPos pos, BlockState state, TileEntity be, Direction direction, int distance) {
			return state.getValue(POWERED) && (distance < 2 || direction == Direction.UP);
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}
