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
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ScannerTrapdoorBlockEntity extends DisguisableBlockEntity implements IViewActivated, ITickingBlockEntity, ILockable {
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	protected IntOption signalLength = new IntOption("signalLength", 0, 0, 400, 5); //20 seconds max
	private DoubleOption maximumDistance = new DoubleOption("maximumDistance", 5.0D, 0.1D, 25.0D, 0.1D) {
		@Override
		public String getKey(String denotation) {
			return "option.generic.viewActivated.maximumDistance";
		}
	};
	private DisabledOption disabled = new DisabledOption(false);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private int viewCooldown = 0;

	public ScannerTrapdoorBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.SCANNER_TRAPDOOR_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		checkView(level, pos);
	}

	@Override
	public boolean onEntityViewed(LivingEntity entity, BlockHitResult hitResult) {
		if (!isConsideredInvisible(entity)) {
			BlockState state = getBlockState();

			if (!(entity instanceof Player player) || (!isModuleEnabled(ModuleType.DISGUISE) && !(state.getValue(TrapDoorBlock.OPEN) ? hitResult.getDirection().getAxis() == state.getValue(HorizontalDirectionalBlock.FACING).getAxis() : hitResult.getDirection().getAxis() == Axis.Y)))
				return false;

			if (!isLocked() && !isDisabled()) {
				Owner viewingPlayer = new Owner(player);

				if (ConfigHandler.SERVER.trickScannersWithPlayerHeads.get() && player.getItemBySlot(EquipmentSlot.HEAD).getItem() == Items.PLAYER_HEAD)
					viewingPlayer = PlayerUtils.getSkullOwner(player);

				if (!isOwnedBy(viewingPlayer) && !isAllowed(viewingPlayer.getName())) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_TRAPDOOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner())), ChatFormatting.RED);
					return true;
				}

				boolean shouldBeOpen = !state.getValue(TrapDoorBlock.OPEN);

				if (shouldBeOpen && sendMessage.get())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SCANNER_TRAPDOOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.hello", viewingPlayer.getName()), ChatFormatting.GREEN);

				level.setBlockAndUpdate(worldPosition, state.setValue(TrapDoorBlock.OPEN, shouldBeOpen));
				BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.SCANNER_TRAPDOOR.get());
				SCContent.SCANNER_TRAPDOOR.get().playSound(null, level, worldPosition, shouldBeOpen);

				if (getSignalLength() > 0)
					level.scheduleTick(worldPosition, SCContent.SCANNER_TRAPDOOR.get(), getSignalLength());
			}
			else {
				if (isLocked() && sendMessage.get()) {
					MutableComponent blockName = Utils.localize(SCContent.SCANNER_TRAPDOOR.get().getDescriptionId());

					PlayerUtils.sendMessageToPlayer(player, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), ChatFormatting.DARK_RED, false);
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
