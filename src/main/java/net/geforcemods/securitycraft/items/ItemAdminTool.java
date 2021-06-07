package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemAdminTool extends Item {

	public ItemAdminTool() {
		if(ConfigHandler.allowAdminTool)
			setCreativeTab(SecurityCraft.tabSCTechnical);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if(!world.isRemote && ConfigHandler.allowAdminTool) {
			if(!player.isCreative())
			{
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:adminTool.name"), Utils.localize("messages.securitycraft:adminTool.needCreative"), TextFormatting.DARK_PURPLE);
				return EnumActionResult.SUCCESS;
			}

			EnumActionResult briefcaseResult = handleBriefcase(player, hand).getType();

			if(briefcaseResult != EnumActionResult.PASS)
				return briefcaseResult;

			TileEntity te = world.getTileEntity(pos);

			if(te != null) {
				boolean hasInfo = false;

				if(te instanceof IOwnable) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:adminTool.name"), Utils.localize("messages.securitycraft:adminTool.owner.name", (((IOwnable) te).getOwner().getName() == null ? "????" : ((IOwnable) te).getOwner().getName())), TextFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:adminTool.name"), Utils.localize("messages.securitycraft:adminTool.owner.uuid", (((IOwnable) te).getOwner().getUUID() == null ? "????" : ((IOwnable) te).getOwner().getUUID())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IPasswordProtected) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:adminTool.name"), Utils.localize("messages.securitycraft:adminTool.password", (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IModuleInventory) {
					List<EnumModuleType> modules = ((IModuleInventory) te).getInsertedModules();

					if(!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:adminTool.name"), Utils.localize("messages.securitycraft:adminTool.equippedModules"), TextFormatting.DARK_PURPLE);

						for(EnumModuleType module : modules)
							PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:adminTool.name"), new TextComponentString("- ").appendSibling(Utils.localize(module.getTranslationKey())), TextFormatting.DARK_PURPLE);

						hasInfo = true;
					}
				}

				if(te instanceof TileEntitySecretSign)
				{
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:adminTool.name"), new TextComponentString(""), TextFormatting.DARK_PURPLE);

					for(int i = 0; i < 4; i++)
					{
						PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:adminTool.name"), ((TileEntitySecretSign)te).signText[i], TextFormatting.DARK_PURPLE);
					}

					hasInfo = true;
				}

				if(!hasInfo)
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:adminTool.name"), Utils.localize("messages.securitycraft:adminTool.noInfo"), TextFormatting.DARK_PURPLE);

				return EnumActionResult.SUCCESS;
			}

			PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:adminTool.name"), Utils.localize("messages.securitycraft:adminTool.noInfo"), TextFormatting.DARK_PURPLE);
		}
		else {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:adminTool.name"), Utils.localize("messages.securitycraft:adminTool.disabled"), TextFormatting.DARK_PURPLE);
		}

		return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(!player.isCreative())
		{
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey()), Utils.localize("messages.securitycraft:adminTool.needCreative"), TextFormatting.DARK_PURPLE);
			return ActionResult.newResult(EnumActionResult.FAIL, player.getHeldItem(hand));
		}
		else return handleBriefcase(player, hand);
	}

	private ActionResult<ItemStack> handleBriefcase(EntityPlayer player, EnumHand hand)
	{
		ItemStack adminTool = player.getHeldItem(hand);

		if (hand == EnumHand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.briefcase) {
			ItemStack briefcase = player.getHeldItemOffhand();
			TextComponentTranslation adminToolName = Utils.localize(getTranslationKey());
			String ownerName = ItemBriefcase.getOwnerName(briefcase);
			String ownerUUID = ItemBriefcase.getOwnerUUID(briefcase);

			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.name", ownerName.isEmpty() ? "????" : ownerName), TextFormatting.DARK_PURPLE);
			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.uuid", ownerUUID.isEmpty() ? "????" : ownerUUID), TextFormatting.DARK_PURPLE);
			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.password", briefcase.hasTagCompound() ? briefcase.getTagCompound().getString("passcode") : "????"), TextFormatting.DARK_PURPLE);
			return ActionResult.newResult(EnumActionResult.SUCCESS, adminTool);
		}

		return ActionResult.newResult(EnumActionResult.PASS, adminTool);
	}
}
