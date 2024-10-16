package net.geforcemods.securitycraft.mixin.datafix;

import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.geforcemods.securitycraft.components.DataFixHandler;
import net.minecraft.util.datafix.schemas.V4067;

/**
 * Unregisters the old security sea boat entity type and registers the new ones to the datafixer
 */
@Mixin(V4067.class)
public class V4067Mixin {
	@Inject(method = "registerEntities", at = @At("TAIL"))
	private void securitycraft$unRegisterSecuritySeaBoatEntityTypes(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> ci, @Local Map<String, Supplier<TypeTemplate>> map) {
		map.remove("securitycraft:security_sea_boat");
		DataFixHandler.registerInventoryAndModules(schema, map, "securitycraft:oak_security_sea_boat");
		DataFixHandler.registerInventoryAndModules(schema, map, "securitycraft:spruce_security_sea_boat");
		DataFixHandler.registerInventoryAndModules(schema, map, "securitycraft:birch_security_sea_boat");
		DataFixHandler.registerInventoryAndModules(schema, map, "securitycraft:jungle_security_sea_boat");
		DataFixHandler.registerInventoryAndModules(schema, map, "securitycraft:acacia_security_sea_boat");
		DataFixHandler.registerInventoryAndModules(schema, map, "securitycraft:dark_oak_security_sea_boat");
		DataFixHandler.registerInventoryAndModules(schema, map, "securitycraft:mangrove_security_sea_boat");
		DataFixHandler.registerInventoryAndModules(schema, map, "securitycraft:cherry_security_sea_boat");
		DataFixHandler.registerInventoryAndModules(schema, map, "securitycraft:bamboo_security_sea_raft");
	}
}