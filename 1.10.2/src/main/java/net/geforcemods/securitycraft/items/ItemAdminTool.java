package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemAdminTool extends Item {

	public ItemAdminTool() {
		super();
		
		if(mod_SecurityCraft.configHandler.allowAdminTool) {
			this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
		}
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote) {
			if(worldIn.getTileEntity(pos) != null) {
				TileEntity te = worldIn.getTileEntity(pos);
				boolean hasInfo = false;

				if(te instanceof IOwnable) {
					PlayerUtils.sendMessageToPlayer(playerIn, I18n.format("item.adminTool.name"), I18n.format("messages.adminTool.owner.name").replace("#", (((IOwnable) te).getOwner().getName() == null ? "????" : ((IOwnable) te).getOwner().getName())), TextFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(playerIn, I18n.format("item.adminTool.name"), I18n.format("messages.adminTool.owner.uuid").replace("#", (((IOwnable) te).getOwner().getUUID() == null ? "????" : ((IOwnable) te).getOwner().getUUID())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}
				
				if(te instanceof IPasswordProtected) {
					PlayerUtils.sendMessageToPlayer(playerIn, I18n.format("item.adminTool.name"), I18n.format("messages.adminTool.password").replace("#", (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}
				
				if(te instanceof CustomizableSCTE) {
					List<EnumCustomModules> modules = ((CustomizableSCTE) te).getModules();
					
					if(!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(playerIn, I18n.format("item.adminTool.name"), I18n.format("messages.adminTool.equippedModules"), TextFormatting.DARK_PURPLE);
						
						for(EnumCustomModules module : modules) {
							PlayerUtils.sendMessageToPlayer(playerIn, I18n.format("item.adminTool.name"), "-" + module.getName(), TextFormatting.DARK_PURPLE);
						}
						
						hasInfo = true;
					}
				}
				
				if(!hasInfo) {
					PlayerUtils.sendMessageToPlayer(playerIn, I18n.format("item.adminTool.name"), I18n.format("messages.adminTool.noInfo"), TextFormatting.DARK_PURPLE);
				}
				
				return EnumActionResult.FAIL;
			}
			
			PlayerUtils.sendMessageToPlayer(playerIn, I18n.format("item.adminTool.name"), I18n.format("messages.adminTool.noInfo"), TextFormatting.DARK_PURPLE);
		}
		
		return EnumActionResult.FAIL;
	}

}
