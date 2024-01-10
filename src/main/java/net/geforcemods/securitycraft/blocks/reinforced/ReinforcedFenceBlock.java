package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.OwnableFenceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedFenceBlock extends OwnableFenceBlock implements IReinforcedBlock {
	private final Block vanillaBlock;

	public ReinforcedFenceBlock(MapColor mapColor, Block vanillaBlock) {
		this(Material.WOOD, mapColor, vanillaBlock);
		setSoundType(SoundType.WOOD);
	}

	public ReinforcedFenceBlock(Material material, MapColor mapColor, Block vanillaBlock) {
		super(material, mapColor);
		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public boolean eventReceived(IBlockState state, World level, BlockPos pos, int id, int param) {
		TileEntity be = level.getTileEntity(pos);

		return be != null && be.receiveClientEvent(id, param);
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(vanillaBlock);
	}
}
