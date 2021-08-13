package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

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

				if(te instanceof IOwnable ownable) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.name", (ownable.getOwner().getName() == null ? "????" : ownable.getOwner().getName())), ChatFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.uuid", (ownable.getOwner().getUUID() == null ? "????" : ownable.getOwner().getUUID())), ChatFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IPasswordProtected passwordProtected) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.password", (passwordProtected.getPassword() == null ? "????" : passwordProtected.getPassword())), ChatFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IModuleInventory inv) {
					List<ModuleType> modules = inv.getInsertedModules();

					if(!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.equippedModules"), ChatFormatting.DARK_PURPLE);

						for(ModuleType module : modules)
							PlayerUtils.sendMessageToPlayer(player, adminToolName, new TextComponent("- ").append(new TranslatableComponent(module.getTranslationKey())), ChatFormatting.DARK_PURPLE);

						hasInfo = true;
					}
				}

				if(te instanceof SecretSignBlockEntity signTe)
				{
					PlayerUtils.sendMessageToPlayer(player, adminToolName, new TextComponent(""), ChatFormatting.DARK_PURPLE); //EMPTY

					for(int i = 0; i < 4; i++)
					{
						FormattedText text = signTe.getMessage(i, false);

						if(text instanceof MutableComponent mutableComponent)
							PlayerUtils.sendMessageToPlayer(player, adminToolName, mutableComponent, ChatFormatting.DARK_PURPLE);
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
