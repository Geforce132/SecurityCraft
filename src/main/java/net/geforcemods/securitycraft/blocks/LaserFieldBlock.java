package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LaserFieldBlock extends OwnableBlock implements IOverlayDisplay {
	public static final PropertyInteger BOUNDTYPE = PropertyInteger.create("boundtype", 1, 3);
	private static final AxisAlignedBB BOUNDTYPE_1_AABB, BOUNDTYPE_2_AABB, BOUNDTYPE_3_AABB;

	static {
		float px = 1.0F / 16.0F;

		BOUNDTYPE_1_AABB = new AxisAlignedBB(6.75 * px, 0.0F, 6.75 * px, 9.25 * px, 1.0F, 9.25 * px);
		BOUNDTYPE_2_AABB = new AxisAlignedBB(6.75 * px, 6.75 * px, 0.0F, 9.25 * px, 9.25 * px, 1.0F);
		BOUNDTYPE_3_AABB = new AxisAlignedBB(0.0F, 6.75 * px, 6.75 * px, 1.0F, 9.25 * px, 9.25 * px);
	}

	public LaserFieldBlock(Material material) {
		super(material);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (!world.isRemote && entity instanceof EntityLivingBase) {
			if (!state.getBoundingBox(world, pos).offset(pos).intersects(entity.getEntityBoundingBox()))
				return;

			for (int i = 0; i < ConfigHandler.laserBlockRange; i++) {
				BlockPos offsetPos = pos.offset(getFieldDirection(state), i);
				IBlockState offsetState = world.getBlockState(offsetPos);
				Block block = offsetState.getBlock();

				if (block == SCContent.laserBlock) {
					TileEntity te = world.getTileEntity(offsetPos);

					if (te instanceof LaserBlockBlockEntity) {
						LaserBlockBlockEntity laser = (LaserBlockBlockEntity) te;

						if (laser.isAllowed(entity) || laser.isConsideredInvisible((EntityLivingBase) entity))
							return;

						if (!(entity instanceof EntityPlayer && laser.isOwnedBy(entity) && laser.ignoresOwner())) {
							if (laser.allowsOwnableEntity(entity))
								return;

							if (laser.isModuleEnabled(ModuleType.REDSTONE)) {
								if (laser.timeSinceLastToggle() < 500)
									laser.setLastToggleTime(System.currentTimeMillis());
								else {
									int signalLength = laser.getSignalLength();
									boolean wasPowered = offsetState.getValue(LaserBlock.POWERED);

									laser.setLastToggleTime(System.currentTimeMillis());
									world.setBlockState(offsetPos, offsetState.cycleProperty(LaserBlock.POWERED));
									BlockUtils.updateIndirectNeighbors(world, offsetPos, SCContent.laserBlock);
									laser.propagate(new ILinkedAction.StateChanged<>(LaserBlock.POWERED, wasPowered, !wasPowered), laser);

									if (signalLength > 0)
										world.scheduleUpdate(offsetPos, SCContent.laserBlock, signalLength);
								}
							}

							if (laser.isModuleEnabled(ModuleType.HARMING))
								((EntityLivingBase) entity).attackEntityFrom(CustomDamageSources.LASER, (float) ConfigHandler.laserDamage);
						}

						break;
					}
				}
			}
		}
	}

	public static EnumFacing getFieldDirection(IBlockState state) {
		if (state.getBlock() instanceof LaserFieldBlock) {
			int boundType = state.getValue(BOUNDTYPE);

			if (boundType == 1)
				return EnumFacing.UP;
			else if (boundType == 2)
				return EnumFacing.SOUTH;
			else if (boundType == 3)
				return EnumFacing.EAST;
		}

		return null;
	}

	public static int getBoundType(EnumFacing direction) {
		switch (direction) {
			case UP:
			case DOWN:
				return 1;
			case NORTH:
			case SOUTH:
				return 2;
			case EAST:
			case WEST:
				return 3;
			default:
				return 1;
		}
	}

	@Override
	public void onPlayerDestroy(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote) {
			int boundType = state.getValue(LaserFieldBlock.BOUNDTYPE);
			EnumFacing direction = EnumFacing.byIndex((boundType - 1) * 2);

			BlockUtils.removeInSequence((directionToCheck, stateToCheck) -> stateToCheck.getBlock() == this && stateToCheck.getValue(BOUNDTYPE) == boundType, world, pos, direction, direction.getOpposite());
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (source.getBlockState(pos).getValue(BOUNDTYPE) == 1)
			return BOUNDTYPE_1_AABB;
		else if (source.getBlockState(pos).getValue(BOUNDTYPE) == 2)
			return BOUNDTYPE_2_AABB;
		else if (source.getBlockState(pos).getValue(BOUNDTYPE) == 3)
			return BOUNDTYPE_3_AABB;
		else
			return FULL_BLOCK_AABB;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(BOUNDTYPE, 1);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(BOUNDTYPE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BOUNDTYPE);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BOUNDTYPE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		int boundType = state.getValue(BOUNDTYPE);

		return rot == Rotation.CLOCKWISE_180 ? state : state.withProperty(BOUNDTYPE, boundType == 2 ? 3 : (boundType == 3 ? 2 : 1));
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		return null;
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return false;
	}
}
