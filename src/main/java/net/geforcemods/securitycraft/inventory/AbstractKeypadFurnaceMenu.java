package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blocks.AbstractKeypadFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.util.Constants;

public class AbstractKeypadFurnaceMenu extends AbstractFurnaceContainer {
	private final Block furnaceBlock;
	public AbstractKeypadFurnaceBlockEntity te;
	private IWorldPosCallable worldPosCallable;

	protected AbstractKeypadFurnaceMenu(ContainerType<?> menuType, IRecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookCategory recipeBookType, Block furnaceBlock, int windowId, PlayerInventory inventory, AbstractKeypadFurnaceBlockEntity be) {
		super(menuType, recipeType, recipeBookType, windowId, inventory, be, be.getFurnaceData());

		this.furnaceBlock = furnaceBlock;
		te = be;
		worldPosCallable = IWorldPosCallable.create(be.getLevel(), be.getBlockPos());
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return stillValid(worldPosCallable, player, furnaceBlock);
	}

	@Override
	public void removed(PlayerEntity player) {
		worldPosCallable.execute((level, pos) -> {
			level.levelEvent(player, Constants.WorldEvents.IRON_DOOR_CLOSE_SOUND, pos, 0);
			level.setBlockAndUpdate(pos, te.getBlockState().setValue(AbstractKeypadFurnaceBlock.OPEN, false));
		});
	}
}
