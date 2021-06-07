package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class AdminToolItem extends Item {

	public AdminToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		PlayerEntity player = ctx.getPlayer();
		TranslationTextComponent adminToolName = Utils.localize(getTranslationKey());

		if(!world.isRemote && ConfigHandler.SERVER.allowAdminTool.get()) {
			if(!player.isCreative())
			{
				PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.needCreative"), TextFormatting.DARK_PURPLE);
				return ActionResultType.FAIL;
			}

			ActionResultType briefcaseResult = handleBriefcase(player, ctx.getHand()).getType();

			if(briefcaseResult != ActionResultType.PASS)
				return briefcaseResult;

			TileEntity te = world.getTileEntity(pos);

			if(te != null) {
				boolean hasInfo = false;

				if(te instanceof IOwnable) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.name", (((IOwnable) te).getOwner().getName() == null ? "????" : ((IOwnable) te).getOwner().getName())), TextFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.uuid", (((IOwnable) te).getOwner().getUUID() == null ? "????" : ((IOwnable) te).getOwner().getUUID())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IPasswordProtected) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.password", (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IModuleInventory) {
					List<ModuleType> modules = ((IModuleInventory) te).getInsertedModules();

					if(!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.equippedModules"), TextFormatting.DARK_PURPLE);

						for(ModuleType module : modules)
							PlayerUtils.sendMessageToPlayer(player, adminToolName, new StringTextComponent("- ").appendSibling(new TranslationTextComponent(module.getTranslationKey())), TextFormatting.DARK_PURPLE);

						hasInfo = true;
					}
				}

				if(te instanceof SecretSignTileEntity)
				{
					PlayerUtils.sendMessageToPlayer(player, adminToolName, new StringTextComponent(""), TextFormatting.DARK_PURPLE);

					for(int i = 0; i < 4; i++)
					{
						PlayerUtils.sendMessageToPlayer(player, adminToolName, ((SecretSignTileEntity)te).signText[i], TextFormatting.DARK_PURPLE);
					}

					hasInfo = true;
				}

				if(!hasInfo)
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.noInfo"), TextFormatting.DARK_PURPLE);

				return ActionResultType.SUCCESS;
			}

			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.noInfo"), TextFormatting.DARK_PURPLE);
		}
		else {
			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.disabled"), TextFormatting.DARK_PURPLE);
		}

		return ActionResultType.FAIL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		if(!player.isCreative())
		{
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey()), Utils.localize("messages.securitycraft:adminTool.needCreative"), TextFormatting.DARK_PURPLE);
			return ActionResult.resultFail(player.getHeldItem(hand));
		}
		else return handleBriefcase(player, hand);
	}

	private ActionResult<ItemStack> handleBriefcase(PlayerEntity player, Hand hand)
	{
		ItemStack adminTool = player.getHeldItem(hand);

		if (hand == Hand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.BRIEFCASE.get()) {
			ItemStack briefcase = player.getHeldItemOffhand();
			TranslationTextComponent adminToolName = Utils.localize(getTranslationKey());
			String ownerName = BriefcaseItem.getOwnerName(briefcase);
			String ownerUUID = BriefcaseItem.getOwnerUUID(briefcase);

			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.name", ownerName.isEmpty() ? "????" : ownerName), TextFormatting.DARK_PURPLE);
			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.uuid", ownerUUID.isEmpty() ? "????" : ownerUUID), TextFormatting.DARK_PURPLE);
			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.password", briefcase.hasTag() ? briefcase.getTag().getString("passcode") : "????"), TextFormatting.DARK_PURPLE);
			return ActionResult.resultSuccess(adminTool);
		}

		return ActionResult.resultPass(adminTool);
	}
}
