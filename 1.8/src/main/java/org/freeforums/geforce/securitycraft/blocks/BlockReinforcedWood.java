package org.freeforums.geforce.securitycraft.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedWood extends BlockOwnable {
	
    public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 5);

    public BlockReinforcedWood(){
        super(Material.wood);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, 0));
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list){
        for(int i = 0; i < 6; i++){
            list.add(new ItemStack(itemIn, 1, i));
        }
    }
    
    public int damageDropped(IBlockState state)
    {
        return ((Integer) state.getValue(VARIANT)).intValue();
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, meta);
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(VARIANT)).intValue();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {VARIANT});
    }

}
