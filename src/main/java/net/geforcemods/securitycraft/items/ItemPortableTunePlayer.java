package net.geforcemods.securitycraft.items;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem.NoteWrapper;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPortableTunePlayer extends Item {
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.getBlockState(pos).getBlock() == SCContent.sonicSecuritySystem) {
			TileEntitySonicSecuritySystem te = (TileEntitySonicSecuritySystem)world.getTileEntity(pos);

			if (te.getOwner().isOwner(player) || ModuleUtils.isAllowed(te, player)) {
				if (te.getNumberOfNotes() > 0) {
					ItemStack stack = player.getHeldItem(hand);

					if(!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());

					te.saveNotes(stack.getTagCompound());
					player.sendStatusMessage(Utils.localize("messages.securitycraft:portable_tune_player.tune_saved"), true);
				}
				else
					player.sendStatusMessage(Utils.localize("messages.securitycraft:portable_tune_player.no_tune"), true);

				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote) {
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());

			NBTTagCompound tag = stack.getTagCompound();
			boolean isTunePlaying = SCEventHandler.PLAYING_TUNES.containsKey(player);

			if (!isTunePlaying && tag.hasKey("Notes")) {
				Deque<NoteWrapper> notes = new ArrayDeque<>();

				TileEntitySonicSecuritySystem.loadNotes(stack.getTagCompound(), notes);
				SCEventHandler.PLAYING_TUNES.put(player, MutablePair.of(0, notes));
				return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
			}
			else if (isTunePlaying) {
				SCEventHandler.PLAYING_TUNES.remove(player);
				return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
			}
		}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		if(!stack.hasTagCompound())
			return;

		// If a tune is stored in this item, show the number of notes in this tune in the tooltip
		int notesCount = stack.getTagCompound().getTagList("Notes", Constants.NBT.TAG_COMPOUND).tagCount();

		if(notesCount > 0)
			tooltip.add(TextFormatting.GRAY + Utils.localize("tooltip.securitycraft:portableTunePlayer.noteCount", notesCount).getFormattedText());
	}
}
