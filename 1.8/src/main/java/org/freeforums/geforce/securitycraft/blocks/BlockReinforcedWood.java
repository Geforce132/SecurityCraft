package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class BlockReinforcedWood extends BlockOwnable implements IHelpInfo {
	
    public BlockReinforcedWood()
    {
        super(Material.wood);
        this.setBlockUnbreakable();
        this.setResistance(1000F);
        this.setStepSound(Block.soundTypeWood);
        this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
    }

	public String getHelpInfo() {
		return "Reinforced wood planks are indestructible wood blocks. Only the person who placed who placed them down can destroy them using the Universal Block Remover.";
	}

	public String[] getRecipe() {
		return new String[]{"Reinforced wood planks require: 4 iron ingots, 1 plank", " X ", "XYX", " X ", "X = iron ingot, Y = plank"};
	}

}
