package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

public class CameraMonitorItem extends Item {

	public CameraMonitorItem() {
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z);
	}

	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ){
		if(!world.isRemote){
			if(BlockUtils.getBlock(world, pos) == SCContent.SECURITY_CAMERA.get() && !PlayerUtils.isPlayerMountedOnCamera(player)){
				if(!((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player) && !((SecurityCameraTileEntity)world.getTileEntity(pos)).hasModule(CustomModules.SMART)){
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.CAMERA_MONITOR.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:cameraMonitor.cannotView"), TextFormatting.RED);
					return ActionResultType.SUCCESS;
				}

				if(stack.getTag() == null)
					stack.setTag(new CompoundNBT());

				CameraView view = new CameraView(pos, player.dimension.getId());

				if(isCameraAdded(stack.getTag(), view)){
					stack.getTag().remove(getTagNameFromPosition(stack.getTag(), view));
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.CAMERA_MONITOR.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:cameraMonitor.unbound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.RED);
					return ActionResultType.SUCCESS;
				}

				for(int i = 1; i <= 30; i++)
					if (!stack.getTag().contains("Camera" + i)){
						stack.getTag().putString("Camera" + i, view.toNBTString());
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.CAMERA_MONITOR.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:cameraMonitor.bound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.GREEN);
						break;
					}

				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new UpdateNBTTagOnClient(stack));

				return ActionResultType.SUCCESS;
			}
		}else if(world.isRemote && (BlockUtils.getBlock(world, pos) != SCContent.SECURITY_CAMERA.get() || PlayerUtils.isPlayerMountedOnCamera(player))){
			if(stack.getTag() == null || stack.getTag().isEmpty()) {
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.CAMERA_MONITOR.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:cameraMonitor.rightclickToView"), TextFormatting.RED);
				return ActionResultType.SUCCESS;
			}

			SecurityCraft.proxy.displayCameraMonitorGui(player.inventory, (CameraMonitorItem) stack.getItem(), stack.getTag());
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (world.isRemote) {
			if(!stack.hasTag() || !hasCameraAdded(stack.getTag())) {
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.CAMERA_MONITOR.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:cameraMonitor.rightclickToView"), TextFormatting.RED);
				return ActionResult.resultPass(stack);
			}

			if(stack.getItem() == SCContent.CAMERA_MONITOR.get())
				SecurityCraft.proxy.displayCameraMonitorGui(player.inventory, (CameraMonitorItem) stack.getItem(), stack.getTag());
		}

		return ActionResult.resultPass(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if(stack.getTag() == null)
			return;

		tooltip.add(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:cameraMonitor") + " " + getNumberOfCamerasBound(stack.getTag()) + "/30"));
	}

	public static String getTagNameFromPosition(CompoundNBT tag, CameraView view) {
		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return "Camera" + i;
			}

		return "";
	}

	public boolean hasCameraAdded(CompoundNBT tag){
		if(tag == null) return false;

		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i))
				return true;

		return false;
	}

	public boolean isCameraAdded(CompoundNBT tag, CameraView view){
		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return true;
			}

		return false;
	}

	public ArrayList<CameraView> getCameraPositions(CompoundNBT tag){
		ArrayList<CameraView> list = new ArrayList<>();

		for(int i = 1; i <= 30; i++)
			if(tag != null && tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				list.add(new CameraView(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), (coords.length == 4 ? Integer.parseInt(coords[3]) : 0)));
			}
			else
				list.add(null);

		return list;
	}

	public int getNumberOfCamerasBound(CompoundNBT tag) {
		if(tag == null) return 0;

		int amount = 0;

		for(int i = 1; i <= 31; i++)
		{
			if(tag.contains("Camera" + i))
				amount++;
		}

		return amount;
	}

}