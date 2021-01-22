package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.blocks.BlockKeypadChest;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Defines a block that can be converted to a password-protected variant by rightclicking it with a Key Panel.
 * Call <pre>FMLInterModComms.sendFunctionMessage("securitycraft", "registerPasswordConvertible", "your.package.ClassThatImplementsIPasswordConvertible");</pre>
 * during FMLInitializationEvent to register this with SecurityCraft.<br>
 * Do note, that you also need to implement Function<Object,IPasswordConvertible> on the class that you send via IMC. You can just return <code>this</code>
 * in the apply method. The Object argument is unused and will always be null.
 * <p>
 * - If you are converting a chest into a password-protected chest, you can extend {@link BlockKeypadChest.Convertible}.<br>
 * - If you are converting a furnace into a password-protected furnace, you can extend {@link BlockKeypadFurnace.Convertible}.
 *
 * @author bl4ckscor3
 */
public interface IPasswordConvertible
{
	/**
	 * The block that has to be rightclicked in order to convert it
	 * @return Aforementioned block
	 */
	public Block getOriginalBlock();

	/**
	 * Converts the original block to the password-protected one
	 * @param player The player who initiated the conversion
	 * @param world The world in which the conversion takes place
	 * @param pos The position the conversaion takes place at
	 * @return true if the conversion was successful, false otherwise
	 */
	public boolean convert(EntityPlayer player, World world, BlockPos pos);
}
