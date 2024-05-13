package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICodebreakable;
import net.geforcemods.securitycraft.misc.LinkingStateItemPropertyHandler;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CodebreakerItem extends Item {
	public static final ResourceLocation STATE_PROPERTY = new ResourceLocation(SecurityCraft.MODID, "codebreaker_state");
	public static final String WORKING = "working", LAST_USED_TIME = "last_used_time", WAS_SUCCESSFUL = "was_successful";

	public CodebreakerItem() {
		maxStackSize = 1;
		setMaxDamage(4); //5 uses because when the damage is 0 the item has one more use
		addPropertyOverride(STATE_PROPERTY, (stack, world, entity) -> {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());

			NBTTagCompound tag = stack.getTagCompound();

			if (CodebreakerItem.wasRecentlyUsed(stack))
				return tag.getBoolean(CodebreakerItem.WAS_SUCCESSFUL) ? 0.75F : 0.5F;

			if (!(entity instanceof EntityPlayer))
				return 0.0F;

			EntityPlayer player = (EntityPlayer) entity;
			float state = LinkingStateItemPropertyHandler.getLinkingState(world, player, stack, (_level, pos) -> _level.getTileEntity(pos) instanceof ICodebreakable, 0, null, false, (_tag, pos) -> true);

			if (state == LinkingStateItemPropertyHandler.LINKED_STATE || state == LinkingStateItemPropertyHandler.NOT_LINKED_STATE)
				return 0.25F;
			else
				return 0.0F;
		});
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack codebreaker = player.getHeldItem(hand);

		if (hand == EnumHand.MAIN_HAND) {
			ItemStack briefcase = player.getHeldItemOffhand();

			if (briefcase.getItem() == SCContent.briefcase) {
				if (BriefcaseItem.isOwnedBy(briefcase, player) && !player.isCreative())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.codebreaker), Utils.localize("messages.securitycraft:codebreaker.owned"), TextFormatting.RED);
				else {
					double chance = ConfigHandler.codebreakerChance;

					if (chance < 0.0D)
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.briefcase), Utils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
					else {
						codebreaker.damageItem(1, player);

						if (!world.isRemote) {
							if (!codebreaker.hasTagCompound())
								codebreaker.setTagCompound(new NBTTagCompound());

							if (wasRecentlyUsed(codebreaker))
								return ActionResult.newResult(EnumActionResult.PASS, codebreaker);

							boolean isSuccessful = player.isCreative() || SecurityCraft.RANDOM.nextDouble() < chance;
							NBTTagCompound tag = codebreaker.getTagCompound();

							tag.setLong(CodebreakerItem.LAST_USED_TIME, System.currentTimeMillis());
							tag.setBoolean(CodebreakerItem.WAS_SUCCESSFUL, isSuccessful);

							if (isSuccessful)
								player.openGui(SecurityCraft.instance, Screens.BRIEFCASE_INVENTORY.ordinal(), world, (int) player.posX, (int) player.posY, (int) player.posZ);
							else
								PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.codebreaker), Utils.localize("messages.securitycraft:codebreaker.failed"), TextFormatting.RED);
						}
					}
				}

				return ActionResult.newResult(EnumActionResult.SUCCESS, codebreaker);
			}
		}

		return ActionResult.newResult(EnumActionResult.PASS, codebreaker);
	}

	public static boolean wasRecentlyUsed(ItemStack stack) {
		long lastUsedTime = stack.getTagCompound().getLong(CodebreakerItem.LAST_USED_TIME);

		return lastUsedTime != 0 && System.currentTimeMillis() - lastUsedTime < 3000L;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}
}
