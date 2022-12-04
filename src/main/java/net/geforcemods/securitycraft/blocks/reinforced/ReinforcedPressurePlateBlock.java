package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
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

public class ReinforcedPressurePlateBlock extends PressurePlateBlock implements IReinforcedBlock {
	private final Block vanillaBlock;

	public ReinforcedPressurePlateBlock(Sensitivity sensitivity, Block.Properties properties, Block vanillaBlock) {
		super(sensitivity, properties);

		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		int redstoneStrength = getSignalForState(state);

		if (!world.isClientSide && redstoneStrength == 0 && entity instanceof PlayerEntity) {
			TileEntity te = world.getBlockEntity(pos);

			if (te instanceof AllowlistOnlyBlockEntity) {
				if (isAllowedToPress(world, pos, (AllowlistOnlyBlockEntity) te, (PlayerEntity) entity))
					checkPressed(world, pos, state, redstoneStrength);
			}
		}
	}

	@Override
	protected int getSignalStrength(World world, BlockPos pos) {
		AxisAlignedBB aabb = TOUCH_AABB.move(pos);
		List<? extends Entity> list;

		list = world.getEntities(null, aabb);

		if (!list.isEmpty()) {
			TileEntity te = world.getBlockEntity(pos);

			if (te instanceof AllowlistOnlyBlockEntity) {
				for (Entity entity : list) {
					if (entity instanceof PlayerEntity && isAllowedToPress(world, pos, (AllowlistOnlyBlockEntity) te, (PlayerEntity) entity))
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

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).dropAllModules();

			if (!isMoving && getSignalForState(state) > 0) {
				level.updateNeighborsAt(pos, this);
				level.updateNeighborsAt(pos.below(), this);
			}

			if (!newState.hasTileEntity())
				level.removeBlockEntity(pos);
		}
	}

	public boolean isAllowedToPress(World world, BlockPos pos, AllowlistOnlyBlockEntity te, PlayerEntity entity) {
		return te.isOwnedBy(entity) || te.isAllowed(entity);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity) placer));
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState) {
		return defaultBlockState();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
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
		public boolean isPowering(World world, BlockPos pos, BlockState state, TileEntity te, Direction direction, int distance) {
			return state.getValue(POWERED) && (distance < 2 || direction == Direction.UP);
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}
