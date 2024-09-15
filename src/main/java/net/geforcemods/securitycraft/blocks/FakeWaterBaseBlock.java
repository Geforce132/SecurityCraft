package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeWaterBaseBlock extends BlockStaticLiquid implements IOverlayDisplay {
	public FakeWaterBaseBlock(Material material) {
		super(material);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!checkForMixing(world, pos, state))
			updateLiquid(world, pos, state);
	}

	private void updateLiquid(World world, BlockPos pos, IBlockState state) {
		BlockDynamicLiquid liquid = getFlowingBlock(material);

		world.setBlockState(pos, liquid.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
		world.scheduleUpdate(pos, liquid, tickRate(world));
	}

	public static BlockDynamicLiquid getFlowingBlock(Material material) {
		if (material == Material.WATER)
			return (BlockDynamicLiquid) SCContent.bogusWaterFlowing;
		else if (material == Material.LAVA)
			return (BlockDynamicLiquid) SCContent.bogusLavaFlowing;
		else
			throw new IllegalArgumentException("Invalid material");
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (!world.isRemote && !(entity instanceof EntityItem) && !(entity instanceof EntityBoat)) {
			if (!(entity instanceof EntityPlayer) || (!((EntityPlayer) entity).capabilities.isCreativeMode && !(entity.getRidingEntity() instanceof EntityBoat)))
				entity.attackEntityFrom(CustomDamageSources.FAKE_WATER, 4F);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		return new ItemStack(Blocks.WATER);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return false;
	}
}
