package net.geforcemods.securitycraft.items;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity.NoteWrapper;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class PortableTunePlayerItem extends Item {
	public PortableTunePlayerItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();

		if (level.getBlockState(pos).getBlock() == SCContent.SONIC_SECURITY_SYSTEM.get()) {
			SonicSecuritySystemBlockEntity be = (SonicSecuritySystemBlockEntity) level.getBlockEntity(pos);
			Player player = ctx.getPlayer();

			if (be.isOwnedBy(player) || be.isAllowed(player)) {
				if (be.getNumberOfNotes() > 0) {
					be.saveNotes(ctx.getItemInHand().getOrCreateTag());
					player.displayClientMessage(Utils.localize("messages.securitycraft:portable_tune_player.tune_saved"), true);
				}
				else
					player.displayClientMessage(Utils.localize("messages.securitycraft:portable_tune_player.no_tune"), true);

				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide) {
			CompoundTag tag = stack.getOrCreateTag();
			boolean isTunePlaying = SCEventHandler.PLAYING_TUNES.containsKey(player);

			if (!isTunePlaying && tag.contains("Notes")) {
				Deque<NoteWrapper> notes = new ArrayDeque<>();

				SonicSecuritySystemBlockEntity.loadNotes(stack.getTag(), notes);
				SCEventHandler.PLAYING_TUNES.put(player, MutablePair.of(0, notes));
				return InteractionResultHolder.success(stack);
			}
			else if (isTunePlaying) {
				SCEventHandler.PLAYING_TUNES.remove(player);
				return InteractionResultHolder.success(stack);
			}
		}

		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
		if (!stack.hasTag())
			return;

		// If a tune is stored in this item, show the number of notes in this tune in the tooltip
		int notesCount = stack.getTag().getList("Notes", Tag.TAG_COMPOUND).size();

		if (notesCount > 0)
			tooltip.add(Utils.localize("tooltip.securitycraft.component.notes", notesCount).withStyle(Utils.GRAY_STYLE));
	}
}
