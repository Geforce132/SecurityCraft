package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockKeypadFrame extends BlockOwnable
{
	@SideOnly(Side.CLIENT)
	private IIcon keypadFrameFront;

	public BlockKeypadFrame(Material m)
	{
		super(m);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int i, int k)
	{
		if(i == 3 && k == 0)
			return keypadFrameFront;
		
    	if(k == 7 || k == 8 || k == 9 || k == 10)
    		return i == 1 ? blockIcon : (i == 0 ? blockIcon : (i != (k - 5) ? blockIcon : keypadFrameFront));
    	else
    		return i == 1 ? blockIcon : (i == 0 ? blockIcon : (i != k ? blockIcon : keypadFrameFront));
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg)
	{
		blockIcon = reg.registerIcon("minecraft:stone");
		keypadFrameFront = reg.registerIcon("securitycraft:keypadFrameFront");
	}
	
    public void onBlockAdded(World world, int i, int k, int l)
    {
    	super.onBlockAdded(world, i, k, l);
    }
	
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase elb, ItemStack stack)
    {
        int l = MathHelper.floor_double((double)(elb.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0)
            world.setBlockMetadataWithNotify(i, j, k, 2, 2);

        if (l == 1)
            world.setBlockMetadataWithNotify(i, j, k, 5, 2);

        if (l == 2)
            world.setBlockMetadataWithNotify(i, j, k, 3, 2);

        if (l == 3)
            world.setBlockMetadataWithNotify(i, j, k, 4, 2);
    }
}
