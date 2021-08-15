package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
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

public class KeypadFurnaceMenu extends AbstractFurnaceMenu{

	public KeypadFurnaceBlockEntity te;
	private ContainerLevelAccess worldPosCallable;

	public KeypadFurnaceMenu(int windowId, Level world, BlockPos pos, Inventory inventory) {
		this(windowId, world, pos, inventory, (KeypadFurnaceBlockEntity)world.getBlockEntity(pos), ((KeypadFurnaceBlockEntity)world.getBlockEntity(pos)).getFurnaceData());
	}

	public KeypadFurnaceMenu(int windowId, Level world, BlockPos pos, Inventory inventory, Container furnaceInv, ContainerData furnaceData) {
		super(SCContent.mTypeKeypadFurnace, RecipeType.SMELTING, RecipeBookType.FURNACE, windowId, inventory, furnaceInv, furnaceData);
		this.te = (KeypadFurnaceBlockEntity)world.getBlockEntity(pos);
		worldPosCallable = ContainerLevelAccess.create(world, pos);
	}

	@Override
	public boolean stillValid(Player player){
		return stillValid(worldPosCallable, player, SCContent.KEYPAD_FURNACE.get());
	}

	@Override
	public void removed(Player player)
	{
		te.getLevel().setBlockAndUpdate(te.getBlockPos(), te.getBlockState().setValue(KeypadFurnaceBlock.OPEN, false));
	}
}