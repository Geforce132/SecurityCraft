--------------------------Changelog for v1.9.6 of SecurityCraft--------------------------

- New: Support for FTB Teams when having team ownership enabled in the config
- New: Laser blocks can now be configured to have lasers on specific sides be disabled by using a Smart Module
- New: Command "/sc dump <registry>" to be able to copy all of SecurityCraft's registry entries of the specified registry
- New: Configs "sentryAttackableEntitiesAllowlist" and "sentryAttackableEntitiesDenylist" for controlling which entities a Sentry can and cannot attack
- New: Password-protected Barrel
- Change: Lasers and inventory scanner fields are now removed silently
- Change: Inventory scanners and password-protected chests now only drop their modules if the last scanner/chest is removed
- Change: Changing the owner of a Reinforced Hopper now needs validation of the new owner (akin to reinforced pistons)
- Change: New look for the Claymore
- API: IModuleInventory#shouldDropModules to determine whether a block should drop its modules when broken
- Fix: Turning off camera rotation leads to extreme stuttering of the camera
- Fix: Shift-clicking a potion in the brewing stand screen puts the potion into the wrong slot
- Fix: Redstone does not automatically connect to the Alarm, Projector, and Username Logger
- Fix: Some text in the check password screens doesn't show up properly
- Fix: Options are not synchronized between newly placed laser blocks
- Fix: Modules are not properly synchronized between laser blocks in certain situations
- Fix: The Codebreaker has 3 uses instead of 5
- Fix: The Sentry's head could sometimes rapidly stutter in height when completely extended or retracted
- Fix: The Block Change Detector list cannot be scrolled using the mouse wheel
- Fix: The Reinforced Hopper checks for the wrong allowlist sometimes
- Fix: It's possible to remove other players' laser/inventory scanner fields
- Fix: Cannot place blocks against laser blocks from other players
- Fix: Severe lag when a player mounts a camera on a dedicated server when other players are connected
- Misc.: The Spanish translation has been updated (Thanks Globi10!)
- Misc.: Dialects for German (de_at, de_ch), French (fr_ca), and Spanish (es_ar, es_cl, es_ec, es_mx, es_uy, es_ve) are now supported. While these are not proper translations into the specific dialects, they should be better than having to resort to English
- Misc.: Updates to SC Manual entries and other text, to bring them up to date with actual functionality of the mod

--------------------------Changelog for v1.9.5 of SecurityCraft--------------------------

- New: The reinforced block tint color can now be changed with a config setting
- New: Netherrack Mine and End Stone Mine
- New: The damage dealt by a Laser Block containing a Harming Module can now be changed with a config setting
- New: More modded wooden chests can now be converted to a Password-protected Chest
- New: Crystal Quartz Bricks, Smooth Crystal Quartz, Smooth Crystal Quartz Stairs, Smooth Crystal Quartz Slab, and their reinforced variants
- New: Ignore owner option for blocks like the Inventory Scanner or Block Change Detector
- New: Smart Module (incorrect code = cooldown) and Harming Module (incorrect code = damage) support for blocks that require a passcode
- New: Config option "incorrectPasscodeDamage" to define the amount of damage dealt to a player who inserted an incorrect passcode
- Change: Password-protected blocks can now only be activated if a password has been set, even if the player activating the block is on the allowlist
- Change: Passcodes now have to be manually confirmed by using the new enter button found in the check password interface (or by using the enter key on the keyboard)
- API: Changed IPasswordConvertible#getOriginalBlock to IPasswordConvertible#isValidStateForConversion for finer control over what can be converted
- API: New methods IModuleInventory#isAllowed and IModuleInventory#isDenied to check whether an entity is listed on an allowlist or denylist module respectively
- API: Moved and renamed Owner#isOwner methods to IPasswordProtected#isOwnedBy
- API: New methods IPasswordProtected#startCooldown, IPasswordProtected#isOnCooldown IPasswordProtected#getCooldownEnd, and IPasswordProtected#getIncorrectPasscodeDamage
- API: New method IPasswordProtected#verifyPasswordSet which returns whether a password has been set for this IPasswordProtected
- API: Removed the IOwnable parameter from IPasswordProtected#setPasswordGUI because the relevant code has been moved to IPasswordProtected#verifyPasswordSet
- API: New linked action ILinkedAction#StateChanged which is used when the state at the linkable block entity's position changes
- Fix: The Reinforced Water Cauldron and Reinforced Grass Block don't have a reinforced tint
- Fix: Reinforced Stained Glass Pane items display incorrectly
- Fix: The Reinforced Moss Carpet has a different sound compared to the vanilla Moss Carpet
- Fix: Crash when inserting a module into a single Inventory Scanner
- Fix: Breaking a disguised Inventory Scanner does not update the disguise of the connected scanner
- Fix: Some animations stutter
- Fix: Using /kill to remove a camera entity, or removing it by other non-standard means, does not update the camera block properly
- Fix: Error when unbinding the last mine/sentry from a Mine/Sentry Remote Access Tool
- Fix: Placing an Inventory Scanner facing a disguised scanner does not update the placed scanner's disguise
- Fix: The Cage Trap and Password-protected Furnace/Smoker/Blast Furnace are black instead of see-through
- Fix: Breaking an Inventory Scanner/Laser or one of its field does not break all fields, if the respective config setting is lower than the amount of fields present
- Fix: Breaking an Inventory Scanner Field with the inventoryScannerRange config setting set to >=3 does not break and reinstate all fields properly
- Fix: Players that are on the allowlist of a Security Camera cannot toggle the camera's redstone output
- Fix: The Redstone Module in a Laser Block does not respect ownership
- Fix: Disabled option in laser blocks does not work properly
- Fix: In the check password screen, using the enter key to press focused buttons does not work
- Fix: Several instances of chunks not loading correctly when changing the view distance while viewing a camera
- Fix: The Username Logger sometimes cannot be activated by a short redstone signal
- Fix: The Codebreaker sometimes loses durability when rightclicking a block even though no codebreaking attempt has been made
- Fix: Taking an item out of a Display Case does not work when the offhand is not empty
- Fix: Issues when unloading and saving chunks containing linkable block entities
- Fix: Some scroll lists can be scrolled a tiny bit even though there is no scrollbar
- Fix: Some scroll lists cannot be scrolled far enough to show all entries
- Fix: Some mines explode when detecting a player in creative mode
- Fix: Laser blocks that are indirectly connected to a laser field detecting an entity don't activate
- Fix: Laser fields can activate disconnected laser blocks in some circumstances

--------------------------Changelog for v1.9.4 of SecurityCraft--------------------------

- New: Japanese translation (Thanks momo-i!)
- New: ProjectE support (all reinforced blocks now have an EMC value)
- New: Taser effects and damage are now configurable
- New: The Block Change Detector can now show highlights in the world, marking where it detected block changes
- New: Ability to change a block pocket's outline color
- New: The I.M.S. is now waterloggable
- New: Scrolling in the SecurityCraft Manual while holding the control key will now scroll through subpages (if any exist)
- New: Rift Stabilizer, a block that detects and prevents teleportation attempts in its vicinity
- New: Display Case. Securely display your items
- New: Glow Display Case
- Change: The Laser Block now needs a Redstone Module in order to emit a redstone signal when someone walks through the laser
- Change: Most of SecurityCraft's screens are now no longer pausing the game when open, and can now be closed with the "Open/Close Inventory" key
- Change: Made Fake Water's damage equivalent to lava damage
- API: Changed LinkedAction to an interface and the enum values to records for easier usability
- API: Several methods in LinkableBlockEntity were changed to account for the LinkedAction change
- API: LinkedAction has been renamed to ILinkedAction
- API: Changes to ICodebreakable and IPasswordProtected to cut down on duplicate code and make them easier and more clear to use
- Fix: Nether portals can replace reinforced blocks when generating
- Fix: Crash when trying to disguise a block as/project some blocks that don't always have a block entity associated with them
- Fix: Can't place blocks on blocks locked by a Sonic Security System
- Fix: Interacting with blocks while holding a Sonic Security System may sometimes place it for a short while
- Fix: A Sentry's name does not show in the Sentry Remote Access Tool
- Fix: Mobs can spawn on reinforced ice and reinforced iron trapdoors
- Fix: Snow layers can be placed on Reinforced Ice and Reinforced Packed Ice
- Fix: Snow layers cannot be placed on Reinforced Mud
- Fix: Sentry bullets can remove item frames
- Fix: Some blocks don't show up properly in the state selector of the Projector/Disguise Module
- Fix: Block Pocket Manager's size button and offset slider are available to non-owners
- Fix: Password-protected furnaces close even if some players still have it open
- Fix: Tooltips in some screens can overlap when tabbing through buttons while hovering over other areas that show a tooltip
- Fix: The Harming Module does not work when a Laser Block is powered
- Fix: Disguisable blocks do not show the proper block model sometimes
- Fix: The Sonic Security System has no placing sound
- Fix: Several issues in various language files
- Fix: Some reinforced blocks can be destroyed by unintended vanilla means
- Fix: Option tooltips do not update with the new value when the option is changed
- Fix: Potion particles are visible when being in Fake Lava
- Fix: Block entities of reinforced stairs don't get properly removed when the block is broken, leading to inteaction issues with other blocks placed in the same block space
- Fix: Grass can grow under disguised blocks when it shouldn't
- Fix: Briefcase and Disguise Module item duplication
- Removed: Finnish translation due to being outdated and incomplete
- Misc: The minimum required Forge version is now 43.1.8

--------------------------Changelog for v1.9.3.1 of SecurityCraft--------------------------

- Change: The default range of the IMS has been increased from 12 to 15 blocks
- Fix: Newly placed Sentries do not work. To fix non-working sentries, right-click them with redstone
- Misc.: The French translation has been updated to address the new content from v1.9.3

--------------------------Changelog for v1.9.3 of SecurityCraft--------------------------

- New: Reinforced Blocks: Mangrove Planks, Mangrove Log, Stripped Mangrove Log, Stripped Mangrove Wood, Mangrove Wood, Mangrove Slab, Mangrove Stairs, Mangrove Button, Mangrove Pressure Plate, Mud, Packed Mud, Mud Bricks, Mud Brick Slab, Mud Brick Stairs, Mud Brick Wall, Ochre Froglight, Verdant Froglight, Pearlescent Froglight
- New: Some of SecurityCraft's blocks, items, and entities can now trigger sculk sensors
- New: Items can now be dragged from JEI into the Inventory Scanner's ghost slots
- New: Item and block tag "securitycraft:reinforced/terracotta"
- New: Several additions to minecraft's tags, including "minecraft:azalea_grows_on", "minecraft:sand", and "minecraft:terracotta"
- New: Several blocks now have a new Universal Block Modifier option to disable them
- New: Blocks that have been linked to a Sonic Security System can now be seen and removed in the item's and block's screen
- New: Support for WTHIT, a fork of HWYLA
- Change: Reduced the Rail Mine's explosion size by 25% to make it comparable to other mines' explosions
- Change: Blocks that entities can intersect with, such as Laser and Inventory scanner fields, mines and the Cage Trap, now only trigger when the entity touches their bounding box
- API: Added DisabledOption for having a default option to disable blocks
- API: Added Option#getKey and Option#getDescriptionKey to easily access the language keys associated with an option
- API: IEMPAffected has been added. It can be used to shut down select SecurityCraft blocks and entities when they're within range of an EMP blast
- API: IEMPAffectedTE has been added as a default implementation for tile entities which want to implement IEMPAffected
- Fix: Water doesn't render correctly while a Sonic Security System is recording within the player's view
- Fix: The reinforced stone pressure plate is not in SecurityCraft's "securitycraft:reinforced/pressure_plates" tag
- Fix: Blocks disguised as a translucent block, like stained glass, don't show up properly
- Fix: The SecurityCraft Manual shows the info for secret signs on the keycard page (Thanks cdgamedev!)

--------------------------Changelog for v1.9.2-beta6 of SecurityCraft--------------------------

- Fix: Compatibility with Forge 41.0.94+
- Misc: The minimum required Forge version is now 41.0.94

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