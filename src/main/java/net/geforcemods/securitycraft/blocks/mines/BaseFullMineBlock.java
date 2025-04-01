package net.geforcemods.securitycraft.blocks.mines;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BaseFullMineBlock extends ExplosiveBlock implements IOverlayDisplay, IBlockMine {
	private final Block blockDisguisedAs;

	public BaseFullMineBlock(Block disguisedBlock) {
		super(disguisedBlock.getMaterial(disguisedBlock.getDefaultState()));
		blockDisguisedAs = disguisedBlock;
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
		return blockDisguisedAs.getDefaultState().getBlockHardness(world, pos);
	}

	@Override
	public Material getMaterial(IBlockState state) {
		return blockDisguisedAs.getDefaultState().getMaterial();
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		IBlockState vanillaState = blockDisguisedAs.getDefaultState();

		return vanillaState.getBlock().getSoundType(vanillaState, world, pos, entity);
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess level, BlockPos pos) {
		return blockDisguisedAs.getDefaultState().getMapColor(level, pos);
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		IBlockState vanillaState = blockDisguisedAs.getDefaultState();

		return vanillaState.getBlock().getHarvestTool(vanillaState);
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		IBlockState vanillaState = blockDisguisedAs.getDefaultState();

		return vanillaState.getBlock().isToolEffective(type, vanillaState);
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		IBlockState vanillaState = blockDisguisedAs.getDefaultState();

		return vanillaState.getBlock().getHarvestLevel(vanillaState);
	}

	@Override
	public boolean isTranslucent(IBlockState state) {
		return blockDisguisedAs.getDefaultState().isTranslucent();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess access, BlockPos pos) {
		return null;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
		if (entity instanceof EntityItem)
			addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
		else {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof OwnableBlockEntity) {
				OwnableBlockEntity ownableTe = (OwnableBlockEntity) te;

				if (ownableTe.allowsOwnableEntity(entity)) {
					addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
					return;
				}

				if (entity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entity;

					if (ownableTe.isOwnedBy(player) || player.isCreative()) {
						addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
						return;
					}
				}
			}

			addCollisionBoxToList(pos, entityBox, collidingBoxes, NULL_AABB);
		}
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (!Utils.doesEntityOwn(entity, world, pos))
			explode(world, pos);
	}

	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion) {
		if (!world.isRemote) {
			if (pos.equals(new BlockPos(explosion.getPosition())))
				return;

			explode(world, pos);
		}
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (!world.isRemote)
			if (player != null && player.capabilities.isCreativeMode && !ConfigHandler.mineExplodesWhenInCreative)
				return super.removedByPlayer(state, world, pos, player, willHarvest);
			else if (!Utils.doesEntityOwn(player, world, pos)) {
				explode(world, pos);
				return super.removedByPlayer(state, world, pos, player, willHarvest);
			}

		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public boolean activateMine(World world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean defuseMine(World world, BlockPos pos) {
		return false;
	}

	@Override
	public void explode(World world, BlockPos pos) {
		if (!world.isRemote) {
			world.destroyBlock(pos, false);
			world.newExplosion((Entity) null, pos.getX(), pos.getY() + 0.5D, pos.getZ(), ConfigHandler.smallerMineExplosion ? 2.5F : 5.0F, ConfigHandler.shouldSpawnFire, ConfigHandler.mineExplosionsBreakBlocks);
		}
	}

	@Override
	public boolean canDropFromExplosion(Explosion explosion) {
		return false;
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
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
	public TileEntity createNewTileEntity(World world, int meta) {
		return new OwnableBlockEntity();
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		return new ItemStack(blockDisguisedAs);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return false;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		if (IDisguisable.shouldPickBlockDisguise(world, pos, player))
			return new ItemStack(blockDisguisedAs);

		return super.getPickBlock(state, target, world, pos, player);
	}

	public Block getBlockDisguisedAs() {
		return blockDisguisedAs;
	}
}