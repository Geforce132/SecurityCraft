package net.geforcemods.securitycraft.blockentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;

public class SecurityCameraBlockEntity extends CustomizableBlockEntity implements ITickableTileEntity {
	public double cameraRotation = 0.0D;
	public boolean addToRotation = true;
	public boolean down = false, downSet = false;
	private int playersViewing = 0;
	private DoubleOption rotationSpeedOption = new DoubleOption(this::getBlockPos, "rotationSpeed", 0.018D, 0.01D, 0.025D, 0.001D, true);
	private BooleanOption shouldRotateOption = new BooleanOption("shouldRotate", true);
	private DoubleOption customRotationOption = new DoubleOption(this::getBlockPos, "customRotation", cameraRotation, 1.55D, -1.55D, rotationSpeedOption.get(), true);

	public SecurityCameraBlockEntity() {
		super(SCContent.beTypeSecurityCamera);
	}

	@Override
	public void tick() {
		if (!downSet) {
			down = getBlockState().getValue(SecurityCameraBlock.FACING) == Direction.DOWN;
			downSet = true;
		}

		if (!shouldRotateOption.get()) {
			cameraRotation = customRotationOption.get();
			return;
		}

		if (addToRotation && cameraRotation <= 1.55F)
			cameraRotation += rotationSpeedOption.get();
		else
			addToRotation = false;

		if (!addToRotation && cameraRotation >= -1.55F)
			cameraRotation -= rotationSpeedOption.get();
		else
			addToRotation = true;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.REDSTONE, ModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				rotationSpeedOption, shouldRotateOption, customRotationOption
		};
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module) {
		super.onModuleRemoved(stack, module);

		if (module == ModuleType.REDSTONE)
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecurityCameraBlock.POWERED, false));
	}

	public void startViewing() {
		if (playersViewing++ == 0)
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecurityCameraBlock.BEING_VIEWED, true));
	}

	public void stopViewing() {
		if (--playersViewing == 0)
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecurityCameraBlock.BEING_VIEWED, false));
	}
}
