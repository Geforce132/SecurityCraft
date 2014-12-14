package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class BlockReinforcedWood extends BlockOwnable {
	
    public BlockReinforcedWood()
    {
        super(Material.wood);
        this.setBlockUnbreakable();
        this.setResistance(1000F);
        this.setStepSound(Block.soundTypeWood);
        this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
    }

}
