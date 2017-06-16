package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ItemKeycardBase extends Item{
	
	private final int level;

	public ItemKeycardBase(int level) {
		this.level = level;
        this.setMaxDamage(0);
		this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}
	
	public int getKeycardLV(ItemStack par1ItemStack){
		if(level == 0){
			return 1;
		}else if(level == 1){
			return 2;
		}else if(level == 2){
			return 3;
		}else if(level == 3){
			return 6;
		}else if(level == 4){
			return 4;
		}else if(level == 5){
			return 5;
		}else{
			return 0;
		}
	}
    
    @Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {		
		if(level == 3){		
			if(par1ItemStack.getTagCompound() == null){
				par1ItemStack.setTagCompound(new NBTTagCompound());
				par1ItemStack.getTagCompound().setInteger("Uses", 5);
			}
			
			par3List.add(I18n.format("tooltip.keycard.uses") + " " + par1ItemStack.getTagCompound().getInteger("Uses"));			
			
		}
	}
	
}
