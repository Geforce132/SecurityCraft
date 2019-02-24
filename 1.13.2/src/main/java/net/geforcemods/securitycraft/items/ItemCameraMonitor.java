package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemCameraMonitor extends Item {

	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ());
	}

	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, ItemStack stack, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!world.isRemote){
			if(BlockUtils.getBlock(world, pos) == SCContent.securityCamera && !PlayerUtils.isPlayerMountedOnCamera(player)){
				if(!((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player)){
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:cameraMonitor.name"), ClientUtils.localize("messages.securitycraft:cameraMonitor.cannotView"), TextFormatting.RED);
					return EnumActionResult.SUCCESS;
				}

				if(player.inventory.getCurrentItem().getTag() == null)
					player.inventory.getCurrentItem().setTag(new NBTTagCompound());

				CameraView view = new CameraView(pos, player.dimension.getId());

				if(isCameraAdded(player.inventory.getCurrentItem().getTag(), view)){
					player.inventory.getCurrentItem().getTag().remove(getTagNameFromPosition(player.inventory.getCurrentItem().getTag(), view));
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:cameraMonitor.name"), ClientUtils.localize("messages.securitycraft:cameraMonitor.unbound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.RED);
					return EnumActionResult.SUCCESS;
				}

				for(int i = 1; i <= 30; i++)
					if (!player.inventory.getCurrentItem().getTag().contains("Camera" + i)){
						player.inventory.getCurrentItem().getTag().putString("Camera" + i, view.toNBTString());
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:cameraMonitor.name"), ClientUtils.localize("messages.securitycraft:cameraMonitor.bound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.GREEN);
						break;
					}

				SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(stack), (EntityPlayerMP)player);

				return EnumActionResult.SUCCESS;
			}
		}else if(world.isRemote && (BlockUtils.getBlock(world, pos) != SCContent.securityCamera || PlayerUtils.isPlayerMountedOnCamera(player))){
			if(stack.getTag() == null || stack.getTag().isEmpty()) {
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:cameraMonitor.name"), ClientUtils.localize("messages.securitycraft:cameraMonitor.rightclickToView"), TextFormatting.RED);
				return EnumActionResult.SUCCESS;
			}

			player.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.SUCCESS;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (world.isRemote) {
			if(!stack.hasTag() || !hasCameraAdded(stack.getTag())) {
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:cameraMonitor.name"), ClientUtils.localize("messages.securitycraft:cameraMonitor.rightclickToView"), TextFormatting.RED);
				return ActionResult.newResult(EnumActionResult.PASS, stack);
			}

			player.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if(stack.getTag() == null)
			return;

		tooltip.add(new TextComponentString(ClientUtils.localize("tooltip.securitycraft:cameraMonitor") + " " + getNumberOfCamerasBound(stack.getTag()) + "/30"));
	}

	public static String getTagNameFromPosition(NBTTagCompound tag, CameraView view) {
		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return "Camera" + i;
			}

		return "";
	}

	public int getSlotFromPosition(NBTTagCompound tag, CameraView view) {
		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return i;
			}

		return -1;
	}

	public boolean hasCameraAdded(NBTTagCompound tag){
		if(tag == null) return false;

		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i))
				return true;

		return false;
	}

	public boolean isCameraAdded(NBTTagCompound tag, CameraView view){
		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return true;
			}

		return false;
	}

	public ArrayList<CameraView> getCameraPositions(NBTTagCompound tag){
		ArrayList<CameraView> list = new ArrayList<CameraView>();

		for(int i = 1; i <= 30; i++)
			if(tag != null && tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				list.add(new CameraView(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), (coords.length == 4 ? Integer.parseInt(coords[3]) : 0)));
			}
			else
				list.add(null);

		return list;
	}

	public int getNumberOfCamerasBound(NBTTagCompound tag) {
		if(tag == null) return 0;

		for(int i = 1; i <= 31; i++)
			if(tag.contains("Camera" + i))
				continue;
			else
				return i - 1;

		return 0;
	}

}