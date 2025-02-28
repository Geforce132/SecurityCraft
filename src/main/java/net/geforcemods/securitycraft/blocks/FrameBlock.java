package net.geforcemods.securitycraft.blocks;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.misc.GlobalPos;
import net.geforcemods.securitycraft.network.client.InteractWithFrame;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class FrameBlock extends OwnableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public FrameBlock(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
		setHardness(5.0F);
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(this::defaultPlayerRelativeBlockHardness, state, player, level, pos, true);
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == state.getValue(FACING) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		TileEntity te = world.getTileEntity(pos);

		if (stack.getItem() == SCContent.cameraMonitor && te instanceof FrameBlockEntity) {
			FrameBlockEntity be = (FrameBlockEntity) te;

			if (!ConfigHandler.frameFeedViewingEnabled)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.frame), Utils.localize("messages.securitycraft:frame.disabled"), TextFormatting.RED);
			else if (!be.isOwnedBy(player))
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.frame), Utils.localize("messages.securitycraft:notOwned", be.getOwner().getName()), TextFormatting.RED);
			else if (stack.hasTagCompound()) {
				List<Pair<GlobalPos, String>> cameras = CameraMonitorItem.getCameraPositions(stack.getTagCompound());

				if (!cameras.isEmpty()) {
					if (be.applyCameraPositions(stack)) {
						be.switchCameras(null, null, 0, false); //Disable current camera view if new cameras are registered to the frame
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.frame), Utils.localize("messages.securitycraft:frame.camerasSet"), TextFormatting.GREEN);
					}
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.frame), Utils.localize("messages.securitycraft:frame.emptyMonitor"), TextFormatting.RED);
			}

			return true;
		}
		else if (stack.getItem() == SCContent.keyPanel) //Conversion takes priority
			return false;
		else if (te instanceof FrameBlockEntity) {
			FrameBlockEntity be = (FrameBlockEntity) te;
			boolean ownedByUser = be.isOwnedBy(player);

			if (be.isDisabled())
				player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (!ConfigHandler.frameFeedViewingEnabled)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.frame), Utils.localize("messages.securitycraft:frame.disabled"), TextFormatting.RED);
			else if (!world.isRemote && player instanceof EntityPlayerMP && (ownedByUser || be.isAllowed(player)) && !be.getCameraPositions().isEmpty())
				SecurityCraft.network.sendTo(new InteractWithFrame(pos, ownedByUser), (EntityPlayerMP) player);

			return true;
		}

		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.byIndex(meta);

		if (facing.getAxis() == EnumFacing.Axis.Y)
			facing = EnumFacing.NORTH;

		return getDefaultState().withProperty(FACING, facing).withProperty(POWERED, meta > 4);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() + (state.getValue(POWERED) ? 4 : 0);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, POWERED);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World level, IBlockState state) {
		return new FrameBlockEntity();
	}
}
