package net.geforcemods.securitycraft.items;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.MutablePair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.components.Notes;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
					ctx.getItemInHand().set(SCContent.NOTES, new Notes(new ArrayList<>(be.getRecordedNotes())));
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
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide()) {
			boolean isTunePlaying = SCEventHandler.PLAYING_TUNES.containsKey(player);

			if (isTunePlaying) {
				SCEventHandler.PLAYING_TUNES.remove(player);
				return InteractionResult.SUCCESS_SERVER;
			}
			else {
				Notes notes = stack.get(SCContent.NOTES);

				if (notes != null) {
					SCEventHandler.PLAYING_TUNES.put(player, MutablePair.of(0, new ArrayList<>(notes.notes())));
					return InteractionResult.SUCCESS_SERVER;
				}
			}
		}

		return InteractionResult.PASS;
	}
}
