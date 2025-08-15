package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockButton;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class PanicButtonBlock extends BlockButton implements ITileEntityProvider {
	private final float destroyTimeForOwner;

	public PanicButtonBlock() {
		super(false);
		setBlockUnbreakable();
		destroyTimeForOwner = 3.5F;
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getPlayerRelativeBlockHardness, destroyTimeForOwner, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(IBlockAccess level, BlockPos pos, EntityPlayer player) {
		return ConfigHandler.alwaysDrop || super.canHarvestBlock(level, pos, player);
	}

	@Override
	public float getExplosionResistance(Entity exploder) {
		return Float.MAX_VALUE;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return Float.MAX_VALUE;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer) placer));
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public Material getMaterial(IBlockState state) {
		return Material.ROCK;
	}

	@Override
	public EnumPushReaction getPushReaction(IBlockState state) {
		return EnumPushReaction.NORMAL;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(POWERED) ? 4 : 0;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		boolean newPowered = !state.getValue(POWERED);

		world.setBlockState(pos, state.withProperty(POWERED, newPowered));

		if (newPowered)
			playClickSound(player, world, pos);
		else
			playReleaseSound(world, pos);

		world.markBlockRangeForRenderUpdate(pos, pos);
		notifyNeighbors(world, pos, state.getValue(FACING));
		return true;
	}

	private void notifyNeighbors(World world, BlockPos pos, EnumFacing facing) {
		world.notifyNeighborsOfStateChange(pos, this, false);
		world.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this, false);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
		TileEntity te = world.getTileEntity(pos);

		return te != null && te.receiveClientEvent(id, param);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing facing = state.getValue(FACING);
		boolean isPowered = state.getValue(POWERED);
		float height = (isPowered ? 1 : 2) / 16.0F;

		switch (PanicButtonBlock.SwitchEnumFacing.FACING_LOOKUP[facing.ordinal()]) {
			case 1:
				return new AxisAlignedBB(0.0F, 0.30F, 0.18F, height, 0.70F, 0.82F);
			case 2:
				return new AxisAlignedBB(1.0F - height, 0.30F, 0.18F, 1.0F, 0.70F, 0.82F);
			case 3:
				return new AxisAlignedBB(0.1800F, 0.300F, 0.0F, 0.8150F, 0.700F, height);
			case 4:
				return new AxisAlignedBB(0.1800F, 0.300F, 1.0F - height, 0.8150F, 0.700F, 1.0F);
			case 5:
				return new AxisAlignedBB(0.175F, 0.0F, 0.300F, 0.825F, 0.0F + height, 0.700F);
			case 6:
				return new AxisAlignedBB(0.175F, 1.0F - height, 0.300F, 0.8225F, 1.0F, 0.700F);
			default:
				return super.getBoundingBox(state, source, pos);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new OwnableBlockEntity();
	}

	static final class SwitchEnumFacing {
		static final int[] FACING_LOOKUP = new int[EnumFacing.values().length];

		static {
			try {
				FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 1;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 2;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 3;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 4;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.UP.ordinal()] = 5;
			}
			catch (NoSuchFieldError e) {}

			try {
				FACING_LOOKUP[EnumFacing.DOWN.ordinal()] = 6;
			}
			catch (NoSuchFieldError e) {}
		}

		private SwitchEnumFacing() {}
	}

	@Override
	protected void playClickSound(EntityPlayer player, World world, BlockPos pos) {
		world.playSound(player, new BlockPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.4D), SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
	}

	@Override
	protected void playReleaseSound(World world, BlockPos pos) {
		for (EntityPlayer player : world.playerEntities) {
			world.playSound(player, new BlockPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
		}
	}
}
