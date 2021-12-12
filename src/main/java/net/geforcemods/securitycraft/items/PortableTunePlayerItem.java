package net.geforcemods.securitycraft.items;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity.NoteWrapper;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

public class PortableTunePlayerItem extends Item {
	public PortableTunePlayerItem(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();

		if (world.getBlockState(pos).getBlock() == SCContent.SONIC_SECURITY_SYSTEM.get()) {
			SonicSecuritySystemTileEntity te = (SonicSecuritySystemTileEntity)world.getTileEntity(pos);
			PlayerEntity player = ctx.getPlayer();

			if (te.getOwner().isOwner(player) || ModuleUtils.isAllowed(te, player)) {
				if (te.getNumberOfNotes() > 0) {
					te.saveNotes(ctx.getItem().getOrCreateTag());
					player.sendStatusMessage(Utils.localize("messages.securitycraft:portable_tune_player.tune_saved"), true);
				}
				else
					player.sendStatusMessage(Utils.localize("messages.securitycraft:portable_tune_player.no_tune"), true);

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote) {
			CompoundNBT tag = stack.getOrCreateTag();
			boolean isTunePlaying = SCEventHandler.PLAYING_TUNES.containsKey(player);

			if (!isTunePlaying && tag.contains("Notes")) {
				Deque<NoteWrapper> notes = new ArrayDeque<>();

				SonicSecuritySystemTileEntity.loadNotes(stack.getTag(), notes);
				SCEventHandler.PLAYING_TUNES.put(player, MutablePair.of(0, notes));
				return ActionResult.resultSuccess(stack);
			}
			else if (isTunePlaying) {
				SCEventHandler.PLAYING_TUNES.remove(player);
				return ActionResult.resultSuccess(stack);
			}
		}

		return ActionResult.resultPass(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if(!stack.hasTag())
			return;

		// If a tune is stored in this item, show the number of notes in this tune in the tooltip
		int notesCount = stack.getTag().getList("Notes", Constants.NBT.TAG_COMPOUND).size();

		if(notesCount > 0)
			tooltip.add(Utils.localize("tooltip.securitycraft:portableTunePlayer.noteCount", notesCount).mergeStyle(TextFormatting.GRAY));
	}
}
