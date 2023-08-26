package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedPressurePlateBlock extends BlockPressurePlate implements IReinforcedBlock {
	private final Block vanillaBlock;

	public ReinforcedPressurePlateBlock(Material material, Sensitivity sensitivity, SoundType soundType, Block vanillaBlock, MapColor color) {
		super(material, sensitivity);

		setSoundType(soundType);
		this.vanillaBlock = vanillaBlock;
		blockMapColor = color;
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
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		int redstoneStrength = getRedstoneStrength(state);

		if (!world.isRemote && redstoneStrength == 0 && entity instanceof EntityPlayer) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof AllowlistOnlyBlockEntity && isAllowedToPress((AllowlistOnlyBlockEntity) te, (EntityPlayer) entity))
				updateState(world, pos, state, redstoneStrength);
		}
	}

	@Override
	protected int computeRedstoneStrength(World world, BlockPos pos) {
		AxisAlignedBB aabb = PRESSURE_AABB.offset(pos);
		List<? extends Entity> list;

		list = world.getEntitiesWithinAABBExcludingEntity(null, aabb);

		if (!list.isEmpty()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof AllowlistOnlyBlockEntity) {
				for (Entity entity : list) {
					if (entity instanceof EntityPlayer && isAllowedToPress((AllowlistOnlyBlockEntity) te, (EntityPlayer) entity))
						return 15;
				}
			}
		}

		return 0;
	}

	public boolean isAllowedToPress(AllowlistOnlyBlockEntity te, EntityPlayer entity) {
		return te.isOwnedBy(entity) || te.isAllowed(entity);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof IModuleInventory)
			((IModuleInventory) te).dropAllModules();

		super.breakBlock(world, pos, state);
	}

	@Override
	public EnumPushReaction getPushReaction(IBlockState state) {
		return EnumPushReaction.NORMAL;
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(vanillaBlock);
	}

	@Override
	public int getAmount() {
		return 1;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new AllowlistOnlyBlockEntity();
	}

	public static class DoorActivator implements Function<Object, IDoorActivator>, IDoorActivator {
		//@formatter:off
		private final List<Block> blocks = Arrays.asList(
				SCContent.reinforcedStonePressurePlate,
				SCContent.reinforcedWoodenPressurePlate);
		//@formatter:on

		@Override
		public IDoorActivator apply(Object o) {
			return this;
		}

		@Override
		public boolean isPowering(World world, BlockPos pos, IBlockState state, TileEntity te, EnumFacing direction, int distance) {
			return state.getValue(POWERED) && (distance < 2 || direction == EnumFacing.UP);
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}
