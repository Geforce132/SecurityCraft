--------------------------Changelog for v1.9.3 of SecurityCraft--------------------------

- New: Reinforced Blocks: Mangrove Planks, Mangrove Log, Stripped Mangrove Log, Stripped Mangrove Wood, Mangrove Wood, Mangrove Slab, Mangrove Stairs, Mangrove Button, Mangrove Pressure Plate, Mud, Packed Mud, Mud Bricks, Mud Brick Slab, Mud Brick Stairs, Mud Brick Wall, Ochre Froglight, Verdant Froglight, Pearlescent Froglight
- New: Some of SecurityCraft's blocks, items, and entities can now trigger sculk sensors
- New: Items can now be dragged from JEI into the Inventory Scanner's ghost slots
- New: Item and block tag "securitycraft:reinforced/terracotta"
- New: Several additions to minecraft's tags, including "minecraft:azalea_grows_on", "minecraft:sand", and "minecraft:terracotta"
- New: Several blocks now have a new Universal Block Modifier option to disable them
- New: Blocks that have been linked to a Sonic Security System can now be seen and removed in the item's and block's screen
- Change: Reduced the Rail Mine's explosion size by 25% to make it comparable to other mines' explosions
- API: Added DisabledOption for having a default option to disable blocks
- API: Added Option#getKey and Option#getDescriptionKey to easily access the language keys associated with an option
- API: IEMPAffected has been added. It can be used to shut down select SecurityCraft blocks and entities when they're within range of an EMP blast
- API: IEMPAffectedTE has been added as a default implementation for tile entities which want to implement IEMPAffected
- Fix: Water doesn't render correctly while a Sonic Security System is recording within the player's view
- Fix: The reinforced stone pressure plate is not in SecurityCraft's "securitycraft:reinforced/pressure_plates" tag
- Misc: The minimum required Forge version is now 41.0.75

--------------------------Changelog for v1.9.2-beta5 of SecurityCraft--------------------------

- Fix: Compatibility with Forge 41.0.64+
- Misc: The minimum required Forge version is now 41.0.64

--------------------------Changelog for v1.9.2-beta4 of SecurityCraft--------------------------

- Fix: Crash when trying to view a camera on a server

--------------------------Changelog for v1.9.2-beta3 of SecurityCraft--------------------------

- New: Readd JEI integration

--------------------------Changelog for v1.9.2-beta2 of SecurityCraft--------------------------

- Fix: Compatibility with Forge 41.0.28+
- Misc: The minimum required Forge version is now 41.0.28

--------------------------Changelog for v1.9.2-beta1 of SecurityCraft--------------------------

- Change: Item and block tags "securitycraft:reinforced/carpets" are now "securitycraft:reinforced/wool_carpets" to match vanilla