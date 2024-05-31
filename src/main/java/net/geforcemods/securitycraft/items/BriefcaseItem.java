package net.geforcemods.securitycraft.items;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BriefcaseItem extends ColorableItem {
	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		extraHandling(stack, world, player);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void extraHandling(ItemStack stack, World level, EntityPlayer player) {
		if (!level.isRemote) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());

			player.openGui(SecurityCraft.instance, stack.getTagCompound().hasKey("passcode") ? Screens.BRIEFCASE_INSERT_CODE.ordinal() : Screens.BRIEFCASE_CODE_SETUP.ordinal(), level, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == Enchantments.VANISHING_CURSE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack briefcase, World world, List<String> tooltip, ITooltipFlag flag) {
		String ownerName = getOwnerName(briefcase);

		if (!ownerName.isEmpty())
			tooltip.add(Utils.localize("tooltip.securitycraft.component.owner", ownerName).setStyle(Utils.GRAY_STYLE).getFormattedText());
	}

	@Override
	public int getDefaultColor() {
		return 0x333333;
	}

	public static void hashAndSetPasscode(NBTTagCompound briefcaseTag, String passcode, Consumer<byte[]> afterSet) {
		byte[] salt = PasscodeUtils.generateSalt();

		briefcaseTag.setUniqueId("saltKey", SaltData.putSalt(salt));
		PasscodeUtils.hashPasscode(passcode, salt, p -> {
			briefcaseTag.setString("passcode", PasscodeUtils.bytesToString(p));
			afterSet.accept(p);
		});
	}

	public static void checkPasscode(EntityPlayerMP player, String incomingCode, String briefcaseCode, NBTTagCompound tag) {
		UUID saltKey = tag.hasUniqueId("saltKey") ? tag.getUniqueId("saltKey") : null;
		byte[] salt = SaltData.getSalt(saltKey);

		if (salt == null) { //If no salt key or no salt associated with the given key can be found, a new passcode needs to be set
			PasscodeUtils.filterPasscodeAndSaltFromTag(tag);
			return;
		}

		PasscodeUtils.setOnCooldown(player);
		PasscodeUtils.hashPasscode(incomingCode, salt, p -> {
			if (Arrays.equals(PasscodeUtils.stringToBytes(briefcaseCode), p)) {
				if (!tag.hasKey("owner")) { //If the briefcase doesn't have an owner (that usually gets set when assigning a new passcode), set the player that first enters the correct passcode as the owner
					tag.setString("owner", player.getName());
					tag.setString("ownerUUID", player.getUniqueID().toString());
				}

				player.openGui(SecurityCraft.instance, Screens.BRIEFCASE_INVENTORY.ordinal(), player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
			}
		});
	}

	public static boolean isOwnedBy(ItemStack briefcase, EntityPlayer player) {
		if (!briefcase.hasTagCompound())
			return true;

		String ownerName = getOwnerName(briefcase);
		String ownerUUID = getOwnerUUID(briefcase);

		return ownerName.isEmpty() || ownerUUID.equals(player.getUniqueID().toString()) || (ownerUUID.equals("ownerUUID") && ownerName.equals(player.getName()));
	}

	public static String getOwnerName(ItemStack briefcase) {
		return briefcase.hasTagCompound() ? briefcase.getTagCompound().getString("owner") : "";
	}

	public static String getOwnerUUID(ItemStack briefcase) {
		return briefcase.hasTagCompound() ? briefcase.getTagCompound().getString("ownerUUID") : "";
	}
}
