package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedFenceBlock extends FenceBlock implements IReinforcedBlock {
	private final Block vanillaBlock;

	public ReinforcedFenceBlock(AbstractBlock.Properties properties, Block vanillaBlock) {
		super(properties);
		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));

		if (stack.hasCustomHoverName()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof INameSetter)
				((INameSetter) te).setCustomName(stack.getHoverName());
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new OwnableBlockEntity();
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}
}
