package net.geforcemods.securitycraft;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler.ClientConfig;
import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockCageTrap;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.PortalSize;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.misc.SCWorldListener;
import net.geforcemods.securitycraft.misc.TEInteractionObject;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class SCEventHandler {

	public static HashMap<String, String> tipsWithLink = new HashMap<String, String>();

	public SCEventHandler()
	{
		tipsWithLink.put("trello", "https://trello.com/b/dbCNZwx0/securitycraft");
		tipsWithLink.put("patreon", "https://www.patreon.com/Geforce");
		tipsWithLink.put("discord", "https://discord.gg/U8DvBAW");
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		if(!ClientConfig.CONFIG.sayThanksMessage.get() || !event.getPlayer().getEntityWorld().isRemote)
			return;

		String tipKey = getRandomTip();
		ITextComponent message;

		if(tipsWithLink.containsKey(tipKey.split("\\.")[2]))
			message = new TextComponentString("[" + TextFormatting.GOLD + "SecurityCraft" + TextFormatting.WHITE + "] " + ClientUtils.localize("messages.securitycraft:thanks").replace("#", SecurityCraft.getVersion()) + " " + ClientUtils.localize("messages.securitycraft:tip") + " " + ClientUtils.localize(tipKey) + " ").appendSibling(ForgeHooks.newChatWithLinks(tipsWithLink.get(tipKey.split("\\.")[2])));
		else
			message = new TextComponentString("[" + TextFormatting.GOLD + "SecurityCraft" + TextFormatting.WHITE + "] " + ClientUtils.localize("messages.securitycraft:thanks").replace("#", SecurityCraft.getVersion()) + " " + ClientUtils.localize("messages.securitycraft:tip") + " " + ClientUtils.localize(tipKey));

		event.getPlayer().sendMessage(message);
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(event.getPlayer()) && event.getPlayer().getRidingEntity() instanceof EntitySecurityCamera)
			event.getPlayer().getRidingEntity().remove();
	}

	@SubscribeEvent
	public void onDamageTaken(LivingHurtEvent event)
	{
		if(event.getEntityLiving() != null && PlayerUtils.isPlayerMountedOnCamera(event.getEntityLiving())){
			event.setCanceled(true);
			return;
		}

		if(event.getSource() == CustomDamageSources.electricity)
			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, SCSounds.ELECTRIFIED.path, 0.25F, "block"));
	}

	@SubscribeEvent
	public void onBucketUsed(FillBucketEvent event){
		if(event.getTarget() == null)
			return;

		ItemStack result = fillBucket(event.getWorld(), event.getTarget().getBlockPos());
		if(result.isEmpty())
			return;
		event.setFilledBucket(result);
		event.setResult(Result.ALLOW);
	}

	@SubscribeEvent
	public void onRightClickBlock(RightClickBlock event){
		if(event.getHand() == EnumHand.MAIN_HAND)
		{
			World world = event.getWorld();

			if(!world.isRemote){
				TileEntity tileEntity = world.getTileEntity(event.getPos());
				Block block = world.getBlockState(event.getPos()).getBlock();

				if(PlayerUtils.isHoldingItem(event.getEntityPlayer(), SCContent.codebreaker) && handleCodebreaking(event)) {
					event.setCanceled(true);
					return;
				}

				if(tileEntity != null && tileEntity instanceof CustomizableSCTE && PlayerUtils.isHoldingItem(event.getEntityPlayer(), SCContent.universalBlockModifier)){
					event.setCanceled(true);

					if(!((IOwnable) tileEntity).getOwner().isOwner(event.getEntityPlayer())){
						PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), ClientUtils.localize("item.securitycraft:universalBlockModifier.name"), ClientUtils.localize("messages.securitycraft:notOwned").replace("#", ((IOwnable) tileEntity).getOwner().getName()), TextFormatting.RED);
						return;
					}

					if(event.getEntityPlayer() instanceof EntityPlayerMP)
						NetworkHooks.openGui((EntityPlayerMP)event.getEntityPlayer(), new TEInteractionObject(GuiHandler.KEY_CHANGER, world, event.getPos()));

					return;
				}

				if(tileEntity instanceof INameable && ((INameable) tileEntity).canBeNamed() && PlayerUtils.isHoldingItem(event.getEntityPlayer(), Items.NAME_TAG) && event.getEntityPlayer().inventory.getCurrentItem().hasDisplayName()){
					event.setCanceled(true);

					for(String character : new String[]{"(", ")"})
						if(event.getEntityPlayer().inventory.getCurrentItem().getDisplayName().getFormattedText().contains(character)) {
							PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), "Naming", ClientUtils.localize("messages.securitycraft:naming.error").replace("#n", event.getEntityPlayer().inventory.getCurrentItem().getDisplayName().getFormattedText()).replace("#c", character), TextFormatting.RED);
							return;
						}

					if(((INameable) tileEntity).getCustomName().equals(event.getEntityPlayer().inventory.getCurrentItem().getDisplayName())) {
						PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), "Naming", ClientUtils.localize("messages.securitycraft:naming.alreadyMatches").replace("#n", ((INameable) tileEntity).getCustomName().getFormattedText()), TextFormatting.RED);
						return;
					}

					if(!event.getEntityPlayer().isCreative())
						event.getEntityPlayer().inventory.getCurrentItem().shrink(1);

					((INameable) tileEntity).setCustomName(event.getEntityPlayer().inventory.getCurrentItem().getDisplayName());
					return;
				}

				if(tileEntity != null && isOwnableBlock(block, tileEntity) && PlayerUtils.isHoldingItem(event.getEntityPlayer(), SCContent.universalBlockRemover)){
					event.setCanceled(true);

					if(!((IOwnable) tileEntity).getOwner().isOwner(event.getEntityPlayer())){
						PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), ClientUtils.localize("item.securitycraft:universalBlockRemover.name"), ClientUtils.localize("messages.securitycraft:notOwned").replace("#", ((IOwnable) tileEntity).getOwner().getName()), TextFormatting.RED);
						return;
					}

					if(block == SCContent.laserBlock){
						world.destroyBlock(event.getPos(), true);
						BlockLaserBlock.destroyAdjacentLasers(event.getWorld(), event.getPos());
						event.getEntityPlayer().inventory.getCurrentItem().damageItem(1, event.getEntityPlayer());
					}else if(block == SCContent.cageTrap && world.getBlockState(event.getPos()).get(BlockCageTrap.DEACTIVATED)) {
						BlockPos originalPos = event.getPos();
						BlockPos pos = originalPos.east().up();

						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up(2);
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up(3);
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up().south();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up(2).south();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up(3).south();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up(2);
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up(3);
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up().south();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up(2).south();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up(3).south();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.up().south();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.up(2).south();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.up(3).south();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up(2);
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up(3);
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.up().north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.up(2).north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.up(3).north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up().north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up(2).north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up(3).north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up().north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up(2).north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up(3).north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up(4).south();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up(4).north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up(4).south();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up(4).north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up(4).north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.east().up(4);
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.west().up(4);
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.up(4).south();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.up(4).north();
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						pos = originalPos.up(4);
						if(BlockUtils.getBlock(world, pos) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner(event.getEntityPlayer()))
							BlockUtils.destroyBlock(world, pos, false);

						BlockUtils.destroyBlock(world, originalPos, false);
					}else{
						world.destroyBlock(event.getPos(), true);
						world.removeTileEntity(event.getPos());
						event.getEntityPlayer().inventory.getCurrentItem().damageItem(1, event.getEntityPlayer());
					}

					return;
				}
			}

			//outside !world.isRemote for properly checking the interaction
			//all the sentry functionality for when the sentry is diguised
			List<EntitySentry> sentries = world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(event.getPos()));

			if(!sentries.isEmpty())
				event.setCanceled(sentries.get(0).processInteract(event.getEntityPlayer(), event.getHand())); //cancel if an action was taken
		}
	}

	@SubscribeEvent
	public void onBlockEventBreak(BlockEvent.BreakEvent event)
	{
		if(!(event.getWorld() instanceof World))
			return;

		List<EntitySentry> sentries = ((World)event.getWorld()).getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(event.getPos()));

		//don't let people break the disguise block
		if(!sentries.isEmpty())
		{
			event.setCanceled(true);
			return;
		}

		sentries = ((World)event.getWorld()).getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(event.getPos().up()));

		//remove sentry if block below is broken
		if(!sentries.isEmpty())
			sentries.get(0).remove();
	}

	@SubscribeEvent
	public void onBlockPlaced(PlaceEvent event) {
		handleOwnableTEs(event);

		//reinforced obsidian portal handling
		if(event.getState().getBlock() == Blocks.FIRE && event.getWorld().getBlockState(event.getPos().down()).getBlock() == SCContent.reinforcedObsidian)
		{
			PortalSize portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.X);

			if (portalSize.isValid() && portalSize.getPortalBlockCount() == 0)
				portalSize.placePortalBlocks();
			else
			{
				portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.Z);

				if (portalSize.isValid() && portalSize.getPortalBlockCount() == 0)
					portalSize.placePortalBlocks();
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		//fix for spawning under the portal
		if(event.getEntity() instanceof EntityPlayer && !event.getWorld().isRemote) //nether
		{
			BlockPos pos = event.getEntity().getPosition();

			//check for obsidian or reinforced obsidian from the player's position up to the world height
			do
			{
				if(event.getWorld().getBlockState(pos).getBlock() == Blocks.OBSIDIAN)
				{
					//check if the block is part of a valid portal, and if so move the entity down
					BlockPortal.Size portalSize = new BlockPortal.Size(event.getWorld(), pos, EnumFacing.Axis.X);

					if (portalSize.isValid())
					{
						double y = pos.getY() + 0.5D;

						if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.NETHER_PORTAL) //sometimes the top of the portal is more valid than the bottom o.O
							y -= 3.0D;

						event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
						break;
					}
					else //check other axis
					{
						portalSize = new BlockPortal.Size(event.getWorld(), pos, EnumFacing.Axis.Z);

						if (portalSize.isValid())
						{
							double y = pos.getY() + 0.5D;

							if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.NETHER_PORTAL)
								y -= 3.0D;

							event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
							break;
						}
					}
				}
				else if(event.getWorld().getBlockState(pos).getBlock() == SCContent.reinforcedObsidian) //analogous to if check above
				{
					PortalSize portalSize = new PortalSize(event.getWorld(), pos, EnumFacing.Axis.X);

					if (portalSize.isValid())
					{
						double y = pos.getY() + 0.5D;

						if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.NETHER_PORTAL)
							y -= 3.0D;

						event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
						break;
					}
					else
					{
						portalSize = new PortalSize(event.getWorld(), pos, EnumFacing.Axis.Z);

						if (portalSize.isValid())
						{
							double y = pos.getY() + 0.5D;

							if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.NETHER_PORTAL)
								y -= 3.0D;

							event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
							break;
						}
					}
				}
			}
			while((pos = pos.up()).getY() < event.getWorld().getHeight());

		}
	}

	@SubscribeEvent
	public void onNeighborNotify(NeighborNotifyEvent event)
	{
		//prevent portal blocks from disappearing because they think they're not inside of a proper portal frame
		if(event.getState().getBlock() == Blocks.NETHER_PORTAL)
		{
			EnumFacing.Axis axis = event.getState().get(BlockPortal.AXIS);

			if (axis == EnumFacing.Axis.X)
			{
				PortalSize portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.X);

				if (portalSize.isValid() || portalSize.getPortalBlockCount() > portalSize.getWidth() * portalSize.getHeight())
					event.setCanceled(true);
			}
			else if (axis == EnumFacing.Axis.Z)
			{
				PortalSize portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.Z);

				if (portalSize.isValid() || portalSize.getPortalBlockCount() > portalSize.getWidth() * portalSize.getHeight())
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onBlockBroken(BreakEvent event){
		if(event.getWorld() instanceof World && !event.getWorld().isRemote())
			if(event.getWorld().getTileEntity(event.getPos()) != null && event.getWorld().getTileEntity(event.getPos()) instanceof CustomizableSCTE){
				CustomizableSCTE te = (CustomizableSCTE) event.getWorld().getTileEntity(event.getPos());

				for(int i = 0; i < te.getNumberOfCustomizableOptions(); i++)
					if(!te.modules.get(i).isEmpty()){
						ItemStack stack = te.modules.get(i);
						EntityItem item = new EntityItem((World)event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), stack);
						WorldUtils.addScheduledTask(event.getWorld(), () -> event.getWorld().spawnEntity(item));

						te.onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
						te.createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, te);

						if(te instanceof TileEntitySecurityCamera)
							te.getWorld().notifyNeighborsOfStateChange(te.getPos().offset(te.getWorld().getBlockState(te.getPos()).get(BlockSecurityCamera.FACING), -1), te.getWorld().getBlockState(te.getPos()).getBlock(), true);
					}
			}
	}

	@SubscribeEvent
	public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		if(event.getTarget() instanceof EntityPlayer && event.getTarget() != event.getEntityLiving().getAttackingEntity())
		{
			if(PlayerUtils.isPlayerMountedOnCamera(event.getTarget()))
				((EntityLiving)event.getEntityLiving()).setAttackTarget(null);
		}
		else if(event.getTarget() instanceof EntitySentry)
			((EntityLiving)event.getEntityLiving()).setAttackTarget(null);
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		if(event.getWorld() instanceof World)
			((World)event.getWorld()).addEventListener(new SCWorldListener());
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		if(event.getEntityPlayer() != null)
		{
			Item held = event.getEntityPlayer().getHeldItemMainhand().getItem();

			if(held == SCContent.universalBlockReinforcerLvL1 || held == SCContent.universalBlockReinforcerLvL2 || held == SCContent.universalBlockReinforcerLvL3)
			{
				for(Block rb : IReinforcedBlock.BLOCKS)
				{
					IReinforcedBlock reinforcedBlock = (IReinforcedBlock)rb;

					if(reinforcedBlock.getVanillaBlocks().contains(event.getState().getBlock()))
					{
						event.setNewSpeed(10000.0F);
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRendered(RenderPlayerEvent.Pre event) {
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntityPlayer()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onDrawBlockHighlight(DrawBlockHighlightEvent event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().player.getRidingEntity().getPosition().equals(event.getTarget().getBlockPos()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void renderGameOverlay(RenderGameOverlayEvent event) {
		if(Minecraft.getInstance().player != null && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player)){
			if(event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && ((BlockUtils.getBlock(Minecraft.getInstance().world, BlockUtils.toPos((int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posX), (int)Minecraft.getInstance().player.getRidingEntity().posY, (int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posZ))) instanceof BlockSecurityCamera)))
				GuiUtils.drawCameraOverlay(Minecraft.getInstance(), Minecraft.getInstance().ingameGUI, Minecraft.getInstance().mainWindow, Minecraft.getInstance().player, Minecraft.getInstance().world, BlockUtils.toPos((int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posX), (int)Minecraft.getInstance().player.getRidingEntity().posY, (int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posZ)));
		}
		else if(event.getType() == ElementType.HOTBAR)
		{
			Minecraft mc = Minecraft.getInstance();
			EntityPlayerSP player = mc.player;
			World world = player.getEntityWorld();
			int held = player.inventory.currentItem;

			if(held < 0 || held >= player.inventory.mainInventory.size())
				return;

			ItemStack monitor = player.inventory.mainInventory.get(held);

			if(!monitor.isEmpty() && monitor.getItem() == SCContent.cameraMonitor)
			{
				String textureToUse = "camera_not_bound";
				double eyeHeight = player.getEyeHeight();
				Vec3d lookVec = new Vec3d((player.posX + (player.getLookVec().x * 5)), ((eyeHeight + player.posY) + (player.getLookVec().y * 5)), (player.posZ + (player.getLookVec().z * 5)));
				RayTraceResult mop = world.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec);

				if(mop != null && mop.type == Type.BLOCK && world.getTileEntity(mop.getBlockPos()) instanceof TileEntitySecurityCamera)
				{
					NBTTagCompound cameras = monitor.getTag();

					if(cameras != null)
						for(int i = 1; i < 31; i++)
						{
							if(!cameras.contains("Camera" + i))
								continue;

							String[] coords = cameras.getString("Camera" + i).split(" ");

							if(Integer.parseInt(coords[0]) == mop.getBlockPos().getX() && Integer.parseInt(coords[1]) == mop.getBlockPos().getY() && Integer.parseInt(coords[2]) == mop.getBlockPos().getZ())
							{
								textureToUse = "camera_bound";
								break;
							}
						}

					GlStateManager.enableAlphaTest();
					Minecraft.getInstance().textureManager.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					drawNonStandardTexturedRect(Minecraft.getInstance().mainWindow.getScaledWidth() / 2 - 90 + held * 20 + 2, Minecraft.getInstance().mainWindow.getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					GlStateManager.disableAlphaTest();
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingDestroyEvent(LivingDestroyBlockEvent event)
	{
		event.setCanceled(event.getEntity() instanceof EntityWither && event.getState().getBlock() instanceof IReinforcedBlock);
	}

	@SubscribeEvent
	public void fovUpdateEvent(FOVUpdateEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntity()))
			event.setNewfov(((EntitySecurityCamera) event.getEntity().getRidingEntity()).getZoomAmount());
	}

	@SubscribeEvent
	public void renderHandEvent(RenderHandEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onMouseClicked(MouseClickedEvent event) {
		if(Minecraft.getInstance().world != null)
		{
			if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && event.getButton() != 1) //anything other than rightclick
				event.setCanceled(true);
		}
	}

	private void drawNonStandardTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight)
	{
		double z = 200;
		double widthFactor = 1F / (double) textureWidth;
		double heightFactor = 1F / (double) textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, z).tex(u * widthFactor, (v + height) * heightFactor).endVertex();
		buffer.pos(x + width, y + height, z).tex((u + width) * widthFactor, (v + height) * heightFactor).endVertex();
		buffer.pos(x + width, y, z).tex((u + width) * widthFactor, v * heightFactor).endVertex();
		buffer.pos(x, y, z).tex(u * widthFactor, v * heightFactor).endVertex();
		tessellator.draw();
	}

	private ItemStack fillBucket(World world, BlockPos pos){
		Block block = world.getBlockState(pos).getBlock();

		if(block == SCContent.bogusWater){
			world.removeBlock(pos);
			return new ItemStack(SCContent.fWaterBucket, 1);
		}else if(block == SCContent.bogusLava){
			world.removeBlock(pos);
			return new ItemStack(SCContent.fLavaBucket, 1);
		}
		else
			return ItemStack.EMPTY;
	}

	private void handleOwnableTEs(PlaceEvent event) {
		if(event.getWorld().getTileEntity(event.getPos()) instanceof IOwnable) {
			String name = event.getPlayer().getName().getFormattedText();
			String uuid = event.getPlayer().getGameProfile().getId().toString();

			((IOwnable) event.getWorld().getTileEntity(event.getPos())).getOwner().set(uuid, name);
		}
	}

	private boolean handleCodebreaking(PlayerInteractEvent event) {
		World world = event.getEntityPlayer().world;
		TileEntity tileEntity = event.getEntityPlayer().world.getTileEntity(event.getPos());

		if(ServerConfig.CONFIG.allowCodebreakerItem.get() && event.getEntityPlayer().getHeldItem(event.getHand()).getItem() == SCContent.codebreaker) //safety so when codebreakers are disabled they can't take damage
			event.getEntityPlayer().getHeldItem(event.getHand()).damageItem(1, event.getEntityPlayer());

		if(tileEntity != null && tileEntity instanceof IPasswordProtected && new Random().nextInt(3) == 1)
			return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockState(event.getPos()), event.getEntityPlayer(), !ServerConfig.CONFIG.allowCodebreakerItem.get());

		return false;
	}

	private String getRandomTip(){
		String[] tips = {
				"messages.tip.scHelp",
				"messages.tip.trello",
				"messages.tip.patreon",
				"messages.tip.discord",
				"messages.tip.scserver"
		};

		return tips[new Random().nextInt(tips.length)];
	}

	private boolean isOwnableBlock(Block block, TileEntity tileEntity){
		return (tileEntity instanceof TileEntityOwnable || tileEntity instanceof IOwnable || block instanceof BlockOwnable);
	}

}
