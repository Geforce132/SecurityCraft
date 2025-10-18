package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.inventory.LensContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class LaserBlock extends DisguisableBlock {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public LaserBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(level, pos, state, entity, stack);
		setLaser(level, pos, entity instanceof PlayerEntity ? (PlayerEntity) entity : null);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		LaserBlockBlockEntity be = (LaserBlockBlockEntity) level.getBlockEntity(pos);

		if (be.isOwnedBy(player)) {
			if (!level.isClientSide) {
				if (!be.isEnabled())
					player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
				else {
					NetworkHooks.openGui((ServerPlayerEntity) player, be, buf -> {
						buf.writeBlockPos(pos);
						buf.writeNbt(LaserBlockBlockEntity.saveSideConfig(be.getSideConfig()));
					});
				}
			}

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	public void setLaser(World level, BlockPos pos, PlayerEntity player) {
		for (Direction facing : Direction.values()) {
			setLaser(level, pos, facing, player);
		}
	}

	public void setLaser(World level, BlockPos pos, Direction facing, PlayerEntity player) {
		LaserBlockBlockEntity thisBe = (LaserBlockBlockEntity) level.getBlockEntity(pos);

		if (!thisBe.isEnabled() || !thisBe.isSideEnabled(facing))
			return;

		int boundType = LaserFieldBlock.getBoundType(facing);

		for (int i = 1; i <= ConfigHandler.SERVER.laserBlockRange.get(); i++) {
			BlockPos offsetPos = pos.relative(facing, i);
			BlockState offsetState = level.getBlockState(offsetPos);
			Block offsetBlock = offsetState.getBlock();

			if (!offsetState.isAir(level, offsetPos) && !offsetState.getMaterial().isReplaceable() && offsetBlock != SCContent.LASER_BLOCK.get())
				return;
			else if (offsetBlock == SCContent.LASER_BLOCK.get()) {
				LaserBlockBlockEntity thatBe = (LaserBlockBlockEntity) level.getBlockEntity(offsetPos);

				if (thisBe.getOwner().owns(thatBe) && thatBe.isEnabled()) {
					if (!thatBe.isSideEnabled(facing.getOpposite())) {
						thisBe.setSideEnabled(facing, false, null);
						return;
					}

					ModuleType failedType = thisBe.synchronizeWith(thatBe);

					if (failedType != null) {
						if (player != null) {
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:laser.sync_failed", Utils.getFormattedCoordinates(thatBe.getBlockPos()), Utils.localize(failedType.getTranslationKey())), TextFormatting.RED);
							thisBe.setSideEnabled(facing, false, null);
							thatBe.setSideEnabled(facing.getOpposite(), false, null);
							player.closeContainer();
						}

						return;
					}

					for (int j = 1; j < i; j++) {
						offsetPos = pos.relative(facing, j);
						offsetState = level.getBlockState(offsetPos);

						if (offsetState.isAir(level, offsetPos) || offsetState.getMaterial().isReplaceable()) {
							level.setBlockAndUpdate(offsetPos, SCContent.LASER_FIELD.get().getPotentiallyWaterloggedState(boundType, level, offsetPos));

							TileEntity te = level.getBlockEntity(offsetPos);

							if (te instanceof IOwnable)
								((IOwnable) te).setOwner(thisBe.getOwner().getUUID(), thisBe.getOwner().getName());
						}
					}

					thatBe.getLensContainer().setChanged();
				}

				return;
			}
		}
	}

	@Override
	public void destroy(IWorld level, BlockPos pos, BlockState state) {
		if (!level.isClientSide())
			destroyAdjacentLasers(level, pos);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof LaserBlockBlockEntity) {
				LaserBlockBlockEntity be = (LaserBlockBlockEntity) te;
				LensContainer lensContainer = be.getLensContainer();

				InventoryHelper.dropContents(level, pos, lensContainer);
				lensContainer.clearContent();
				level.updateNeighbourForOutputSignal(pos, this);
				LinkableBlockEntity.unlinkFromAllLinked(be);
			}
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	public static void destroyAdjacentLasers(IWorld level, BlockPos pos) {
		BlockUtils.removeInSequence((direction, state) -> {
			if (state.getBlock() != SCContent.LASER_FIELD.get())
				return false;

			return state.getValue(LaserFieldBlock.BOUNDTYPE) == LaserFieldBlock.getBoundType(direction);
		}, level, pos, Direction.values());
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		setLaser(level, pos, null);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader level, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		if (state.getValue(POWERED)) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof LaserBlockBlockEntity && ((LaserBlockBlockEntity) te).isModuleEnabled(ModuleType.REDSTONE))
				return 15;
		}

		return 0;
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		return getSignal(state, level, pos, side);
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		if (!level.isClientSide && state.getValue(POWERED)) {
			level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, pos, SCContent.LASER_BLOCK.get());
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World level, BlockPos pos, Random rand) {
		if (state.getValue(POWERED)) {
			double x = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;
			float r = 0.6F + 0.4F;
			float g = Math.max(0.0F, 0.7F - 0.5F);
			float b = Math.max(0.0F, 0.6F - 0.7F);

			level.addParticle(new RedstoneParticleData(r, g, b, 1), false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(new RedstoneParticleData(r, g, b, 1), false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(new RedstoneParticleData(r, g, b, 1), false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			level.addParticle(new RedstoneParticleData(r, g, b, 1), false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			level.addParticle(new RedstoneParticleData(r, g, b, 1), false, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(POWERED, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new LaserBlockBlockEntity();
	}
}
