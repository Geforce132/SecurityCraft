package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.material.Material;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;

public class BlockOwnableWithInfo extends BlockOwnable implements IHelpInfo {

	private String helpInfo;
	private String[] recipe;
	
	public BlockOwnableWithInfo(Material par1, String helpInfo, String[] recipe) {
		super(par1);
		this.helpInfo = helpInfo;
		this.recipe = recipe;
	}

	public String getHelpInfo() {
		return helpInfo;
	}

	public String[] getRecipe() {
		return recipe;
	}

}
