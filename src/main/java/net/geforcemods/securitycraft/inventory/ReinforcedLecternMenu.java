package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ReinforcedLecternBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;

public class ReinforcedLecternMenu extends LecternMenu {
	public final ReinforcedLecternBlockEntity be;

	public ReinforcedLecternMenu(int id, Level level, BlockPos pos) {
		super(id);
		this.be = (ReinforcedLecternBlockEntity) level.getBlockEntity(pos);
	}

	public ReinforcedLecternMenu(int id, ReinforcedLecternBlockEntity be) {
		super(id, be.bookAccess, be.dataAccess);
		this.be = be;
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		//while the respective buttons are removed in the screen clientside, the server should still prevent any attempts by unallowed clients at using their functionality
		if (!be.isOwnedBy(player) && (id == LecternMenu.BUTTON_TAKE_BOOK || be.isPageLocked() && (id == LecternMenu.BUTTON_PREV_PAGE || id == LecternMenu.BUTTON_NEXT_PAGE)))
			return false;

		return super.clickMenuButton(player, id);
	}

	@Override
	public MenuType<?> getType() {
		return SCContent.REINFORCED_LECTERN_MENU.get();
	}
}
