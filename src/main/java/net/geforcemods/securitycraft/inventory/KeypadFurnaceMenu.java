package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraftforge.common.util.Constants;

public class KeypadFurnaceMenu extends ContainerFurnace {
	private KeypadFurnaceBlockEntity te;

	public KeypadFurnaceMenu(InventoryPlayer player, KeypadFurnaceBlockEntity te) {
		super(player, te);
		this.te = te;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return BlockUtils.isWithinUsableDistance(te.getWorld(), te.getPos(), player, SCContent.keypadFurnace);
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		te.getWorld().playEvent(null, Constants.WorldEvents.IRON_DOOR_CLOSE_SOUND, te.getPos(), 0);
		te.getWorld().setBlockState(te.getPos(), te.getWorld().getBlockState(te.getPos()).withProperty(KeypadFurnaceBlock.OPEN, false));
	}
}