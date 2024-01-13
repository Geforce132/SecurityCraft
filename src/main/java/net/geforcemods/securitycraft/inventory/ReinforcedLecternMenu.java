package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ReinforcedLecternBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.LecternContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedLecternMenu extends LecternContainer {
	public final ReinforcedLecternBlockEntity be;

	public ReinforcedLecternMenu(int id, World level, BlockPos pos) {
		super(id);
		this.be = (ReinforcedLecternBlockEntity) level.getBlockEntity(pos);
	}

	public ReinforcedLecternMenu(int id, ReinforcedLecternBlockEntity be) {
		super(id, be.bookAccess, be.dataAccess);
		this.be = be;
	}

	@Override
	public boolean clickMenuButton(PlayerEntity player, int id) {
		//while the respective buttons are removed in the screen clientside, the server should still prevent any attempts by unallowed clients at using their functionality
		if (!be.isOwnedBy(player) && (id == 3 || be.isPageLocked() && (id == 1 || id == 2)))
			return false;

		return super.clickMenuButton(player, id);
	}

	@Override
	public ContainerType<?> getType() {
		return SCContent.REINFORCED_LECTERN_MENU.get();
	}
}
