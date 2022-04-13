package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blocks.AbstractKeypadFurnaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeypadSmokerMenu extends AbstractFurnaceContainer {
	public AbstractKeypadFurnaceBlockEntity te;
	private IWorldPosCallable worldPosCallable;

	public KeypadSmokerMenu(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
		this(windowId, world, pos, inventory, (AbstractKeypadFurnaceBlockEntity) world.getBlockEntity(pos), ((AbstractKeypadFurnaceBlockEntity) world.getBlockEntity(pos)).getFurnaceData());
	}

	public KeypadSmokerMenu(int windowId, World world, BlockPos pos, PlayerInventory inventory, IInventory furnaceInv, IIntArray furnaceData) {
		super(SCContent.KEYPAD_SMOKER_MENU.get(), IRecipeType.SMOKING, RecipeBookCategory.SMOKER, windowId, inventory, furnaceInv, furnaceData);
		te = (AbstractKeypadFurnaceBlockEntity) world.getBlockEntity(pos);
		worldPosCallable = IWorldPosCallable.create(world, pos);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return stillValid(worldPosCallable, player, SCContent.KEYPAD_SMOKER.get());
	}

	@Override
	public void removed(PlayerEntity player) {
		te.getLevel().setBlockAndUpdate(te.getBlockPos(), te.getBlockState().setValue(AbstractKeypadFurnaceBlock.OPEN, false));
	}
}