package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class UniversalKeyChangerItem extends Item {
	public UniversalKeyChangerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
		PlayerEntity player = ctx.getPlayer();
		Hand hand = ctx.getHand();
		World level = ctx.getLevel();
		ActionResultType briefcaseResult = handleBriefcase(level, player, hand).getResult();

		if (briefcaseResult != ActionResultType.PASS)
			return briefcaseResult;

		BlockPos pos = ctx.getClickedPos();
		TileEntity be = level.getBlockEntity(pos);

		if (be instanceof DisplayCaseBlockEntity && (((DisplayCaseBlockEntity) be).isOpen() && ((DisplayCaseBlockEntity) be).getDisplayedStack().isEmpty()))
			return ActionResultType.PASS;
		else if (be instanceof IPasscodeProtected) {
			if (((IOwnable) be).isOwnedBy(player)) {
				if (!level.isClientSide)
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(DataType.UNIVERSAL_KEY_CHANGER, pos));

				return ActionResultType.SUCCESS;
			}
			else if (!(be.getBlockState().getBlock() instanceof IDisguisable) || (((BlockItem) ((IDisguisable) be.getBlockState().getBlock()).getDisguisedStack(level, pos).getItem()).getBlock() instanceof IDisguisable)) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(((IOwnable) be).getOwner())), TextFormatting.RED);
				return ActionResultType.FAIL;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		return handleBriefcase(level, player, hand);
	}

	private ActionResult<ItemStack> handleBriefcase(World level, PlayerEntity player, Hand hand) {
		ItemStack keyChanger = player.getItemInHand(hand);

		if (hand == Hand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get()) {
			ItemStack briefcase = player.getOffhandItem();

			if (BriefcaseItem.isOwnedBy(briefcase, player) || player.isCreative()) {
				CompoundNBT tag = briefcase.getTag();

				if (tag != null && tag.contains("passcode")) {
					if (tag.contains("saltKey") && !level.isClientSide)
						SaltData.removeSalt(tag.getUUID("saltKey"));

					PasscodeUtils.filterPasscodeAndSaltFromTag(tag);
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.briefcase.passcodeReset"), TextFormatting.GREEN);
					return ActionResult.success(keyChanger);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.briefcase.noPasscode"), TextFormatting.RED);
			}
			else
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.briefcase.notOwned"), TextFormatting.RED);

			return ActionResult.consume(keyChanger);
		}

		return ActionResult.pass(keyChanger);
	}
}
