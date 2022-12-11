--------------------------Changelog for v1.9.5 of SecurityCraft--------------------------

- New: The reinforced block tint color can now be changed with a config setting
- New: Netherrack Mine and End Stone Mine
- New: The damage dealt by a Laser Block containing a Harming Module can now be changed with a config setting
- New: More modded wooden chests can now be converted to a Password-protected Chest
- API: Changed IPasswordConvertible#getOriginalBlock to IPasswordConvertible#isValidStateForConversion for finer control over what can be converted
- API: New methods IModuleInventory#isAllowed and IModuleInventory#isDenied to check whether an entity is listed on an allowlist or denylist module respectively
- API: Moved and renamed Owner#isOwner methods to IPasswordProtected#isOwnedBy
- Fix: The Reinforced Water Cauldron and Reinforced Grass Block don't have a reinforced tint
- Fix: Reinforced Stained Glass Pane items display incorrectly
- Fix: The Reinforced Moss Carpet has a different sound compared to the vanilla Moss Carpet

--------------------------Changelog for v1.9.4-beta1 of SecurityCraft--------------------------

- New: Game rules "fakeWaterSourceConversion" and "fakeLavaSourceConversion" to control source conversion of fake liquids, akin to vanilla's water and lava
- Change: Items in SecurityCraft's creative tabs have been reordered to match vanilla's order and in general make more sense
- Change: Sounds for nether wood reinforced blocks have been adjusted to match the vanilla variants
- Misc.: The minimum Forge version is 44.0.11