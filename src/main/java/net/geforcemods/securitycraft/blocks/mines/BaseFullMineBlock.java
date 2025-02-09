package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BaseFullMineBlock extends ExplosiveBlock implements IOverlayDisplay, IBlockMine {
	private final Block blockDisguisedAs;

	public BaseFullMineBlock(AbstractBlock.Properties properties, Block disguisedBlock) {
		super(properties);
		blockDisguisedAs = disguisedBlock;
	}

	@Override
	public ToolType getHarvestTool(BlockState state) {
		return blockDisguisedAs.getHarvestTool(blockDisguisedAs.defaultBlockState());
	}

	@Override
	public int getHarvestLevel(BlockState state) {
		return blockDisguisedAs.getHarvestLevel(blockDisguisedAs.defaultBlockState());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		if (ctx instanceof EntitySelectionContext) {
			Entity entity = ((EntitySelectionContext) ctx).getEntity();

			if (entity instanceof ItemEntity)
				return VoxelShapes.block();

			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof OwnableBlockEntity) {
				OwnableBlockEntity ownable = (OwnableBlockEntity) te;

				if (ownable.allowsOwnableEntity(entity))
					return VoxelShapes.block();

				if (entity instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity) entity;

					if (ownable.isOwnedBy(player) || player.isCreative())
						return VoxelShapes.block();
				}
			}

			return ctx == ISelectionContext.empty() ? VoxelShapes.block() : VoxelShapes.empty();
		}

		return VoxelShapes.block();
	}

	@Override
	public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
		if (!Utils.doesEntityOwn(entity, level, pos))
			explode(level, pos);
	}

	@Override
	public void wasExploded(World level, BlockPos pos, Explosion explosion) {
		if (!level.isClientSide) {
			if (pos.equals(new BlockPos(explosion.getPosition())))
				return;

			explode(level, pos);
		}
	}

	@Override
	public boolean removedByPlayer(BlockState state, World level, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
		if (!level.isClientSide) {
			if (player != null && player.isCreative() && !ConfigHandler.SERVER.mineExplodesWhenInCreative.get())
				return super.removedByPlayer(state, level, pos, player, willHarvest, fluid);
			else if (!Utils.doesEntityOwn(player, level, pos)) {
				explode(level, pos);
				return super.removedByPlayer(state, level, pos, player, willHarvest, fluid);
			}
		}

		return super.removedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Override
	public boolean activateMine(World level, BlockPos pos) {
		return false;
	}

	@Override
	public boolean defuseMine(World level, BlockPos pos) {
		return false;
	}

	@Override
	public void explode(World level, BlockPos pos) {
		if (!level.isClientSide) {
			level.destroyBlock(pos, false);
			level.explode((Entity) null, pos.getX(), pos.getY() + 0.5D, pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 2.5F : 5.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
		}
	}

	@Override
	public boolean dropFromExplosion(Explosion explosion) {
		return false;
	}

	@Override
	public boolean isActive(World level, BlockPos pos) {
		return true;
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return false;
	}

	@Override
	public boolean isDefusable() {
		return false;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get());
	}

	@Override
	public ItemStack getDisplayStack(World level, BlockState state, BlockPos pos) {
		return new ItemStack(blockDisguisedAs);
	}

	@Override
	public boolean shouldShowSCInfo(World level, BlockState state, BlockPos pos) {
		return false;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player) {
		if (level.getBlockEntity(pos) instanceof OwnableBlockEntity) {
			OwnableBlockEntity te = (OwnableBlockEntity) level.getBlockEntity(pos);

			if (player.isCreative() || te.isOwnedBy(player))
				return super.getPickBlock(state, target, level, pos, player);
		}

		return new ItemStack(blockDisguisedAs);
	}

	public Block getBlockDisguisedAs() {
		return blockDisguisedAs;
	}
}