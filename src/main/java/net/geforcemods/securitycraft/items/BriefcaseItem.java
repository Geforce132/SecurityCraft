package net.geforcemods.securitycraft.items;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class BriefcaseItem extends Item implements IDyeableArmorItem {
	public BriefcaseItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType useOn(ItemUseContext ctx) {
		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		ItemStack stack = ctx.getItemInHand();
		PlayerEntity player = ctx.getPlayer();

		if (level.getBlockState(pos).getBlock() instanceof CauldronBlock) //don't open the briefcase when a cauldron is rightclicked for removing the dye
			return ActionResultType.SUCCESS;

		handle(stack, level, player);
		return ActionResultType.CONSUME;
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);

		handle(stack, level, player);
		return ActionResult.consume(stack);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == Enchantments.VANISHING_CURSE;
	}

	private void handle(ItemStack stack, World level, PlayerEntity player) {
		if (!level.isClientSide)
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(stack.getOrCreateTag().contains("passcode") ? OpenScreen.DataType.CHECK_BRIEFCASE_PASSCODE : OpenScreen.DataType.SET_BRIEFCASE_PASSCODE));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack briefcase, World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
		String ownerName = getOwnerName(briefcase);

		if (!ownerName.isEmpty())
			tooltip.add(Utils.localize("tooltip.securitycraft.component.owner", ownerName).setStyle(Utils.GRAY_STYLE));
	}

	public static void hashAndSetPasscode(CompoundNBT briefcaseTag, String passcode, Consumer<byte[]> afterSet) {
		byte[] salt = PasscodeUtils.generateSalt();

		briefcaseTag.putUUID("saltKey", SaltData.putSalt(salt));
		PasscodeUtils.hashPasscode(passcode, salt, p -> {
			briefcaseTag.putString("passcode", PasscodeUtils.bytesToString(p));
			afterSet.accept(p);
		});
	}

	public static void checkPasscode(ServerPlayerEntity player, ItemStack briefcase, String incomingCode, String briefcaseCode, CompoundNBT tag) {
		UUID saltKey = tag.contains("saltKey", Constants.NBT.TAG_INT_ARRAY) ? tag.getUUID("saltKey") : null;
		byte[] salt = SaltData.getSalt(saltKey);

		if (salt == null) { //If no salt key or no salt associated with the given key can be found, a new passcode needs to be set
			PasscodeUtils.filterPasscodeAndSaltFromTag(tag);
			return;
		}

		PasscodeUtils.hashPasscode(incomingCode, salt, p -> {
			if (Arrays.equals(PasscodeUtils.stringToBytes(briefcaseCode), p)) {
				if (!tag.contains("owner")) { //If the briefcase doesn't have an owner (that usually gets set when assigning a new passcode), set the player that first enters the correct passcode as the owner
					tag.putString("owner", player.getName().getString());
					tag.putString("ownerUUID", player.getUUID().toString());
				}

				NetworkHooks.openGui(player, new INamedContainerProvider() {
					@Override
					public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
						return new BriefcaseMenu(windowId, inv, ItemContainer.briefcase(PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get())));
					}

					@Override
					public ITextComponent getDisplayName() {
						return briefcase.getHoverName();
					}
				}, player.blockPosition());
			}
		});
	}

	public static boolean isOwnedBy(ItemStack briefcase, PlayerEntity player) {
		if (!briefcase.hasTag())
			return true;

		String ownerName = getOwnerName(briefcase);
		String ownerUUID = getOwnerUUID(briefcase);

		return ownerName.isEmpty() || ownerUUID.equals(player.getUUID().toString()) || (ownerUUID.equals("ownerUUID") && ownerName.equals(player.getName().getString()));
	}

	public static String getOwnerName(ItemStack briefcase) {
		return briefcase.hasTag() ? briefcase.getTag().getString("owner") : "";
	}

	public static String getOwnerUUID(ItemStack briefcase) {
		return briefcase.hasTag() ? briefcase.getTag().getString("ownerUUID") : "";
	}
}
