package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
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
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		int redstoneStrength = getRedstoneStrength(state);

		if (!world.isRemote && redstoneStrength == 0 && entity instanceof PlayerEntity) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof AllowlistOnlyTileEntity) {
				if (isAllowedToPress(world, pos, (AllowlistOnlyTileEntity) te, (PlayerEntity) entity))
					updateState(world, pos, state, redstoneStrength);
			}
		}
	}

	@Override
	protected int computeRedstoneStrength(World world, BlockPos pos) {
		AxisAlignedBB aabb = PRESSURE_AABB.offset(pos);
		List<? extends Entity> list;

		list = world.getEntitiesWithinAABBExcludingEntity(null, aabb);

		if (!list.isEmpty()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof AllowlistOnlyTileEntity) {
				for (Entity entity : list) {
					if (entity instanceof PlayerEntity && isAllowedToPress(world, pos, (AllowlistOnlyTileEntity) te, (PlayerEntity) entity))
						return 15;
				}
			}
		}

		return 0;
	}

	public boolean isAllowedToPress(World world, BlockPos pos, AllowlistOnlyTileEntity te, PlayerEntity entity) {
		return te.getOwner().isOwner(entity) || ModuleUtils.isAllowed(te, entity);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity) placer));
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState) {
		return getDefaultState();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AllowlistOnlyTileEntity();
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
			return state.get(POWERED) && (distance < 2 || direction == Direction.UP);
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}
