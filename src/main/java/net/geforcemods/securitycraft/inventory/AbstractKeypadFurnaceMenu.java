package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blocks.AbstractKeypadFurnaceBlock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.gameevent.GameEvent;

public abstract class AbstractKeypadFurnaceMenu extends AbstractFurnaceMenu {
	private final Block furnaceBlock;
	public final AbstractKeypadFurnaceBlockEntity be;
	private final ContainerLevelAccess worldPosCallable;

	protected AbstractKeypadFurnaceMenu(MenuType<?> menuType, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookType recipeBookType, Block furnaceBlock, int windowId, Inventory inventory, AbstractKeypadFurnaceBlockEntity be) {
		super(menuType, recipeType, recipeBookType, windowId, inventory, be, be.getFurnaceData());

		this.furnaceBlock = furnaceBlock;
		this.be = be;
		worldPosCallable = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(worldPosCallable, player, furnaceBlock);
	}

	@Override
	public void removed(Player player) {
		worldPosCallable.execute((level, pos) -> {
			level.levelEvent(player, LevelEvent.SOUND_CLOSE_IRON_DOOR, pos, 0);
			level.gameEvent(player, GameEvent.CONTAINER_CLOSE, pos);
			level.setBlockAndUpdate(pos, be.getBlockState().setValue(AbstractKeypadFurnaceBlock.OPEN, false));
		});
	}
}
