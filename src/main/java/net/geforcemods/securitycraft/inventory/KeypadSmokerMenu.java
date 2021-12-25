package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blocks.AbstractKeypadFurnaceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class KeypadSmokerMenu extends AbstractFurnaceMenu {
	public AbstractKeypadFurnaceBlockEntity be;
	private ContainerLevelAccess worldPosCallable;

	public KeypadSmokerMenu(int windowId, Level level, BlockPos pos, Inventory inventory) {
		this(windowId, level, pos, inventory, (AbstractKeypadFurnaceBlockEntity) level.getBlockEntity(pos), ((AbstractKeypadFurnaceBlockEntity) level.getBlockEntity(pos)).getFurnaceData());
	}

	public KeypadSmokerMenu(int windowId, Level level, BlockPos pos, Inventory inventory, Container furnaceInv, ContainerData furnaceData) {
		super(SCContent.mTypeKeypadSmoker, RecipeType.SMOKING, RecipeBookType.SMOKER, windowId, inventory, furnaceInv, furnaceData);
		this.be = (AbstractKeypadFurnaceBlockEntity) level.getBlockEntity(pos);
		worldPosCallable = ContainerLevelAccess.create(level, pos);
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(worldPosCallable, player, SCContent.KEYPAD_SMOKER.get());
	}

	@Override
	public void removed(Player player) {
		be.getLevel().setBlockAndUpdate(be.getBlockPos(), be.getBlockState().setValue(AbstractKeypadFurnaceBlock.OPEN, false));
	}
}