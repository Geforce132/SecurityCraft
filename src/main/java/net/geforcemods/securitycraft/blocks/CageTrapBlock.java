package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.tileentity.CageTrapTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CageTrapBlock extends OwnableBlock implements IIntersectable {

	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");

	public CageTrapBlock(Material material) {
		super(SoundType.METAL, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F).doesNotBlockMovement());
		setDefaultState(stateContainer.getBaseState().with(DEACTIVATED, false));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx){
		return state.get(DEACTIVATED) ? VoxelShapes.fullCube() : VoxelShapes.empty();
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote){
			CageTrapTileEntity tileEntity = (CageTrapTileEntity) world.getTileEntity(pos);
			boolean isPlayer = entity instanceof PlayerEntity;
			boolean shouldCaptureMobs = tileEntity.getOptionByName("captureMobs").asBoolean();

			if(isPlayer || (entity instanceof MobEntity && shouldCaptureMobs)){
				if((isPlayer && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner((PlayerEntity)entity)))
					return;

				if(BlockUtils.getBlockPropertyAsBoolean(world, pos, DEACTIVATED))
					return;

				BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
				BlockUtils.setBlock(world, pos.up(4), SCContent.reinforcedIronBars);
				BlockUtils.setBlock(world, pos.up(3), SCContent.reinforcedIronBars);
				BlockUtils.setBlock(world, pos.getX() + 1, pos.getY() + 4, pos.getZ(), SCContent.reinforcedIronBars);
				BlockUtils.setBlock(world, pos.getX() - 1, pos.getY() + 4, pos.getZ(), SCContent.reinforcedIronBars);
				BlockUtils.setBlock(world, pos.getX(), pos.getY() + 4, pos.getZ() + 1, SCContent.reinforcedIronBars);
				BlockUtils.setBlock(world, pos.getX(), pos.getY() + 4, pos.getZ() - 1, SCContent.reinforcedIronBars);

				BlockUtils.setBlockInBox(world, pos.getX(), pos.getY(), pos.getZ(), SCContent.reinforcedIronBars);
				setTileEntities(world, pos.getX(), pos.getY(), pos.getZ(), ((IOwnable)world.getTileEntity(pos)).getOwner().getUUID(), ((IOwnable)world.getTileEntity(pos)).getOwner().getName());

				world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 3.0F, 1.0F);

				if(isPlayer)
					entity.sendMessage(new TranslationTextComponent("["+ TextFormatting.BLACK + ClientUtils.localize(SCContent.cageTrap.getTranslationKey()) + TextFormatting.RESET + "] " + ClientUtils.localize("messages.securitycraft:cageTrap.captured").replace("#player", ((PlayerEntity) entity).getName().getFormattedText()).replace("#location", Utils.getFormattedCoordinates(pos))));
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(DEACTIVATED, false);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(DEACTIVATED);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new CageTrapTileEntity().intersectsEntities();
	}

	public void setTileEntities(World world, int x, int y, int z, String uuid, String name)
	{
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y, z))).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 4, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 3, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 4, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 4, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 4, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 4, z - 1))).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 1, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 2, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 3, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 1, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 2, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 3, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 1, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 2, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 3, z))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 1, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 2, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 3, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 1, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 2, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 3, z + 1))).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 1, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 2, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x, y + 3, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 1, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 2, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 3, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 1, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 2, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 3, z - 1))).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 4, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x + 1, y + 4, z - 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 4, z + 1))).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(BlockUtils.toPos(x - 1, y + 4, z - 1))).getOwner().set(uuid, name);
	}
}
