package net.geforcemods.securitycraft.misc;

import static net.geforcemods.securitycraft.gui.GuiHandler.CUSTOMIZE_BLOCK;
import static net.geforcemods.securitycraft.gui.GuiHandler.EDIT_SECRET_SIGN;
import static net.geforcemods.securitycraft.gui.GuiHandler.INVENTORY_SCANNER;
import static net.geforcemods.securitycraft.gui.GuiHandler.KEYPAD_FURNACE;
import static net.geforcemods.securitycraft.gui.GuiHandler.KEY_CHANGER;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.containers.ContainerCustomizeBlock;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.containers.ContainerInventoryScanner;
import net.geforcemods.securitycraft.containers.ContainerKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TEInteractionObject extends BaseInteractionObject
{
	private final World world;
	private final BlockPos pos;

	public TEInteractionObject(ResourceLocation id, World world, BlockPos pos)
	{
		super(id);
		this.world = world;
		this.pos = pos;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player)
	{
		TileEntity te = world.getTileEntity(pos);

		if(id.equals(INVENTORY_SCANNER) && te instanceof TileEntityInventoryScanner)
			return new ContainerInventoryScanner(player.inventory, (TileEntityInventoryScanner)te);
		else if(id.equals(KEYPAD_FURNACE) && te instanceof TileEntityKeypadFurnace)
			return new ContainerKeypadFurnace(player.inventory, (TileEntityKeypadFurnace)te);
		else if(id.equals(KEY_CHANGER) && te != null && PlayerUtils.isHoldingItem(player, SCContent.universalKeyChanger))
			return new ContainerGeneric();
		else if(id.equals(CUSTOMIZE_BLOCK) && te instanceof CustomizableSCTE)
			return new ContainerCustomizeBlock(player.inventory, (CustomizableSCTE)te);
		else if(id.equals(EDIT_SECRET_SIGN))
			return new ContainerGeneric();

		return super.createContainer(playerInventory, player);
	}
}
