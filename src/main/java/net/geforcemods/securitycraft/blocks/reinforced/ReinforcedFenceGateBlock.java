package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;

public class ReinforcedFenceGateBlock extends FenceGateBlock {
	public ReinforcedFenceGateBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return ActionResultType.FAIL;
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity) placer));
	}

	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		if (world.getBlockState(pos).getValue(OPEN))
			return;

		if (!getShape(state, world, pos, ISelectionContext.of(entity)).bounds().move(pos).inflate(0.01D).intersects(entity.getBoundingBox()))
			return;
		else if (entity instanceof ItemEntity)
			return;
		else if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;

			if (((OwnableBlockEntity) world.getBlockEntity(pos)).isOwnedBy(player))
				return;
		}
		else if (!world.isClientSide && entity instanceof CreeperEntity) {
			CreeperEntity creeper = (CreeperEntity) entity;
			LightningBoltEntity lightning = LevelUtils.createLightning(world, Vector3d.atBottomCenterOf(pos), true);

			creeper.thunderHit((ServerWorld) world, lightning);
			return;
		}

		entity.hurt(CustomDamageSources.ELECTRICITY, 6.0F);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!world.isClientSide) {
			boolean isPoweredSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos);

			if (isPoweredSCBlock || block.defaultBlockState().isSignalSource()) {
				if (isPoweredSCBlock && !state.getValue(OPEN) && !state.getValue(POWERED)) {
					world.setBlock(pos, state.setValue(OPEN, true).setValue(POWERED, true), 2);
					world.levelEvent(null, Constants.WorldEvents.IRON_DOOR_OPEN_SOUND, pos, 0);
				}
				else if (!isPoweredSCBlock && state.getValue(OPEN) && state.getValue(POWERED)) {
					world.setBlock(pos, state.setValue(OPEN, false).setValue(POWERED, false), 2);
					world.levelEvent(null, Constants.WorldEvents.IRON_DOOR_CLOSE_SOUND, pos, 0);
				}
				else if (isPoweredSCBlock != state.getValue(POWERED)) {
					world.setBlock(pos, state.setValue(POWERED, isPoweredSCBlock), 2);
				}
			}
		}
	}

	@Override
	public boolean triggerEvent(BlockState state, World world, BlockPos pos, int id, int param) {
		TileEntity be = world.getBlockEntity(pos);

		return be != null && be.triggerEvent(id, param);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get());
	}
}
