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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class AdminToolItem extends Item {

	public AdminToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Player player = ctx.getPlayer();
		MutableComponent adminToolName = Utils.localize(getDescriptionId());

		if(ConfigHandler.SERVER.allowAdminTool.get()) {
			if(!player.isCreative())
			{
				PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.needCreative"), ChatFormatting.DARK_PURPLE);
				return InteractionResult.FAIL;
			}

			InteractionResult briefcaseResult = handleBriefcase(player, ctx.getHand()).getResult();

			if(briefcaseResult != InteractionResult.PASS)
				return briefcaseResult;

			BlockEntity te = world.getBlockEntity(pos);

			if(te != null) {
				boolean hasInfo = false;

				if(te instanceof IOwnable) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.name", (((IOwnable) te).getOwner().getName() == null ? "????" : ((IOwnable) te).getOwner().getName())), ChatFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.uuid", (((IOwnable) te).getOwner().getUUID() == null ? "????" : ((IOwnable) te).getOwner().getUUID())), ChatFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IPasswordProtected) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.password", (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword())), ChatFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IModuleInventory) {
					List<ModuleType> modules = ((IModuleInventory) te).getInsertedModules();

					if(!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.equippedModules"), ChatFormatting.DARK_PURPLE);

						for(ModuleType module : modules)
							PlayerUtils.sendMessageToPlayer(player, adminToolName, new TextComponent("- ").append(new TranslatableComponent(module.getTranslationKey())), ChatFormatting.DARK_PURPLE);

						hasInfo = true;
					}
				}

				if(te instanceof SecretSignTileEntity)
				{
					PlayerUtils.sendMessageToPlayer(player, adminToolName, new TextComponent(""), ChatFormatting.DARK_PURPLE); //EMPTY

					for(int i = 0; i < 4; i++)
					{
						FormattedText text = ((SecretSignTileEntity)te).messages[i];

						if(text instanceof MutableComponent)
							PlayerUtils.sendMessageToPlayer(player, adminToolName, (MutableComponent)text, ChatFormatting.DARK_PURPLE);
					}

					hasInfo = true;
				}

				if(!hasInfo)
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.noInfo"), ChatFormatting.DARK_PURPLE);

				return InteractionResult.SUCCESS;
			}

			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.noInfo"), ChatFormatting.DARK_PURPLE);
		}
		else {
			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.disabled"), ChatFormatting.DARK_PURPLE);
		}

		return InteractionResult.FAIL;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if(!player.isCreative())
		{
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:adminTool.needCreative"), ChatFormatting.DARK_PURPLE);
			return InteractionResultHolder.fail(player.getItemInHand(hand));
		}
		else return handleBriefcase(player, hand);
	}

	private InteractionResultHolder<ItemStack> handleBriefcase(Player player, InteractionHand hand)
	{
		ItemStack adminTool = player.getItemInHand(hand);

		if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get()) {
			ItemStack briefcase = player.getOffhandItem();
			MutableComponent adminToolName = Utils.localize(getDescriptionId());
			String ownerName = BriefcaseItem.getOwnerName(briefcase);
			String ownerUUID = BriefcaseItem.getOwnerUUID(briefcase);

			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.name", ownerName.isEmpty() ? "????" : ownerName), ChatFormatting.DARK_PURPLE);
			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.uuid", ownerUUID.isEmpty() ? "????" : ownerUUID), ChatFormatting.DARK_PURPLE);
			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.password", briefcase.hasTag() ? briefcase.getTag().getString("passcode") : "????"), ChatFormatting.DARK_PURPLE);
			return InteractionResultHolder.success(adminTool);
		}

		return InteractionResultHolder.pass(adminTool);
	}
}
