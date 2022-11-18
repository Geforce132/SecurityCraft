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
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class DisplayCaseBlockEntity extends CustomizableBlockEntity implements ITickableTileEntity, IPasswordProtected, ILockable {
	private AxisAlignedBB renderBoundingBox = AxisAlignedBB.ofSize(0.0D, 0.0D, 0.0D);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private DisabledOption disabled = new DisabledOption(false);
	private ItemStack displayedStack = ItemStack.EMPTY;
	private boolean shouldBeOpen;
	private float openness;
	private float oOpenness;
	private String passcode;

	public DisplayCaseBlockEntity() {
		super(SCContent.DISPLAY_CASE_BLOCK_ENTITY.get());
	}

	@Override
	public void setLevelAndPosition(World level, BlockPos pos) {
		super.setLevelAndPosition(level, pos);
		renderBoundingBox = new AxisAlignedBB(pos);
	}

	@Override
	public void tick() {
		oOpenness = openness;

		if (!shouldBeOpen && openness > 0.0F)
			openness = Math.max(openness - 0.1F, 0.0F);
		else if (shouldBeOpen && openness < 1.0F)
			openness = Math.min(openness + 0.1F, 1.0F);
	}

	@Override
	public void activate(PlayerEntity player) {
		if (!level.isClientSide) {
			Block block = getBlockState().getBlock();

			if (block instanceof DisplayCaseBlock)
				((DisplayCaseBlock) block).activate(this);
		}
	}

	@Override
	public void openPasswordGUI(PlayerEntity player) {
		if (!level.isClientSide) {
			if (getPassword() != null)
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(DataType.CHECK_PASSWORD, worldPosition));
			else {
				if (getOwner().isOwner(player))
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(DataType.SET_PASSWORD, worldPosition));
				else
					PlayerUtils.sendMessageToPlayer(player, new StringTextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
			}
		}
	}

	@Override
	public boolean onCodebreakerUsed(BlockState state, PlayerEntity player) {
		if (isDisabled()) {
			player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			return false;
		}
		else {
			activate(player);
			return true;
		}
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
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		tag.put("DisplayedStack", getDisplayedStack().save(new CompoundNBT()));
		tag.putBoolean("ShouldBeOpen", shouldBeOpen);

		if (passcode != null && !passcode.isEmpty())
			tag.putString("Passcode", passcode);

		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		load(state, tag, true);
	}

	public void load(BlockState state, CompoundNBT tag, boolean forceOpenness) {
		super.load(state, tag);
		setDisplayedStack(ItemStack.of((CompoundNBT) tag.get("DisplayedStack")));
		shouldBeOpen = tag.getBoolean("ShouldBeOpen");
		passcode = tag.getString("Passcode");

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
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		load(state, tag, false);
	}

	public void setDisplayedStack(ItemStack displayedStack) {
		this.displayedStack = displayedStack;
		sync();
	}

	public ItemStack getDisplayedStack() {
		return displayedStack;
	}

	public void setOpen(boolean shouldBeOpen) {
		level.playSound(null, worldPosition, shouldBeOpen ? SCSounds.DISPLAY_CASE_OPEN.event : SCSounds.DISPLAY_CASE_CLOSE.event, SoundCategory.BLOCKS, 1.0F, 1.0F);
		this.shouldBeOpen = shouldBeOpen;
		sync();
	}

	public void forceOpen(boolean open) {
		shouldBeOpen = open;
		oOpenness = openness = open ? 1.0F : 0.0F;
		sync();
	}

	public float getOpenness(float partialTicks) {
		return MathHelper.lerp(partialTicks, oOpenness, openness);
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
	public AxisAlignedBB getRenderBoundingBox() {
		return renderBoundingBox;
	}
}
