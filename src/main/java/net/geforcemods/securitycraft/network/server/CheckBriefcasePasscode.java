package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class CheckBriefcasePasscode implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "check_briefcase_passcode");
	private String passcode;

	public CheckBriefcasePasscode() {}

	public CheckBriefcasePasscode(String passcode) {
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public CheckBriefcasePasscode(FriendlyByteBuf buf) {
		passcode = buf.readUtf();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(passcode);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		ItemStack briefcase = PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get());

		if (!briefcase.isEmpty()) {
			CompoundTag tag = Utils.getTag(briefcase).getUnsafe();
			String tagCode = tag.getString("passcode");

			if (tagCode.length() == 4) //If an old plaintext passcode is encountered, generate and check with the hashed variant
				BriefcaseItem.hashAndSetPasscode(tag, PasscodeUtils.hashPasscodeWithoutSalt(tagCode), p -> BriefcaseItem.checkPasscode(player, briefcase, passcode, PasscodeUtils.bytesToString(p), tag));
			else
				BriefcaseItem.checkPasscode(player, briefcase, passcode, tagCode, tag);
		}
	}
}
