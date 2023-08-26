package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedIronTrapDoorBlock extends BaseIronTrapDoorBlock implements IReinforcedBlock {
	public ReinforcedIronTrapDoorBlock(Material material) {
		super(material);
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
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.IRON_TRAPDOOR);
	}

	@Override
	public int getAmount() {
		return 1;
	}
}