package net.geforcemods.securitycraft.items;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.commons.lang3.tuple.MutablePair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity.NoteWrapper;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

			if (te.getNumberOfNotes() > 0) {
				te.saveNotes(ctx.getItem().getOrCreateTag());
				ctx.getPlayer().sendStatusMessage(Utils.localize("messages.securitycraft:portable_tune_player.tune_saved"), true);
			}
			else
				ctx.getPlayer().sendStatusMessage(Utils.localize("messages.securitycraft:portable_tune_player.no_tune"), true);

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote) {
			CompoundNBT tag = stack.getOrCreateTag();

			if (tag.contains("Notes") && !SCEventHandler.PLAYING_TUNES.containsKey(player)) {
				Deque<NoteWrapper> notes = new ArrayDeque<>();

				SonicSecuritySystemTileEntity.loadNotes(stack.getTag(), notes);
				SCEventHandler.PLAYING_TUNES.put(player, MutablePair.of(SCEventHandler.NOTE_DELAY, notes));
				return ActionResult.resultSuccess(stack);
			}
		}

		return ActionResult.resultPass(stack);
	}
}
