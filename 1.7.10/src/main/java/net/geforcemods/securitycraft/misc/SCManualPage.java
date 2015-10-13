package net.geforcemods.securitycraft.misc;

import net.minecraft.item.Item;

public class SCManualPage {
	
	private Item item;
	private String itemName;
	private String helpInfo;
	
	public SCManualPage(Item item, String itemName, String helpInfo){
		this.item = item;
		this.itemName = itemName;
		this.helpInfo = helpInfo;
	}

	public Item getItem() {
		return item;
	}

	public String getItemName() {
		return itemName;
	}

	public String getHelpInfo() {
		return helpInfo;
	}

}
