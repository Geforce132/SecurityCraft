package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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

		if(ConfigHandler.allowAdminTool)
			setCreativeTab(SecurityCraft.tabSCTechnical);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!world.isRemote && ConfigHandler.allowAdminTool) {
			if(world.getTileEntity(pos) != null) {
				TileEntity te = world.getTileEntity(pos);
				boolean hasInfo = false;

				if(te instanceof IOwnable) {
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:adminTool.name"), ClientUtils.localize("messages.securitycraft:adminTool.owner.name").replace("#", (((IOwnable) te).getOwner().getName() == null ? "????" : ((IOwnable) te).getOwner().getName())), TextFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:adminTool.name"), ClientUtils.localize("messages.securitycraft:adminTool.owner.uuid").replace("#", (((IOwnable) te).getOwner().getUUID() == null ? "????" : ((IOwnable) te).getOwner().getUUID())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IPasswordProtected) {
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:adminTool.name"), ClientUtils.localize("messages.securitycraft:adminTool.password").replace("#", (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof CustomizableSCTE) {
					List<EnumCustomModules> modules = ((CustomizableSCTE) te).getModules();

					if(!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:adminTool.name"), ClientUtils.localize("messages.securitycraft:adminTool.equippedModules"), TextFormatting.DARK_PURPLE);

						for(EnumCustomModules module : modules)
							PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:adminTool.name"), "-" + module.getName(), TextFormatting.DARK_PURPLE);

						hasInfo = true;
					}
				}

				if(te instanceof TileEntitySecretSign)
				{
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:adminTool.name"), "", TextFormatting.DARK_PURPLE);

					for(int i = 0; i < 4; i++)
					{
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:adminTool.name"), ((TileEntitySecretSign)te).signText[i].getUnformattedText(), TextFormatting.DARK_PURPLE);
					}

					hasInfo = true;
				}

				if(!hasInfo)
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:adminTool.name"), ClientUtils.localize("messages.securitycraft:adminTool.noInfo"), TextFormatting.DARK_PURPLE);

				return EnumActionResult.FAIL;
			}

			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:adminTool.name"), ClientUtils.localize("messages.securitycraft:adminTool.noInfo"), TextFormatting.DARK_PURPLE);
		}

		return EnumActionResult.FAIL;
	}

}
