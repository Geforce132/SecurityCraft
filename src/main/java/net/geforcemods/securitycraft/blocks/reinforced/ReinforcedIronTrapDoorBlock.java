package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ReinforcedIronTrapDoorBlock extends BaseIronTrapDoorBlock implements IReinforcedBlock {
	public ReinforcedIronTrapDoorBlock(Material material) {
		super(material);
	}

	@Override
	public Material getMaterial(IBlockState state) {
		return convertToVanillaState(state).getMaterial();
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getSoundType(vanillaState, world, pos, entity);
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess level, BlockPos pos) {
		return convertToVanillaState(state).getMapColor(level, pos);
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getHarvestTool(vanillaState);
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().isToolEffective(type, vanillaState);
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getHarvestLevel(vanillaState);
	}

	@Override
	public boolean isTranslucent(IBlockState state) {
		return convertToVanillaState(state).isTranslucent();
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor) {
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos);

		if (hasActiveSCBlock != state.getValue(OPEN)) {
			world.setBlockState(pos, state.withProperty(OPEN, BlockUtils.hasActiveSCBlockNextTo(world, pos)), 2);
			world.markBlockRangeForRenderUpdate(pos, pos);
			playSound((EntityPlayer) null, world, pos, hasActiveSCBlock);
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos);

		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(OPEN, hasActiveSCBlock);
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.IRON_TRAPDOOR);
	}
}