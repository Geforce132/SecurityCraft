package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;

public class OwnableBlock extends Block implements EntityBlock {

	public OwnableBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof Player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (Player)placer));

		if (stack.hasCustomHoverName()) {
			BlockEntity te = world.getBlockEntity(pos);

			if (te instanceof INameable && ((INameable)te).canBeNamed()) {
				((INameable)te).setCustomSCName(stack.getHoverName());
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new OwnableTileEntity();
	}
}
