package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IViewActivated;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class ScannerTrapdoorBlockEntity extends CustomizableBlockEntity implements IViewActivated, ITickable, ILockable {
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	protected IntOption signalLength = new IntOption(this::getPos, "signalLength", 0, 0, 400, 5); //20 seconds max
	private DoubleOption maximumDistance = new DoubleOption(this::getPos, "maximumDistance", 5.0D, 0.1D, 25.0D, 0.1D) {
		@Override
		public String getKey(String denotation) {
			return "option.generic.viewActivated.maximumDistance";
		}
	};
	private DisabledOption disabled = new DisabledOption(false);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private int viewCooldown = 0;

	@Override
	public void update() {
		checkView(world, pos);
	}

	@Override
	public boolean onEntityViewed(EntityLivingBase entity, RayTraceResult hitResult) {
		if (!respectInvisibility.isConsideredInvisible(entity)) {
			IBlockState state = world.getBlockState(pos);

			if (!(entity instanceof EntityPlayer) || !(state.getValue(BlockTrapDoor.OPEN) ? hitResult.sideHit.getAxis() == state.getValue(BlockHorizontal.FACING).getAxis() : hitResult.sideHit.getAxis() == Axis.Y))
				return false;

			EntityPlayer player = (EntityPlayer) entity;

			if (!isLocked() && !isDisabled()) {
				Owner viewingPlayer = new Owner(player);

				if (ConfigHandler.trickScannersWithPlayerHeads && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.SKULL)
					viewingPlayer = PlayerUtils.getSkullOwner(player);

				if (!isOwnedBy(viewingPlayer) && !isAllowed(viewingPlayer.getName())) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.scannerTrapdoor), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner())), TextFormatting.RED);
					return true;
				}

				boolean shouldBeOpen = !state.getValue(BlockTrapDoor.OPEN);

				if (shouldBeOpen && sendMessage.get())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.scannerTrapdoor), Utils.localize("messages.securitycraft:retinalScanner.hello", viewingPlayer.getName()), TextFormatting.GREEN);

				world.setBlockState(pos, state.withProperty(BlockTrapDoor.OPEN, shouldBeOpen));
				BlockUtils.updateIndirectNeighbors(world, pos, SCContent.scannerTrapdoor);
				world.markBlockRangeForRenderUpdate(pos.down(), pos);
				SCContent.scannerTrapdoor.playSound(null, world, pos, shouldBeOpen);

				if (getSignalLength() > 0)
					world.scheduleUpdate(pos, SCContent.scannerTrapdoor, getSignalLength());
			}
			else {
				if (isLocked() && sendMessage.get()) {
					ITextComponent blockName = Utils.localize(SCContent.scannerTrapdoor);

					PlayerUtils.sendMessageToPlayer(player, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), TextFormatting.DARK_RED, false);
				}
				else if (isDisabled())
					player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			}

			return true;
		}

		return false;
	}

	@Override
	public int getViewCooldown() {
		return viewCooldown;
	}

	@Override
	public void setViewCooldown(int viewCooldown) {
		this.viewCooldown = viewCooldown;
		markDirty();
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public double getMaximumDistance() {
		return maximumDistance.get();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendMessage, signalLength, disabled, maximumDistance, respectInvisibility
		};
	}
}
