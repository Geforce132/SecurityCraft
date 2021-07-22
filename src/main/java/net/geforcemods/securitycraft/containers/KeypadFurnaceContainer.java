package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
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

public class KeypadFurnaceContainer extends AbstractFurnaceMenu{

	public KeypadFurnaceTileEntity te;
	private ContainerLevelAccess worldPosCallable;

	public KeypadFurnaceContainer(int windowId, Level world, BlockPos pos, Inventory inventory) {
		this(windowId, world, pos, inventory, (KeypadFurnaceTileEntity)world.getBlockEntity(pos), ((KeypadFurnaceTileEntity)world.getBlockEntity(pos)).getFurnaceData());
	}

	public KeypadFurnaceContainer(int windowId, Level world, BlockPos pos, Inventory inventory, Container furnaceInv, ContainerData furnaceData) {
		super(SCContent.cTypeKeypadFurnace, RecipeType.SMELTING, RecipeBookType.FURNACE, windowId, inventory, furnaceInv, furnaceData);
		this.te = (KeypadFurnaceTileEntity)world.getBlockEntity(pos);
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