package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.containers.BlockReinforcerContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class UniversalBlockReinforcerItem extends Item
{
	public UniversalBlockReinforcerItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		if(!world.isClientSide && player instanceof ServerPlayerEntity)
		{
			NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
				@Override
				public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
				{
					return new BlockReinforcerContainer(windowId, inv, UniversalBlockReinforcerItem.this == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get());
				}

				@Override
				public ITextComponent getDisplayName()
				{
					return new TranslationTextComponent(getDescriptionId());
				}
			}, data -> data.writeBoolean(this == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()));
		}

		return ActionResult.consume(player.getItemInHand(hand));
	}

	@Override
	public boolean canAttackBlock(BlockState vanillaState, World world, BlockPos pos, PlayerEntity player) //gets rid of the stuttering experienced with onBlockStartBreak
	{
		if(!player.isCreative() && player.getMainHandItem().getItem() == this)
		{
			Block block = vanillaState.getBlock();
			Block rb = IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(block);

			if(rb != null)
			{
				BlockState convertedState = ((IReinforcedBlock)rb).getConvertedState(vanillaState);
				TileEntity te = world.getBlockEntity(pos);
				CompoundNBT tag = null;

				if(te != null)
				{
					tag = te.save(new CompoundNBT());

					if(te instanceof IInventory)
						((IInventory)te).clearContent();
				}

				world.setBlockAndUpdate(pos, convertedState);
				te = world.getBlockEntity(pos);

				if(tag != null)
					te.load(convertedState, tag);

				((IOwnable)te).setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
				player.getMainHandItem().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
				return false;
			}
		}

		return true;
	}
}
