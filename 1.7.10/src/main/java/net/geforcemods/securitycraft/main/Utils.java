package net.geforcemods.securitycraft.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockKeycardReader;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.network.packets.PacketSSyncTENBTTag;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * SecurityCraft's utility methods are found here. Frequently used or simplified vanilla code can be found here.
 * 
 * @author Geforce
 */
public class Utils {
	
public static class PlayerUtils{
	
	/**
	 * Sets the given player's position and rotation. <p>
	 * 
	 * Args: player, x, y, z, yaw, pitch.
	 */
	public static void setPlayerPosition(EntityPlayer player, double x, double y, double z, float yaw, float pitch){
		player.setPositionAndRotation(x, y, z, yaw, pitch);
		player.setPositionAndUpdate(x, y, z);
	}
	
	/**
	 * Gets the EntityPlayer instance of a player (if they're online) using their name. <p>
	 * 
	 * Args: playerName.
	 */
	@SuppressWarnings("rawtypes")
	public static EntityPlayer getPlayerFromName(String par1){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			List players = Minecraft.getMinecraft().theWorld.playerEntities;
	    	Iterator iterator = players.iterator();
	    	
	    	while(iterator.hasNext()){
	    		EntityPlayer tempPlayer = (EntityPlayer) iterator.next();
	    		if(tempPlayer.getCommandSenderName().matches(par1)){
	    			return tempPlayer;
	    		}
	    	}
	    	
	    	return null;
		}else{
			List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
	    	Iterator iterator = players.iterator();
	    	
	    	while(iterator.hasNext()){
	    		EntityPlayer tempPlayer = (EntityPlayer) iterator.next();
	    		if(tempPlayer.getCommandSenderName().matches(par1)){
	    			return tempPlayer;
	    		}
	    	}
	    	
	    	return null;
		}
    }
	
	/**
	 * Returns true if a player with the given name is in the world.
	 * 
	 * Args: playerName.
	 */
	public static boolean isPlayerOnline(String par1) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			for(int i = 0; i < Minecraft.getMinecraft().theWorld.playerEntities.size(); i++){
	    		EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().theWorld.playerEntities.get(i);
	    		
	    		if(player != null && player.getCommandSenderName().matches(par1)){
	    			return true;
	    		}
	    	}
			
			return false;
		}else{
			return (MinecraftServer.getServer().getConfigurationManager().func_152612_a(par1) != null);  	
		}
    }
	
	/**
	 * Sends the given player a chat message. <p>
	 * 
	 * Args: player, prefix, text, color.
	 */
	public static void sendMessageToPlayer(EntityPlayer player, String prefix, String text, EnumChatFormatting color){
		player.addChatComponentMessage(new ChatComponentText("[" + color + prefix + EnumChatFormatting.WHITE + "] " + text));
	}
	
	/**
	 * Sends the given {@link ICommandSender} a chat message. <p>
	 * 
	 * Args: sender, prefix, text, color.
	 */
	public static void sendMessageToPlayer(ICommandSender sender, String prefix, String text, EnumChatFormatting color){
		sender.addChatMessage(new ChatComponentText("[" + color + prefix + EnumChatFormatting.WHITE + "] " + text));
	}
	
	/**
	 * Returns true if the player is holding the given item.
	 * 
	 * Args: player, item.
	 */
	public static boolean isHoldingItem(EntityPlayer player, Item item){
		if(item == null && player.getCurrentEquippedItem() == null){
			return true;
		}
		
		return (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == item);
	}
	
}

public static class BlockUtils{
	
	/**
	 * Used by the Cage Trap to create the cage. <p>
	 * 
	 * Args: world, x, y, z, block.
	 */
	public static void setBlockInBox(World par1World, int par2, int par3, int par4, Block par5){
		par1World.setBlock(par2 + 1, par3 + 1, par4, par5);
		par1World.setBlock(par2 + 1, par3 + 2, par4, par5);
		par1World.setBlock(par2 + 1, par3 + 3, par4, par5);
		par1World.setBlock(par2 + 1, par3 + 1, par4 + 1, par5);
		par1World.setBlock(par2 + 1, par3 + 2, par4 + 1, par5);
		par1World.setBlock(par2 + 1, par3 + 3, par4 + 1, par5);
		par1World.setBlock(par2 - 1, par3 + 1, par4, par5);
		par1World.setBlock(par2 - 1, par3 + 2, par4, par5);
		par1World.setBlock(par2 - 1, par3 + 3, par4, par5);
		par1World.setBlock(par2 - 1, par3 + 1, par4 + 1, par5);
		par1World.setBlock(par2 - 1, par3 + 2, par4 + 1, par5);
		par1World.setBlock(par2 - 1, par3 + 3, par4 + 1, par5);
		par1World.setBlock(par2, par3 + 1, par4 + 1, par5);
		par1World.setBlock(par2, par3 + 2, par4 + 1, par5);
		par1World.setBlock(par2, par3 + 3, par4 + 1, par5);

		par1World.setBlock(par2 + 1, par3 + 1, par4, par5);
		par1World.setBlock(par2 + 1, par3 + 2, par4, par5);
		par1World.setBlock(par2 + 1, par3 + 3, par4, par5);

		par1World.setBlock(par2, par3 + 1, par4 - 1, par5);
		par1World.setBlock(par2, par3 + 2, par4 - 1, par5);
		par1World.setBlock(par2, par3 + 3, par4 - 1, par5);
		par1World.setBlock(par2 + 1, par3 + 1, par4 - 1, par5);
		par1World.setBlock(par2 + 1, par3 + 2, par4 - 1, par5);
		par1World.setBlock(par2 + 1, par3 + 3, par4 - 1, par5);
		par1World.setBlock(par2 - 1, par3 + 1, par4 - 1, par5);
		par1World.setBlock(par2 - 1, par3 + 2, par4 - 1, par5);
		par1World.setBlock(par2 - 1, par3 + 3, par4 - 1, par5);
	}
	
	/**
	 * Updates a block and notify's neighboring blocks of a change. <p>
	 * 
	 * Args: worldObj, x, y, z, blockID, tickRate, shouldUpdate.
	 */
	public static void updateAndNotify(World par1World, int par2, int par3, int par4, Block par5, int par6, boolean par7){
		if(par7){
			par1World.scheduleBlockUpdate(par2, par3, par4, par5, par6);
		}
		
		par1World.notifyBlocksOfNeighborChange(par2, par3, par4, par5, par1World.getBlockMetadata(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2 + 1, par3, par4, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2 - 1, par3, par4, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2, par3, par4 + 1, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2, par3, par4 - 1, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2, par3 + 1, par4, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2, par3 - 1, par4, par1World.getBlock(par2, par3, par4));
	}
	
	/**
	 * Gets the Item version of the given Block. <p>
	 * 
	 * Args: block
	 */
	public static Item getItemFromBlock(Block par1){
		return Item.getItemFromBlock(par1);
	}
	
	/**
	 * Breaks the block at the given coordinates. <p>
	 * 
	 * Args: world, x, y, z, shouldDropItem.
	 */
	public static void destroyBlock(World par1World, int par2, int par3, int par4, boolean par5){
		par1World.func_147480_a(par2, par3, par4, par5);
	}
	
	/**
	 * Checks if the block at the given coordinates is a beacon, and currently producing that light beam? <p>
	 * 
	 * Args: World, x, y, z.
	 */
	public static boolean isActiveBeacon(World par1World, int beaconX, int beaconY, int beaconZ){
		if(par1World.getBlock(beaconX, beaconY, beaconZ) == Blocks.beacon){
			float f = ((TileEntityBeacon) par1World.getTileEntity(beaconX, beaconY, beaconZ)).func_146002_i();
			
			return f > 0.0F ? true : false;
		}else{
			return false;
		}
	}
	
	/**
	 * Checks if the block at the given coordinates is touching the specified block on any side. <p>
	 * 
	 * Args: world, x, y, z, blockToCheckFor, checkYAxis.
	 */
	public static boolean blockSurroundedBy(World world, int x, int y, int z, Block blockToCheckFor, boolean checkYAxis) {
		if(!checkYAxis && (world.getBlock(x + 1, y, z) == blockToCheckFor || world.getBlock(x - 1, y, z) == blockToCheckFor || world.getBlock(x, y, z + 1) == blockToCheckFor || world.getBlock(x, y, z - 1) == blockToCheckFor)){
			return true;
		}else if(checkYAxis && (world.getBlock(x + 1, y, z) == blockToCheckFor || world.getBlock(x - 1, y, z) == blockToCheckFor || world.getBlock(x, y, z + 1) == blockToCheckFor || world.getBlock(x, y, z - 1) == blockToCheckFor || world.getBlock(x, y + 1, z) == blockToCheckFor || world.getBlock(x, y - 1, z) == blockToCheckFor)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Returns true if the player is the owner of the given block.
	 * 
	 * Args: block's TileEntity, player.
	 */
	public static boolean isOwnerOfBlock(IOwnable block, EntityPlayer player){
		if(!(block instanceof IOwnable) || block == null){
			throw new ClassCastException("You must provide an instance of IOwnable when using Utils.isOwnerOfBlock!");
		}
		
		String uuid = block.getOwnerUUID();
		String owner = block.getOwnerName();
		
		if(uuid != null && !uuid.matches("ownerUUID")){
			return uuid.matches(player.getGameProfile().getId().toString());
		}
		
		if(owner != null && !owner.matches("owner")){
			return owner.matches(player.getCommandSenderName());
		}
		
		return false;
	}
	
	/**
	 * Returns true if the metadata of the block at X, Y, and Z is within (or equal to) the minimum and maximum given.
	 */
	public static boolean isMetadataBetween(World world, int x, int y, int z, int min, int max) {
		return (world.getBlockMetadata(x, y, z) >= min && world.getBlockMetadata(x, y, z) <= max);
	}
	
}

public static class ModuleUtils{
	
	/**
	 * Insert a module into a customizable TileEntity. <p>
	 * 
	 * Args: world, x, y, z, moduleType.
	 */
	public static void insertModule(World par1World, int par2, int par3, int par4, EnumCustomModules module){
		((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4)).insertModule(module);
	}
	
	/**
	 * Used by the Laser Block to insert modules in all directions.
	 * 
	 * Args: world, x, y, z, direction, blockToCheckFor, range, module, updateAdjecentBlocks
	 */
	public static void checkForBlockAndInsertModule(World par1World, int par2, int par3, int par4, String dir, Block blockToCheckFor, int range, ItemStack module, boolean updateAdjecentBlocks){
		for(int i = 1; i <= range; i++){
			if(dir.equalsIgnoreCase("x+")){
				if(par1World.getBlock(par2 + i, par3, par4) == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(par2 + i, par3, par4)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(par2 + i, par3, par4)).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, par2 + i, par3, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(par1World.getBlock(par2 - i, par3, par4) == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(par2 - i, par3, par4)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(par2 - i, par3, par4)).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, par2 - i, par3, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(par1World.getBlock(par2, par3 + i, par4) == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(par2, par3 + i, par4)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3 + i, par4)).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, par2, par3 + i, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(par1World.getBlock(par2, par3 - i, par4) == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(par2, par3 - i, par4)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3 - i, par4)).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, par2, par3 - i, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(par1World.getBlock(par2, par3, par4 + i) == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 + i)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 + i)).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, par2, par3, par4 + i, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z-")){
				if(par1World.getBlock(par2, par3, par4 - i) == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 - i)).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 - i)).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, par2, par3, par4 - i, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}
		}
	}
	
	public static void checkInAllDirsAndInsertModule(World par1World, int par2, int par3, int par4, Block blockToCheckFor, int range, ItemStack module, boolean updateAdjecentBlocks){
		checkForBlockAndInsertModule(par1World, par2, par3, par4, "x+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, par2, par3, par4, "x-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, par2, par3, par4, "y+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, par2, par3, par4, "y-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, par2, par3, par4, "z+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, par2, par3, par4, "z-", blockToCheckFor, range, module, updateAdjecentBlocks);
	}
	
	/**
	 * Used by the Laser Block to remove modules in all directions.
	 * 
	 * Args: world, x, y, z, direction, blockToCheckFor, range, module, updateAdjecentBlocks
	 */
	public static void checkForBlockAndRemoveModule(World par1World, int par2, int par3, int par4, String dir, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjecentBlocks){
		for(int i = 1; i <= range; i++){
			if(dir.equalsIgnoreCase("x+")){
				if(par1World.getBlock(par2 + i, par3, par4) == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(par2 + i, par3, par4)).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(par2 + i, par3, par4)).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, par2 + i, par3, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(par1World.getBlock(par2 - i, par3, par4) == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(par2 - i, par3, par4)).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(par2 - i, par3, par4)).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, par2 - i, par3, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(par1World.getBlock(par2, par3 + i, par4) == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(par2, par3 + i, par4)).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3 + i, par4)).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, par2, par3 + i, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(par1World.getBlock(par2, par3 - i, par4) == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(par2, par3 - i, par4)).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3 - i, par4)).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, par2, par3 - i, par4, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(par1World.getBlock(par2, par3, par4 + i) == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 + i)).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 + i)).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, par2, par3, par4 + i, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z-")){
				if(par1World.getBlock(par2, par3, par4 - i) == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 - i)).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(par2, par3, par4 - i)).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, par2, par3, par4 - i, blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}
		}
	}
	
	public static void checkInAllDirsAndRemoveModule(World par1World, int par2, int par3, int par4, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjecentBlocks){
		checkForBlockAndRemoveModule(par1World, par2, par3, par4, "x+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, par2, par3, par4, "x-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, par2, par3, par4, "y+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, par2, par3, par4, "y-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, par2, par3, par4, "z+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, par2, par3, par4, "z-", blockToCheckFor, range, module, updateAdjecentBlocks);
	}
	
	/**
	 * Get the {@link ItemModule} instance of the given module type. <p>
	 * 
	 * Args: moduleType.
	 */
	public static ItemModule getItemFromModule(EnumCustomModules module) { //TODO Add any new modules to this list!
		if(module == EnumCustomModules.REDSTONE){
			return mod_SecurityCraft.redstoneModule;
		}else if(module == EnumCustomModules.WHITELIST){
			return mod_SecurityCraft.whitelistModule;
		}else if(module == EnumCustomModules.BLACKLIST){
			return mod_SecurityCraft.blacklistModule;
		}else if(module == EnumCustomModules.HARMING){
			return mod_SecurityCraft.harmingModule;
		}else if(module == EnumCustomModules.SMART){
			return mod_SecurityCraft.smartModule;
		}else if(module == EnumCustomModules.STORAGE){
			return mod_SecurityCraft.storageModule;
		}else{
			return null;
		}
	}
	
	
	/**
	 * Gets the players added to customizable modules (such as the Whitelist Module.) <p>
	 * 
	 * Args: world, x, y, z, moduleType.
	 */
	public static List<String> getPlayersFromModule(World par1World, int par2, int par3, int par4, EnumCustomModules module) {
		List<String> list = new ArrayList<String>();
		
		CustomizableSCTE te = (CustomizableSCTE) par1World.getTileEntity(par2, par3, par4);
		
		if(te.hasModule(module)){
			ItemStack item = te.getModule(module);
						
			for(int i = 1; i <= 10; i++){
				if(item.stackTagCompound != null && item.stackTagCompound.getString("Player" + i) != null && !item.stackTagCompound.getString("Player" + i).isEmpty()){
					list.add(item.stackTagCompound.getString("Player" + i).toLowerCase());
				}
			}
		}
		
		return list;
	}
	
	/**
	 * A large block of code that checks if the TileEntity at the specified coordinates has the given module inserted, and what should happen if it does. <p>
	 * 
	 * Args: world, x, y, z, player, moduleType.
	 */
	public static boolean checkForModule(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, EnumCustomModules module){
		TileEntity te = par1World.getTileEntity(par2, par3, par4);
		
		if(te == null || !(te instanceof CustomizableSCTE)){ return false; }
		
		if(te instanceof TileEntityKeypad){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keypad.name"), StatCollector.translateToLocal("messages.module.whitelisted"), EnumChatFormatting.GREEN);
				BlockKeypad.activate(par1World, par2, par3, par4);
				return true;
			}
			
			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.BLACKLIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keypad.name"), StatCollector.translateToLocal("messages.module.blacklisted"), EnumChatFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityKeycardReader){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keycardReader.name"), StatCollector.translateToLocal("messages.module.whitelisted"), EnumChatFormatting.GREEN);
				BlockKeycardReader.activate(par1World, par2, par3, par4);
				return true;
			}
			
			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.BLACKLIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keycardReader.name"), StatCollector.translateToLocal("messages.module.blacklisted"), EnumChatFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityRetinalScanner){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				return true;
			}
		}else if(te instanceof TileEntityInventoryScanner){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && getPlayersFromModule(par1World, par2, par3, par4, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getCommandSenderName().toLowerCase())){
				return true;
			}
		}
		
		return false;
	}
	
}

public static class EntityUtils{
	
	/**
	 * Does the given entity have the specified potion effect? <p>
	 * 
	 * Args: entity, potionEffect.
	 */
	@SuppressWarnings("rawtypes")
	public static boolean doesMobHavePotionEffect(EntityLivingBase mob, Potion potion){
		Iterator iterator = mob.getActivePotionEffects().iterator();

		while(iterator.hasNext()){
			PotionEffect effect = (PotionEffect) iterator.next();
			String eName = effect.getEffectName();
			
			if(eName.matches(potion.getName())){
				return true;
			}else{
				continue;
			}
		}
		
		return false;
	}
	
}

public static class ClientUtils{
	
	/**
	 * Closes any GUI that is currently open. <p>
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static void closePlayerScreen(){
		Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
		Minecraft.getMinecraft().setIngameFocus();
	}
	
	/**
	 * Sets the "zoom" of the client's view.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static void setCameraZoom(double zoom){
		if(zoom == 0){
			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, 1.0D, 46);
			return;
		}
		
		double tempZoom = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, 46);
		ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, tempZoom + zoom, 46);
	}
	
	/**
	 * Gets the "zoom" of the client's view.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static double getCameraZoom(){
		return ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, 46);
	}
	
	/**
	 * Takes a screenshot, and sends the player a notification. <p>
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static void takeScreenshot() {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
        	Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(Minecraft.getMinecraft().mcDataDir, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, Minecraft.getMinecraft().getFramebuffer()));	
        }
	}
	
	/**
	 * Returns the current Minecraft in-game time, in a 12-hour AM/PM format.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static String getFormattedMinecraftTime(){
		Long time = Long.valueOf(Minecraft.getMinecraft().theWorld.provider.getWorldTime());
		
		int hours24 = (int) ((float) time.longValue() / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) ((float) time.longValue() / 16.666666F % 60.0F);
		
		return String.format("%02d:%02d %s", new Object[]{Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM"});
	}
	
	/**
	 * Sends the client-side NBTTagCompound of a block's TileEntity to the server.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static void syncTileEntity(TileEntity tileEntity){
		NBTTagCompound tag = new NBTTagCompound();                
		tileEntity.writeToNBT(tag);
		mod_SecurityCraft.network.sendToServer(new PacketSSyncTENBTTag(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tag));
	}
	
	/**
	 * Returns true if the client is hosting a LAN world.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static boolean isInLANWorld(){
		return (Minecraft.getMinecraft().getIntegratedServer() != null && Minecraft.getMinecraft().getIntegratedServer().getPublic());
	}
	
}

public static class WorldUtils{
	
	/**
	 * Performs a ray trace against all blocks (except liquids) from the starting X, Y, and Z
	 * to the end point, and returns true if a block is within that path.
	 * 
	 * Args: Starting X, Y, Z, ending X, Y, Z.
	 */
	public static boolean isPathObstructed(World world, int x1, int y1, int z1, int x2, int y2, int z2){
		return isPathObstructed(world, (double) x1, (double) y1, (double) z1, (double) x2, (double) y2, (double) z2);
	}

	/**
	 * Performs a ray trace against all blocks (except liquids) from the starting X, Y, and Z
	 * to the end point, and returns true if a block is within that path.
	 * 
	 * Args: Starting X, Y, Z, ending X, Y, Z.
	 */
	public static boolean isPathObstructed(World world, double x1, double y1, double z1, double x2, double y2, double z2) {
		return world.rayTraceBlocks(Vec3.createVectorHelper(x1, y1, z1), Vec3.createVectorHelper(x2, y2, z2)) != null;
	}
	
}

	/**
	 * Removes the last character in the given String. <p>
	 */
	public static String removeLastChar(String par1){
		if(par1 == null || par1.isEmpty()){ return ""; }
		
		return par1.substring(0, par1.length() - 1);
	}
	
	/**
	 * Returns the given X, Y, and Z coordinates in a nice String, useful for chat messages. <p>
	 * 
	 * Args: x, y, z.
	 */
	public static String getFormattedCoordinates(int par1, int par2, int par3){
		return " X:" + par1 + " Y:" + par2 + " Z:" + par3;
	}
	
	/**
	 * Returns the opposite value of the given boolean. <p>
	 */
	public static boolean toggleBoolean(boolean par1) {
		return !par1;
	}
	
	//----------------------//
	//    Random methods    //
	//----------------------//
	
	public static void setISinTEAppropriately(World par1World, int par2, int par3, int par4, ItemStack[] contents, String type) {
		if(par1World.getBlockMetadata(par2, par3, par4) == 4 && par1World.getBlock(par2 - 2, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.inventoryScannerField && par1World.getBlockMetadata(par2 - 2, par3, par4) == 5){
			((TileEntityInventoryScanner) par1World.getTileEntity(par2 - 2, par3, par4)).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(par2 - 2, par3, par4)).setType(type);
		}else if(par1World.getBlockMetadata(par2, par3, par4) == 5 && par1World.getBlock(par2 + 2, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.inventoryScannerField && par1World.getBlockMetadata(par2 + 2, par3, par4) == 4){
			((TileEntityInventoryScanner) par1World.getTileEntity(par2 + 2, par3, par4)).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(par2 + 2, par3, par4)).setType(type);
		}else if(par1World.getBlockMetadata(par2, par3, par4) == 2 && par1World.getBlock(par2, par3, par4 - 2) == mod_SecurityCraft.inventoryScanner && par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.inventoryScannerField && par1World.getBlockMetadata(par2, par3, par4 - 2) == 3){
			((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 - 2)).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 - 2)).setType(type);
		}else if(par1World.getBlockMetadata(par2, par3, par4) == 3 && par1World.getBlock(par2, par3, par4 + 2) == mod_SecurityCraft.inventoryScanner && par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.inventoryScannerField && par1World.getBlockMetadata(par2, par3, par4 + 2) == 2){
			((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 + 2)).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 + 2)).setType(type);
		}
	}
	
	public static boolean hasInventoryScannerFacingBlock(World par1World, int par2, int par3, int par4) {
		if(par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2 + 1, par3, par4) == 4 && par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2 - 1, par3, par4) == 5){
			return true;
		}
		else if(par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2 - 1, par3, par4) == 5 && par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2 + 1, par3, par4) == 4){
			return true;
		}
		else if(par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 + 1) == 2 && par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 - 1) == 3){
			return true;
		}
		else if(par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 - 1) == 3 && par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 + 1) == 2){
			return true;
		}else{
			return false;
		}
	}

}
