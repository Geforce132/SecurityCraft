package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class CameraMonitorItem extends Item {

	private static final Style GRAY_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);

	public CameraMonitorItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), ctx.getItemInHand(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z);
	}

	public InteractionResult onItemUse(Player player, Level world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ){
		if(world.getBlockState(pos).getBlock() == SCContent.SECURITY_CAMERA.get() && !PlayerUtils.isPlayerMountedOnCamera(player)){
			SecurityCameraTileEntity te = (SecurityCameraTileEntity)world.getBlockEntity(pos);

			if(!te.getOwner().isOwner(player) && !te.hasModule(ModuleType.SMART)){
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.cannotView"), ChatFormatting.RED);
				return InteractionResult.FAIL;
			}

			if(stack.getTag() == null)
				stack.setTag(new CompoundTag());

			GlobalPos view = GlobalPos.of(player.level.dimension(), pos);

			if(isCameraAdded(stack.getTag(), view)){
				stack.getTag().remove(getTagNameFromPosition(stack.getTag(), view));
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.unbound", Utils.getFormattedCoordinates(pos)), ChatFormatting.RED);
				return InteractionResult.SUCCESS;
			}

			for(int i = 1; i <= 30; i++)
				if (!stack.getTag().contains("Camera" + i)){
					stack.getTag().putString("Camera" + i, WorldUtils.toNBTString(view));
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.bound", Utils.getFormattedCoordinates(pos)), ChatFormatting.GREEN);
					break;
				}

			if (!world.isClientSide)
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new UpdateNBTTagOnClient(stack));

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if(!stack.hasTag() || !hasCameraAdded(stack.getTag())) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.rightclickToView"), ChatFormatting.RED);
			return InteractionResultHolder.pass(stack);
		}

		if(stack.getItem() == SCContent.CAMERA_MONITOR.get() && world.isClientSide)
			ClientHandler.displayCameraMonitorGui(player.getInventory(), (CameraMonitorItem) stack.getItem(), stack.getTag());

		return InteractionResultHolder.consume(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
		if(stack.getTag() == null)
			return;

		tooltip.add(Utils.localize("tooltip.securitycraft:cameraMonitor").append(new TextComponent(" " + getNumberOfCamerasBound(stack.getTag()) + "/30")).setStyle(GRAY_STYLE));
	}

	public static String getTagNameFromPosition(CompoundTag tag, GlobalPos view) {
		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				if(WorldUtils.checkCoordinates(view, coords))
					return "Camera" + i;
			}

		return "";
	}

	public boolean hasCameraAdded(CompoundTag tag){
		if(tag == null) return false;

		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i))
				return true;

		return false;
	}

	public boolean isCameraAdded(CompoundTag tag, GlobalPos view){
		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				if(WorldUtils.checkCoordinates(view, coords))
					return true;
			}

		return false;
	}

	public ArrayList<GlobalPos> getCameraPositions(CompoundTag tag){
		ArrayList<GlobalPos> list = new ArrayList<>();

		for(int i = 1; i <= 30; i++)
			if(tag != null && tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				//default to overworld if there is no dimension saved
				list.add(GlobalPos.of(coords.length == 4 ? ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(coords[3])) : Level.OVERWORLD, new BlockPos(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]))));
			}
			else
				list.add(null);

		return list;
	}

	public int getNumberOfCamerasBound(CompoundTag tag) {
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