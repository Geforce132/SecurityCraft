package net.geforcemods.securitycraft.misc;

import static net.geforcemods.securitycraft.gui.GuiHandler.BLOCK_REINFORCER;
import static net.geforcemods.securitycraft.gui.GuiHandler.BRIEFCASE;
import static net.geforcemods.securitycraft.gui.GuiHandler.CAMERA_MONITOR;
import static net.geforcemods.securitycraft.gui.GuiHandler.DISGUISE_MODULE;
import static net.geforcemods.securitycraft.gui.GuiHandler.IMS;
import static net.geforcemods.securitycraft.gui.GuiHandler.INSERT_PASSWORD;
import static net.geforcemods.securitycraft.gui.GuiHandler.MANUAL;
import static net.geforcemods.securitycraft.gui.GuiHandler.MODULES;
import static net.geforcemods.securitycraft.gui.GuiHandler.MRAT;
import static net.geforcemods.securitycraft.gui.GuiHandler.SETUP_KEYCARD_READER;
import static net.geforcemods.securitycraft.gui.GuiHandler.SETUP_PASSWORD;
import static net.geforcemods.securitycraft.gui.GuiHandler.USERNAME_LOGGER;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.BriefcaseInventory;
import net.geforcemods.securitycraft.containers.ContainerBlockReinforcer;
import net.geforcemods.securitycraft.containers.ContainerBriefcase;
import net.geforcemods.securitycraft.containers.ContainerDisguiseModule;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.containers.ModuleInventory;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class BaseInteractionObject implements IInteractionObject
{
	protected final ResourceLocation id;

	public BaseInteractionObject(ResourceLocation id)
	{
		this.id = id;
	}

	@Override
	public ITextComponent getName()
	{
		return new StringTextComponent(id.toString());
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public ITextComponent getCustomName()
	{
		return null;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, PlayerEntity player)
	{
		if(id.equals(SETUP_KEYCARD_READER) || id.equals(MRAT) || id.equals(USERNAME_LOGGER) || id.equals(SETUP_PASSWORD) || id.equals(INSERT_PASSWORD) || id.equals(IMS) || id.equals(MODULES))
			return new ContainerGeneric();
		else if(id.equals(CAMERA_MONITOR) && PlayerUtils.isHoldingItem(player, SCContent.cameraMonitor))
			return new ContainerGeneric();
		else if(id.equals(BRIEFCASE) && PlayerUtils.isHoldingItem(player, SCContent.briefcase))
			return new ContainerBriefcase(player, player.inventory, new BriefcaseInventory(player.inventory.getCurrentItem()));
		else if(id.equals(DISGUISE_MODULE) && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canBeCustomized())
			return new ContainerDisguiseModule(player, player.inventory, new ModuleInventory(player.inventory.getCurrentItem()));
		else if(id.equals(BLOCK_REINFORCER))
			return new ContainerBlockReinforcer(player, player.inventory);
		else if(id.equals(MANUAL))
			return new ContainerGeneric();

		return new ContainerGeneric();
	}

	@Override
	public String getGuiID()
	{
		return id.toString();
	}
}
