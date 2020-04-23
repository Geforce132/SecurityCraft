package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.WhitelistOnlyTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedPressurePlateBlock extends PressurePlateBlock implements IReinforcedBlock
{
	public static final Block.Properties STONE_PROPERTIES = Block.Properties.create(Material.ROCK).doesNotBlockMovement().hardnessAndResistance(-1.0F, 6000000.0F);
	public static final Block.Properties WOOD_PROPERTIES = Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(-1.0F, 6000000.0F).sound(SoundType.WOOD);
	private final Block vanillaBlock;

	public ReinforcedPressurePlateBlock(Sensitivity sensitivity, Block.Properties properties, Block vanillaBlock)
	{
		super(sensitivity, properties);

		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		int redstoneStrength = getRedstoneStrength(state);

		if(!world.isRemote && redstoneStrength == 0 && entity instanceof PlayerEntity)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if(tileEntity instanceof WhitelistOnlyTileEntity)
			{
				if(isAllowedToPress(world, pos, (WhitelistOnlyTileEntity)tileEntity, (PlayerEntity)entity))
					updateState(world, pos, state, redstoneStrength);
			}
		}
	}

	@Override
	protected int computeRedstoneStrength(World world, BlockPos pos)
	{
		AxisAlignedBB aabb = PRESSURE_AABB.offset(pos);
		List<? extends Entity> list;

		list = world.getEntitiesWithinAABBExcludingEntity(null, aabb);

		if(!list.isEmpty())
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if(tileEntity instanceof WhitelistOnlyTileEntity)
			{
				for(Entity entity : list)
				{
					if(entity instanceof PlayerEntity && isAllowedToPress(world, pos, (WhitelistOnlyTileEntity)tileEntity, (PlayerEntity)entity))
						return 15;
				}
			}
		}

		return 0;
	}

	public boolean isAllowedToPress(World world, BlockPos pos, WhitelistOnlyTileEntity te, PlayerEntity entity)
	{
		return te.getOwner().isOwner(entity) || ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(entity.getName().getUnformattedComponentText().toLowerCase());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlock;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState();
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder)
	{
		return NonNullList.from(ItemStack.EMPTY, new ItemStack(this));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new WhitelistOnlyTileEntity();
	}
}
