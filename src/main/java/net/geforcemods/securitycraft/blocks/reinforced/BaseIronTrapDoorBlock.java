package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BaseIronTrapDoorBlock extends TrapDoorBlock {
	public BaseIronTrapDoorBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));

		if (stack.hasCustomHoverName()) {
			TileEntity be = level.getBlockEntity(pos);

			if (be instanceof INameSetter)
				((INameSetter) be).setCustomName(stack.getHoverName());
		}
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return ActionResultType.FAIL;
	}

	//here for making it accessible without AT
	@Override
	public void playSound(PlayerEntity player, World level, BlockPos pos, boolean isOpened) {
		super.playSound(player, level, pos, isOpened);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		super.onRemove(state, level, pos, newState, isMoving);

		if (!(newState.getBlock() instanceof BaseIronTrapDoorBlock))
			level.removeBlockEntity(pos);
	}

	@Override
	public boolean triggerEvent(BlockState state, World level, BlockPos pos, int id, int param) {
		TileEntity be = level.getBlockEntity(pos);

		return be != null && be.triggerEvent(id, param);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new OwnableBlockEntity();
	}
}