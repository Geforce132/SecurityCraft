package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.misc.LinkingStateItemPropertyHandler;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SentryRemoteAccessToolItem extends Item {
	public SentryRemoteAccessToolItem() {
		addPropertyOverride(LinkingStateItemPropertyHandler.LINKING_STATE_PROPERTY, LinkingStateItemPropertyHandler::sentryRemoteAccessTool);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote) {
			updateTagWithNames(stack, world);
			player.openGui(SecurityCraft.instance, Screens.SRAT.ordinal(), world, player.getServer().getPlayerList().getEntityViewDistance(), (int) player.posY, (int) player.posZ);
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		List<Sentry> sentries = world.getEntitiesWithinAABB(Sentry.class, new AxisAlignedBB(pos));

		if (!sentries.isEmpty()) {
			Sentry sentry = sentries.get(0);
			BlockPos sentryPos = sentry.getPosition();

			if (!isSentryAdded(stack, sentryPos)) {
				int nextAvailableSlot = getNextAvailableSlot(stack);

				if (nextAvailableSlot == 0) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:remoteAccessSentry.name"), Utils.localize("messages.securitycraft:srat.noSlots"), TextFormatting.RED);
					return EnumActionResult.SUCCESS;
				}

				if (!sentry.isOwnedBy(player)) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:remoteAccessSentry.name"), Utils.localize("messages.securitycraft:srat.cantBind"), TextFormatting.RED);
					return EnumActionResult.SUCCESS;
				}

				if (stack.getTagCompound() == null)
					stack.setTagCompound(new NBTTagCompound());

				stack.getTagCompound().setIntArray("sentry" + nextAvailableSlot, new int[] {
						sentryPos.getX(), sentryPos.getY(), sentryPos.getZ()
				});
				stack.getTagCompound().setString("sentry" + nextAvailableSlot + "_name", sentry.getCustomNameTag());

				if (!world.isRemote)
					SecurityCraft.network.sendTo(new UpdateNBTTagOnClient(stack), (EntityPlayerMP) player);

				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:remoteAccessSentry.name"), Utils.localize("messages.securitycraft:srat.bound", sentryPos), TextFormatting.GREEN);
			}
			else {
				removeTagFromItemAndUpdate(stack, sentryPos, player);
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:remoteAccessSentry.name"), Utils.localize("messages.securitycraft:srat.unbound", sentryPos), TextFormatting.RED);
			}

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
		if (stack.getTagCompound() == null)
			return;

		for (int i = 1; i <= 12; i++) {
			if (stack.getTagCompound().getIntArray("sentry" + i).length > 0) {
				int[] coords = stack.getTagCompound().getIntArray("sentry" + i);

				if (coords[0] == 0 && coords[1] == 0 && coords[2] == 0)
					list.add("---");
				else {
					BlockPos pos = new BlockPos(coords[0], coords[1], coords[2]);
					String nameKey = "sentry" + i + "_name";
					String nameToShow = null;

					if (stack.getTagCompound().hasKey(nameKey))
						nameToShow = stack.getTagCompound().getString(nameKey);
					else {
						List<Sentry> sentries = Minecraft.getMinecraft().player.world.getEntitiesWithinAABB(Sentry.class, new AxisAlignedBB(pos));

						if (!sentries.isEmpty() && sentries.get(0).hasCustomName())
							nameToShow = sentries.get(0).getCustomNameTag();
						else
							nameToShow = Utils.localize("tooltip.securitycraft:sentry", i).getFormattedText();
					}

					list.add(nameToShow + ": " + Utils.getFormattedCoordinates(pos).setStyle(Utils.GRAY_STYLE).getFormattedText());
				}
			}
			else
				list.add("---");
		}
	}

	private void updateTagWithNames(ItemStack stack, World level) {
		if (!stack.hasTagCompound())
			return;

		NBTTagCompound tag = stack.getTagCompound();

		for (int i = 1; i <= 12; i++) {
			int[] coords = tag.getIntArray("sentry" + i);
			String nameKey = "sentry" + i + "_name";

			if (coords.length == 3 && !(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)) {
				BlockPos sentryPos = new BlockPos(coords[0], coords[1], coords[2]);

				if (level.isBlockLoaded(sentryPos, false)) {
					List<Sentry> sentries = level.getEntitiesWithinAABB(Sentry.class, new AxisAlignedBB(sentryPos));

					if (!sentries.isEmpty()) {
						Sentry sentry = sentries.get(0);

						if (sentry.hasCustomName()) {
							tag.setString(nameKey, sentry.getCustomNameTag());
							continue;
						}
					}
				}
				else
					continue;
			}

			tag.removeTag(nameKey);
		}
	}

	private void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, EntityPlayer player) {
		if (stack.getTagCompound() == null)
			return;

		for (int i = 1; i <= 12; i++) {
			if (stack.getTagCompound().getIntArray("sentry" + i).length > 0) {
				int[] coords = stack.getTagCompound().getIntArray("sentry" + i);

				if (coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()) {
					stack.getTagCompound().setIntArray("sentry" + i, new int[] {
							0, 0, 0
					});

					if (!player.world.isRemote)
						SecurityCraft.network.sendTo(new UpdateNBTTagOnClient(stack), (EntityPlayerMP) player);

					return;
				}
			}
		}
	}

	public static boolean hasSentryAdded(NBTTagCompound tag) {
		if (tag == null)
			return false;

		for (int i = 1; i <= 12; i++) {
			int[] coords = tag.getIntArray("sentry" + i);

			if (tag.getIntArray("sentry" + i).length > 0 && (coords[0] != 0 || coords[1] != 0 || coords[2] != 0))
				return true;
		}

		return false;
	}

	public static boolean isSentryAdded(ItemStack stack, BlockPos pos) {
		if (stack.getTagCompound() == null)
			return false;

		for (int i = 1; i <= 12; i++) {
			int[] coords = stack.getTagCompound().getIntArray("sentry" + i);

			if (stack.getTagCompound().getIntArray("sentry" + i).length > 0 && coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
				return true;
		}

		return false;
	}

	public static int getNextAvailableSlot(ItemStack stack) {
		if (stack.getTagCompound() == null)
			return 1;

		for (int i = 1; i <= 12; i++) {
			int[] pos = stack.getTagCompound().getIntArray("sentry" + i);

			if (pos.length == 0 || (pos[0] == 0 && pos[1] == 0 && pos[2] == 0))
				return i;
		}

		return 0;
	}
}
