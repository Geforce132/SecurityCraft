package org.freeforums.geforce.securitycraft.items;

import net.minecraft.item.Item;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;

public class ItemWithInfo extends Item implements IHelpInfo {
	
	private String info;
	private String[] recipe;
	
	public ItemWithInfo(String info, String[] recipe){
		super();
		this.info = info;
		this.recipe = recipe;
	}

	public String getHelpInfo() {
		return info;
	}

	public String[] getRecipe() {
		return recipe;
	}

}
