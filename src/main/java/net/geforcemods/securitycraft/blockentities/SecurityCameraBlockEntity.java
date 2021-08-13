package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SecurityCameraBlockEntity extends CustomizableBlockEntity {

	public double cameraRotation = 0.0D;
	public boolean addToRotation = true;
	public boolean down = false, downSet = false;
	public float lastPitch = Float.MAX_VALUE;
	public float lastYaw = Float.MAX_VALUE;
	private DoubleOption rotationSpeedOption = new DoubleOption(this::getBlockPos, "rotationSpeed", 0.018D, 0.01D, 0.025D, 0.001D, true);
	private BooleanOption shouldRotateOption = new BooleanOption("shouldRotate", true);
	private DoubleOption customRotationOption = new DoubleOption(this::getBlockPos, "customRotation", cameraRotation, 1.55D, -1.55D, rotationSpeedOption.get(), true);

	public SecurityCameraBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.teTypeSecurityCamera, pos, state);
	}

	public static void tick(Level world, BlockPos pos, BlockState state, SecurityCameraBlockEntity te){
		CustomizableBlockEntity.tick(world, pos, state, te);

		if(!te.downSet)
		{
			te.down = state.getValue(SecurityCameraBlock.FACING) == Direction.DOWN;
			te.downSet = true;
		}

		if(!te.shouldRotateOption.get())
		{
			te.cameraRotation = te.customRotationOption.get();
			return;
		}

		if(te.addToRotation && te.cameraRotation <= 1.55F)
			te.cameraRotation += te.rotationSpeedOption.get();
		else
			te.addToRotation = false;

		if(!te.addToRotation && te.cameraRotation >= -1.55F)
			te.cameraRotation -= te.rotationSpeedOption.get();
		else
			te.addToRotation = true;
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		tag.putFloat("LastPitch", lastPitch);
		tag.putFloat("LastYaw", lastYaw);
		return super.save(tag);
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		lastPitch = tag.getFloat("LastPitch");
		lastYaw = tag.getFloat("LastYaw");
	}

	@Override
	public ModuleType[] acceptedModules(){
		return new ModuleType[] { ModuleType.REDSTONE, ModuleType.SMART };
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ rotationSpeedOption, shouldRotateOption, customRotationOption };
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(module == ModuleType.REDSTONE)
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecurityCameraBlock.POWERED, false));
	}
}
