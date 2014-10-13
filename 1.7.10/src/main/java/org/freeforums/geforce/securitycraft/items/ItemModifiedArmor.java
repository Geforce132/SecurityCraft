package org.freeforums.geforce.securitycraft.items;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemModifiedArmor extends ItemArmor {

	public ItemModifiedArmor(ItemArmor.ArmorMaterial par2EnumArmorMaterial, int par3, int par4) {
		super(par2EnumArmorMaterial, par3, par4);
	}
	
	public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, int layer)
	{
		//int i = itemstack.itemID;
		//Apply armor textures.
//		if ((i == mod_SecretAgent.bowlerHat.itemID) || (i == mod_SecretAgent.tuxedo.itemID) || (i == mod_SecretAgent.tuxedoShoes.itemID))
//			return "mod_SecretAgent:textures/models/armor/tuxedo_layer_1.png";
//		if (i == mod_SecretAgent.tuxedoPants.itemID)
//			return "mod_SecretAgent:textures/models/armor/tuxedo_layer_2.png";
//		
//		if ((i == mod_SecretAgent.leatherBowlerHat.itemID) || (i == mod_SecretAgent.leatherTuxedo.itemID) || (i == mod_SecretAgent.leatherTuxedoShoes.itemID))
//			return "mod_SecretAgent:textures/models/armor/leathertuxedo_layer_1.png";
//		if (i == mod_SecretAgent.leatherTuxedoPants.itemID)
//			return "mod_SecretAgent:textures/models/armor/leathertuxedo_layer_2.png";
//		
//		if ((i == mod_SecretAgent.ironBowlerHat.itemID) || (i == mod_SecretAgent.ironTuxedo.itemID) || (i == mod_SecretAgent.ironTuxedoShoes.itemID))
//			return "mod_SecretAgent:textures/models/armor/irontuxedo_layer_1.png";
//		if (i == mod_SecretAgent.ironTuxedoPants.itemID)
//			return "mod_SecretAgent:textures/models/armor/irontuxedo_layer_2.png";
//		
//		if ((i == mod_SecretAgent.goldBowlerHat.itemID) || (i == mod_SecretAgent.goldTuxedo.itemID) || (i == mod_SecretAgent.goldTuxedoShoes.itemID))
//			return "mod_SecretAgent:textures/models/armor/goldtuxedo_layer_1.png";
//		if (i == mod_SecretAgent.goldTuxedoPants.itemID)
//			return "mod_SecretAgent:textures/models/armor/goldtuxedo_layer_2.png";
//		
//		if ((i == mod_SecretAgent.chainBowlerHat.itemID) || (i == mod_SecretAgent.chainTuxedo.itemID) || (i == mod_SecretAgent.chainTuxedoShoes.itemID))
//			return "mod_SecretAgent:textures/models/armor/chaintuxedo_layer_1.png";
//		if (i == mod_SecretAgent.chainTuxedoPants.itemID)
//			return "mod_SecretAgent:textures/models/armor/chaintuxedo_layer_2.png";
//		
//		if ((i == mod_SecretAgent.diamondBowlerHat.itemID) || (i == mod_SecretAgent.diamondTuxedo.itemID) || (i == mod_SecretAgent.diamondTuxedoShoes.itemID))
//			return "mod_SecretAgent:textures/models/armor/diamondtuxedo_layer_1.png";
//		if (i == mod_SecretAgent.diamondTuxedoPants.itemID)
//			return "mod_SecretAgent:textures/models/armor/diamondtuxedo_layer_2.png";
//		
//		if (i == mod_SecretAgent.miniAirCanister.itemID)
//			return "mod_SecretAgent:textures/models/armor/miniaircanister_layer_1.png";
//		
//		if (i == mod_SecretAgent.nightVisionGoggles.itemID)
//			return "mod_SecretAgent:textures/models/armor/nightvisiongoggles_layer_1.png";
//		
//		if (i == mod_SecretAgent.jetpack.itemID)
//			return "mod_SecretAgent:textures/models/armor/jetpack_layer_1.png";
//		
//		return "mod_SecretAgent:textures/models/armor/tuxedo_layer_1.png";
		return "textures/models/armor/invisible.png";
		
	}

}
