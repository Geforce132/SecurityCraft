package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
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
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ScannerTrapdoorBlockEntity extends DisguisableBlockEntity implements IViewActivated, ITickableTileEntity, ILockable {
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	protected IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", 0, 0, 400, 5); //20 seconds max
	private DoubleOption maximumDistance = new DoubleOption(this::getBlockPos, "maximumDistance", 5.0D, 0.1D, 25.0D, 0.1D) {
		@Override
		public String getKey(String denotation) {
			return "option.generic.viewActivated.maximumDistance";
		}
	};
	private DisabledOption disabled = new DisabledOption(false);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private int viewCooldown = 0;

	public ScannerTrapdoorBlockEntity() {
		super(SCContent.SCANNER_TRAPDOOR_BLOCK_ENTITY.get());
	}

	@Override
	public void tick() {
		checkView(level, worldPosition);
	}

	@Override
	public boolean onEntityViewed(LivingEntity entity, BlockRayTraceResult hitResult) {
		if (!isConsideredInvisible(entity)) {
			BlockState state = getBlockState();

			if (!(entity instanceof PlayerEntity) || (!isModuleEnabled(ModuleType.DISGUISE) && !(state.getValue(TrapDoorBlock.OPEN) ? hitResult.getDirection().getAxis() == state.getValue(HorizontalBlock.FACING).getAxis() : hitResult.getDirection().getAxis() == Axis.Y)))
				return false;

			PlayerEntity player = (PlayerEntity) entity;

			if (!isLocked() && !isDisabled()) {
				Owner viewingPlayer = new Owner(player);

				if (ConfigHandler.SERVER.trickScannersWithPlayerHeads.get() && player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == Items.PLAYER_HEAD)
					viewingPlayer = PlayerUtils.getSkullOwner(player);

				if (!isOwnedBy(viewingPlayer) && !isAllowed(viewingPlayer.getName())) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_TRAPDOOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner())), TextFormatting.RED);
					return true;
				}

				boolean shouldBeOpen = !state.getValue(TrapDoorBlock.OPEN);

				if (shouldBeOpen && sendMessage.get())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_TRAPDOOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.hello", viewingPlayer.getName()), TextFormatting.GREEN);

				level.setBlockAndUpdate(worldPosition, state.setValue(TrapDoorBlock.OPEN, shouldBeOpen));
				BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.SCANNER_TRAPDOOR.get());
				SCContent.SCANNER_TRAPDOOR.get().playSound(null, level, worldPosition, shouldBeOpen);

				if (getSignalLength() > 0)
					level.getBlockTicks().scheduleTick(worldPosition, SCContent.SCANNER_TRAPDOOR.get(), getSignalLength());
			}
			else {
				if (isLocked() && sendMessage.get()) {
					IFormattableTextComponent blockName = Utils.localize(SCContent.SCANNER_TRAPDOOR.get().getDescriptionId());

					PlayerUtils.sendMessageToPlayer(player, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), TextFormatting.DARK_RED, false);
				}
				else if (isDisabled())
					player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
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
		setChanged();
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
				ModuleType.ALLOWLIST, ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendMessage, signalLength, disabled, maximumDistance, respectInvisibility
		};
	}

	@Override
	public boolean isConsideredInvisible(LivingEntity entity) {
		return respectInvisibility.isConsideredInvisible(entity);
	}
}
