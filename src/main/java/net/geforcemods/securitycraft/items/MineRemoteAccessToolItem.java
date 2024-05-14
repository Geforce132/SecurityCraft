package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.LinkingStateItemPropertyHandler;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MineRemoteAccessToolItem extends Item {
	public MineRemoteAccessToolItem() {
		addPropertyOverride(LinkingStateItemPropertyHandler.LINKING_STATE_PROPERTY, LinkingStateItemPropertyHandler::mineRemoteAccessTool);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote) {
			player.openGui(SecurityCraft.instance, Screens.MRAT.ordinal(), world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		if (world.getBlockState(pos).getBlock() instanceof IExplosive) {
			if (!isMineAdded(stack, pos)) {
				int availSlot = getNextAvailableSlot(stack);

				if (availSlot == 0) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:remoteAccessMine.name"), Utils.localize("messages.securitycraft:mrat.noSlots"), TextFormatting.RED);
					return EnumActionResult.SUCCESS;
				}

				TileEntity te = world.getTileEntity(pos);

				if (te instanceof IOwnable && !((IOwnable) te).isOwnedBy(player)) {
					player.openGui(SecurityCraft.instance, Screens.MRAT.ordinal(), world, (int) player.posX, (int) player.posY, (int) player.posZ);
					return EnumActionResult.SUCCESS;
				}

				if (stack.getTagCompound() == null)
					stack.setTagCompound(new NBTTagCompound());

				stack.getTagCompound().setIntArray(("mine" + availSlot), new int[] {
						pos.getX(), pos.getY(), pos.getZ()
				});

				if (!world.isRemote)
					SecurityCraft.network.sendTo(new UpdateNBTTagOnClient(stack), (EntityPlayerMP) player);

				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:remoteAccessMine.name"), Utils.localize("messages.securitycraft:mrat.bound", pos), TextFormatting.GREEN);
				return EnumActionResult.SUCCESS;
			}
			else {
				removeTagFromItemAndUpdate(stack, pos, player);
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:remoteAccessMine.name"), Utils.localize("messages.securitycraft:mrat.unbound", pos), TextFormatting.RED);
				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
		if (stack.getTagCompound() == null)
			return;

		for (int i = 1; i <= 6; i++) {
			if (stack.getTagCompound().getIntArray("mine" + i).length > 0) {
				int[] coords = stack.getTagCompound().getIntArray("mine" + i);

				if (!(coords[0] == 0 && coords[1] == 0 && coords[2] == 0))
					list.add(Utils.localize("tooltip.securitycraft:mine", i, Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2]))).setStyle(Utils.GRAY_STYLE).getFormattedText());
			}

			list.add("---");
		}
	}

	public static boolean hasMineAdded(NBTTagCompound tag) {
		if (tag == null)
			return false;

		for (int i = 1; i <= 6; i++) {
			int[] coords = tag.getIntArray("mine" + i);

			if (tag.getIntArray("mine" + i).length > 0 && (coords[0] != 0 || coords[1] != 0 || coords[2] != 0))
				return true;
		}

		return false;
	}

	public static void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, EntityPlayer player) {
		if (stack.getTagCompound() == null)
			return;

		for (int i = 1; i <= 6; i++) {
			if (stack.getTagCompound().getIntArray("mine" + i).length > 0) {
				int[] coords = stack.getTagCompound().getIntArray("mine" + i);

				if (coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()) {
					stack.getTagCompound().setIntArray("mine" + i, new int[] {
							0, 0, 0
					});

					if (!player.world.isRemote)
						SecurityCraft.network.sendTo(new UpdateNBTTagOnClient(stack), (EntityPlayerMP) player);

					return;
				}
			}
		}
	}

	public static boolean isMineAdded(ItemStack stack, BlockPos pos) {
		if (stack.getTagCompound() == null)
			return false;

		for (int i = 1; i <= 6; i++) {
			int[] coords = stack.getTagCompound().getIntArray("mine" + i);

			if (stack.getTagCompound().getIntArray("mine" + i).length > 0 && coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
				return true;
		}

		return false;
	}

	public static int getNextAvailableSlot(ItemStack stack) {
		if (stack.getTagCompound() == null)
			return 1;

		for (int i = 1; i <= 6; i++) {
			if (stack.getTagCompound().getIntArray("mine" + i).length == 0 || (stack.getTagCompound().getIntArray("mine" + i)[0] == 0 && stack.getTagCompound().getIntArray("mine" + i)[1] == 0 && stack.getTagCompound().getIntArray("mine" + i)[2] == 0))
				return i;
		}

		return 0;
	}
}
