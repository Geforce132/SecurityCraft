package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
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
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemAdminTool extends Item {

	public ItemAdminTool() {
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1).maxStackSize(1));
	}

	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		EntityPlayer player = ctx.getPlayer();
		if(!world.isRemote && ServerConfig.CONFIG.allowAdminTool.get()) {
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
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:adminTool.name"), ((TileEntitySecretSign)te).signText[i].getUnformattedComponentText(), TextFormatting.DARK_PURPLE);
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
