package net.geforcemods.securitycraft.misc;

import java.util.function.BooleanSupplier;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.item.Item;

public class SCManualPage {

	private Item item;
	private String helpInfo;
	private BooleanSupplier configValue = () -> true;
	private String designedBy = "";

	public SCManualPage(Item item, String helpInfo){
		this.item = item;
		this.helpInfo = helpInfo;

		if(item == SCContent.KEYCARD_LVL_1.get()) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard1.get();}
		else if(item == SCContent.KEYCARD_LVL_2.get()) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard2.get();}
		else if(item == SCContent.KEYCARD_LVL_3.get()) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard3.get();}
		else if(item == SCContent.KEYCARD_LVL_4.get()) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard4.get();}
		else if(item == SCContent.KEYCARD_LVL_5.get()) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard5.get();}
		else if(item == SCContent.LIMITED_USE_KEYCARD.get())
			configValue = () -> ConfigHandler.CONFIG.ableToCraftLUKeycard.get();
	}

	public Item getItem() {
		return item;
	}

	public String getHelpInfo() {
		return helpInfo;
	}

	public boolean isRecipeDisabled()
	{
		return !configValue.getAsBoolean();
	}

	public void setDesignedBy(String designedBy)
	{
		this.designedBy = designedBy;
	}

	public String getDesignedBy()
	{
		return designedBy;
	}
}
