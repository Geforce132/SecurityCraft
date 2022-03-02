package net.geforcemods.securitycraft.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GenericBEMenu extends AbstractContainerMenu {
	public final BlockEntity be;
	private ContainerLevelAccess worldPosCallable;

	public GenericBEMenu(MenuType<GenericBEMenu> type, int windowId, Level level, BlockPos pos) {
		super(type, windowId);

		be = level.getBlockEntity(pos);
		worldPosCallable = ContainerLevelAccess.create(level, pos);
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(worldPosCallable, player, be.getBlockState().getBlock());
	}
}
