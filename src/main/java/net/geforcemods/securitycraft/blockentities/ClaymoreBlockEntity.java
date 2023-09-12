package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ClaymoreBlockEntity extends CustomizableBlockEntity implements ITickable {
	private IntOption range = new IntOption(this::getPos, "range", 5, 1, 10, 1, true);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private int cooldown = -1;
	private IItemHandler insertOnlyHandler, lensHandler;
	private InventoryBasic lens = new InventoryBasic("", false, 1);

	@Override
	public void update() {
		if (!world.isRemote) {
			IBlockState state = world.getBlockState(pos);

			if (state.getValue(ClaymoreBlock.DEACTIVATED))
				return;

			if (cooldown > 0) {
				cooldown--;
				return;
			}

			if (cooldown == 0) {
				((ClaymoreBlock) getBlockType()).explode(world, pos);
				return;
			}

			EnumFacing dir = state.getValue(ClaymoreBlock.FACING);
			AxisAlignedBB area = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

			if (dir == EnumFacing.NORTH)
				area = area.contract(-0, -0, range.get());
			else if (dir == EnumFacing.SOUTH)
				area = area.contract(-0, -0, -range.get());
			else if (dir == EnumFacing.EAST)
				area = area.contract(-range.get(), -0, -0);
			else if (dir == EnumFacing.WEST)
				area = area.contract(range.get(), -0, -0);

			getWorld().getEntitiesWithinAABB(EntityLivingBase.class, area, e -> !EntityUtils.isInvisible(e) && !allowsOwnableEntity(e) && !(e instanceof EntityPlayer && (((EntityPlayer) e).isCreative() || ((EntityPlayer) e).isSpectator())) && !(EntityUtils.doesEntityOwn(e, world, pos) && ignoresOwner())).stream().findFirst().ifPresent(entity -> {
				cooldown = 20;
				getWorld().playSound(null, new BlockPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, 0.6F);
			});
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("cooldown", cooldown);
		tag.setTag("lens", lens.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		cooldown = tag.getInteger("cooldown");
		lens.setInventorySlotContents(0, new ItemStack(tag.getCompoundTag("lens")));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) BlockUtils.getProtectedCapability(facing, this, this::getNormalHandler, this::getInsertOnlyHandler);
		else
			return super.getCapability(capability, facing);
	}

	private IItemHandler getInsertOnlyHandler() {
		if (insertOnlyHandler == null)
			insertOnlyHandler = new InsertOnlyInvWrapper(lens);

		return insertOnlyHandler;
	}

	private IItemHandler getNormalHandler() {
		if (lensHandler == null)
			lensHandler = new InvWrapper(lens);

		return lensHandler;
	}

	public InventoryBasic getLensContainer() {
		return lens;
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				range, ignoreOwner
		};
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[0];
	}

	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos);
	}
}
