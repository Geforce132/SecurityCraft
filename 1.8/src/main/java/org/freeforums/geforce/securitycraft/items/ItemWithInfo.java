package org.freeforums.geforce.securitycraft.items;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;

import net.minecraft.item.Item;

public class ItemWithInfo extends Item implements IHelpInfo{
	
	private String info;
	private String[] recipe;

	public ItemWithInfo(String info, String[] recipe) {
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
