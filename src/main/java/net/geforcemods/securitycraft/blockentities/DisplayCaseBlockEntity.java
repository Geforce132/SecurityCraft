package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.blocks.DisplayCaseBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

public class DisplayCaseBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity, IPasswordProtected, ILockable {
	private final AABB renderBoundingBox;
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private DisabledOption disabled = new DisabledOption(false);
	private ItemStack displayedStack = ItemStack.EMPTY;
	private boolean shouldBeOpen;
	private float openness;
	private float oOpenness;
	private String passcode;

	public DisplayCaseBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.DISPLAY_CASE_BLOCK_ENTITY.get(), pos, state);
		renderBoundingBox = new AABB(pos);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		oOpenness = openness;

		if (!shouldBeOpen && openness > 0.0F)
			openness = Math.max(openness - 0.1F, 0.0F);
		else if (shouldBeOpen && openness < 1.0F)
			openness = Math.min(openness + 0.1F, 1.0F);
	}

	@Override
	public void activate(Player player) {
		if (!level.isClientSide && getBlockState().getBlock() instanceof DisplayCaseBlock block)
			block.activate(this);
	}

	@Override
	public void openPasswordGUI(Player player) {
		if (!level.isClientSide) {
			if (getPassword() != null)
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.CHECK_PASSWORD, worldPosition));
			else {
				if (getOwner().isOwner(player))
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.SET_PASSWORD, worldPosition));
				else
					PlayerUtils.sendMessageToPlayer(player, Component.literal("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), ChatFormatting.DARK_RED);
			}
		}
	}

	@Override
	public boolean onCodebreakerUsed(BlockState state, Player player) {
		if (!state.getValue(KeypadBlock.POWERED)) {
			if (isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else {
				activate(player);
				return true;
			}
		}

		return false;
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
		setChanged();
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("DisplayedStack", getDisplayedStack().save(new CompoundTag()));
		tag.putBoolean("ShouldBeOpen", shouldBeOpen);
	}

	@Override
	public void load(CompoundTag tag) {
		load(tag, true);
	}

	public void load(CompoundTag tag, boolean forceOpenness) {
		super.load(tag);
		setDisplayedStack(ItemStack.of((CompoundTag) tag.get("DisplayedStack")));
		shouldBeOpen = tag.getBoolean("ShouldBeOpen");

		if (forceOpenness)
			forceOpen(shouldBeOpen);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendMessage, disabled
		};
	}

	public boolean sendsMessages() {
		return sendMessage.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		load(tag, false);
	}

	public void setDisplayedStack(ItemStack displayedStack) {
		this.displayedStack = displayedStack;
		sync();
	}

	public ItemStack getDisplayedStack() {
		return displayedStack;
	}

	public void setOpen(boolean shouldBeOpen) {
		this.shouldBeOpen = shouldBeOpen;
		sync();
	}

	public void forceOpen(boolean open) {
		shouldBeOpen = open;
		oOpenness = openness = open ? 1.0F : 0.0F;
		sync();
	}

	public float getOpenness(float partialTicks) {
		return Mth.lerp(partialTicks, oOpenness, openness);
	}

	public boolean isOpen() {
		return shouldBeOpen;
	}

	private void sync() {
		if (level != null && !level.isClientSide) {
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		}
	}

	@Override
	public AABB getRenderBoundingBox() {
		return renderBoundingBox;
	}
}
