package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

public class MineRemoteAccessToolItem extends Item {
	public MineRemoteAccessToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		if (level.isClientSide) {
			ClientHandler.displayMRATScreen(player.getItemInHand(hand));
		}

		return ActionResult.consume(player.getItemInHand(hand));
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();

		if (level.getBlockState(pos).getBlock() instanceof IExplosive) {
			PlayerEntity player = ctx.getPlayer();

			if (!isMineAdded(stack, pos)) {
				int nextSlot = getNextAvaliableSlot(stack);

				if (nextSlot == 0) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.MINE_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:mrat.noSlots"), TextFormatting.RED);
					return ActionResultType.FAIL;
				}

				TileEntity te = level.getBlockEntity(pos);

				if (te instanceof IOwnable && !((IOwnable) te).isOwnedBy(player)) {
					if (level.isClientSide)
						ClientHandler.displayMRATScreen(stack);

					return ActionResultType.SUCCESS;
				}

				if (stack.getTag() == null)
					stack.setTag(new CompoundNBT());

				stack.getTag().putIntArray(("mine" + nextSlot), new int[] {
						pos.getX(), pos.getY(), pos.getZ()
				});

				if (!level.isClientSide)
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new UpdateNBTTagOnClient(stack));

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.MINE_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:mrat.bound", Utils.getFormattedCoordinates(pos)), TextFormatting.GREEN);
				return ActionResultType.SUCCESS;
			}
			else {
				removeTagFromItemAndUpdate(stack, pos, player);
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.MINE_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:mrat.unbound", Utils.getFormattedCoordinates(pos)), TextFormatting.RED);
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World level, List<ITextComponent> list, ITooltipFlag flag) {
		if (stack.getTag() == null)
			return;

		for (int i = 1; i <= 6; i++) {
			if (stack.getTag().getIntArray("mine" + i).length > 0) {
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if (coords[0] == 0 && coords[1] == 0 && coords[2] == 0)
					list.add(new StringTextComponent(TextFormatting.GRAY + "---"));
				else
					list.add(Utils.localize("tooltip.securitycraft:mine", i, Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2]))).setStyle(Utils.GRAY_STYLE));
			}
			else
				list.add(new StringTextComponent(TextFormatting.GRAY + "---"));
		}
	}

	public static boolean hasMineAdded(CompoundNBT tag) {
		if (tag == null)
			return false;

		for (int i = 1; i <= 6; i++) {
			int[] coords = tag.getIntArray("mine" + i);

			if (tag.getIntArray("mine" + i).length > 0 && (coords[0] != 0 || coords[1] != 0 || coords[2] != 0))
				return true;
		}

		return false;
	}

	public static void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, PlayerEntity player) {
		if (stack.getTag() == null)
			return;

		for (int i = 1; i <= 6; i++) {
			if (stack.getTag().getIntArray("mine" + i).length > 0) {
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if (coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()) {
					stack.getTag().putIntArray("mine" + i, new int[] {
							0, 0, 0
					});

					if (!player.level.isClientSide)
						SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new UpdateNBTTagOnClient(stack));

					return;
				}
			}
		}
	}

	public static boolean isMineAdded(ItemStack stack, BlockPos pos) {
		if (stack.getTag() == null)
			return false;

		for (int i = 1; i <= 6; i++) {
			int[] coords = stack.getTag().getIntArray("mine" + i);

			if (stack.getTag().getIntArray("mine" + i).length > 0 && coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
				return true;
		}

		return false;
	}

	public static int getNextAvaliableSlot(ItemStack stack) {
		if (stack.getTag() == null)
			return 1;

		for (int i = 1; i <= 6; i++) {
			if (stack.getTag().getIntArray("mine" + i).length == 0 || (stack.getTag().getIntArray("mine" + i)[0] == 0 && stack.getTag().getIntArray("mine" + i)[1] == 0 && stack.getTag().getIntArray("mine" + i)[2] == 0))
				return i;
		}

		return 0;
	}
}