--------------------------Changelog for v1.10.1 of SecurityCraft--------------------------

- New: Incognito Mask
- New: In-game notification message that informs players that an error occurred while capturing a frame feed
- New: Debug feature to help identify other mods preventing players from viewing a Security Camera
- New: Configuration option for limiting the amount of chunks that may be forceloaded by frame feeds
- Change: The model and texture of the Motion-Activated Light have been updated
- Change: The Trophy System now shows its targeting laser even when disguised
- Change: The signature of a Keycard Reader can now be changed through a text box
- API: New IOwnable#isOwnedBy overload taking a Player and a boolean for checking ownership respecting the new Incognito Mask
- API: New method Owner#isDefaultOwner to check if the Owner object has no player data associated with it
- API: New method IPasscodeConvertible#getRequiredKeyPanels to control how many key panel items are consumed when converting this block
- Fix: Several mod incompatibilities that caused Frame feeds to not show up correctly
- Fix: Game performance does not improve when breaking frames with an active frame feed
- Fix: Active camera feeds in multiple dimensions can interfere with each other's chunk loading
- Fix: The Level 3 Keycard cannot be crafted
- Fix: The powered Secure Redstone Interface model looks wrong when placed on the wall or ceiling
- Fix: Briefcase and Keycard Holder exploits
- Fix: Inventory scanner crash
- Fix: Placing a Secure Redstone Interface as a receiver does not correctly update itself and surrounding blocks
- Fix: Lenses placed inside a Claymore, Security Camera, or Trophy System sometimes disappear when the world is reloaded
- Fix: Blocks projected by a Projector are reset to their default state when opening the Projector's screen
- Fix: The owner of the topmost block of the cage spawned by a Cage Trap is not set correctly
- Fix: Ownable blocks can be mined by drills or similar blocks from certain other mods
- Fix: Blocks disguised as vanilla blocks can be mined by anyone
- Fix: The rotation of a Security Camera can become desynced between client and server
- Fix: Tricking Retinal Scanners, Scanner Doors and Scanner Trapdoors with player heads does not work
- Fix: The Mine and Sentry Remote Access Tool appear to be able to interact with mines/sentries that are not owned by the player, even though they can't
- Fix: Players viewing a Security Camera are invisible for players in spectator mode
- Fix: The Rift Stabilizer does not keep its custom name as an item when the bottom half is mined
- Fix: Crash when teleporting or respawning near Security Cameras
- Fix: Crash when trying to change the owner of a Reinforced Button or Reinforced Lever
- Fix: Bad client performance when looking at many reinforced blocks at once

--------------------------Changelog for v1.10 of SecurityCraft--------------------------

- New: Cameras can now be viewed on Frames within the world (live camera feeds)[1]
- New: Several server and client config settings to control chunk loading in camera feeds
- New: All blocks can now be broken only by the owner with normal tools (axe, shovel, hoe, ...) and the Universal Block Remover has been disabled by default
- New: Config to re-enable the Universal Block Remover and disable normal block breaking
- New: Config to define tool requirement behavior (e.g. does Reinforced Stone always drop, or just when breaking it using a pickaxe?)
- New: Config to allow other players to break anyone's blocks (disallowed by default)
- New: Config for defining how much longer it should take to break another player's block compared to breaking one's own
- New: Reinforced Blocks: Pale Oak Log, Stripped Pale Oak Log, Pale Oak Wood, Stripped Pale Oak Wood, Pale Oak Planks, Pale Oak Stairs, Pale Oak Slabs, Pale Oak Button, Pale Oake Pressure Plate, Pale Oak Fence, Pale Oak Fence Gate, Pale Moss Block, Pale Moss Carpet
- New: Reinforced Blocks: Resin Block, Resin Bricks, Chiseled Resin Bricks, Resin Brick Slab, Resin Brick Stairs, Resin Brick Wall
- New: Secret Pale Oak (Hanging) Sign
- New: Pale Oak Security Sea Boat
- New: Creaking Heart Mine
- New: The cage trap iron bars now also break when breaking a cage trap in creative mode
- New: The Camera Monitor, Mine/Sentry Remote Access Tool, and Sonic Security System can now be copied in the crafting table, by combining two of the same item (one empty, another one with things bound to it) in a crafting table
- New: Reinforced dispensers are now able to user any level of Universal Block Reinforcer to un-/reinforce the block in front of them
- Change: Camera model animations are now synchronized between players
- Change: The Secure Redstone Interface has new visuals for when it receives/outputs a redstone signal
- Change: Owners of disguised blocks and players in creative mode now receive the actual block instead of the disguise when using Pick Block
- Change: The text in the bottom-right corner of the camera overlay now fades out after 10 seconds
- Change: The Security Camera item model has been changed to match the one used when the camera is placed in the world
- Change: The Block Change Detector, Inventory Scanner Field, Projector, Protecto, Security Camera, Taser, and Username Logger textures have been tweaked
- Change: The default value for the "inventoryScannerRange" config setting has been increased from 2 to 3
- API: New IBlockMine interface for blocks that are block mines
- API: New method IExplosive#explodesWhenInteractedWith as well as two utility methods
- API: New method IPasscodeProtected#savePasscodeAndSalt for more conveniently saving passcode and salt key to NBT
- API: New methods IPasscodeProtected#setSaveSalt and IPasscodeProtected#shouldSaveSalt to control storing the object's salt into its data storage
- Fix: The mine remote access tool can identify block mines
- Fix: The display of items in the SC Manual can change too fast in certain situations
- Fix: Potential startup crash
- Fix: The map color, instrument, and more properties of many reinforced blocks don't match their vanilla counterparts
- Fix: Several reinforced blocks can be broken by pistons
- Fix: Wall hanging signs and horizontal reinforced iron bars have an incorrect translation key
- Fix: Security sea boats cannot be broken by players like normal boats
- Fix: The claymore does not ignore the owner if the "Ignore Owner" option is true, and vice versa
- Fix: Cage traps can be used to maliciously change ownership of blocks
- Fix: Limited use keycards can be used indefinitely in a keycard holder
- Fix: A portable radar chat message is broken in German
- Fix: Anything can pull books out of a Reinforced Chiseled Bookshelf
- Fix: Reinforced buttons and levers can be triggered by wind charges
- Fix: Reinforced grass blocks and water cauldrons are still tinted even if the reinforced tint is turned off
- Fix: Fake water does not flow the same way as regular water
- Fix: Laser and inventory scanner fields don't show up properly in the overlay of Jade/TOP/etc.
- Fix: The color chooser doesn't show color
- Fix: Loading a structure with passcode-protected objects onto itself using a structure block invalidates all passcodes
- Fix: A system of connected laser blocks does not emit redstone correctly when walking into two or more laser fields
- Fix: Brushing suspicious sand mines does not respect the player's block_interaction_range attribute
- Fix: Reinforced Block recipes are grouped and categorized differently in the recipe book compared to vanilla block recipes
- Fix: Text above the Sonic Security System does not render correctly
- Fix: Some blocks are not tinted correctly when rendered in the disguise module or projector screen's state selector
- Fix: Crash when interacting with certain SecurityCraft blocks in spectator mode
- Fix: The camera overlay renders even if HUD rendering is turned off through the use of F1
- Fix: The Projector, projected blocks, Disguise Module, and block disguises do not properly show banner patterns, decorated pot sherds, etc.
- Fix: Block mines are not grouped together in the creative inventory
- Fix: Crash when dispensing a fake liquid bucket item into a block
- Removed: Configuration "ableToBreakMines"
- Misc.: The "security_camera.png" and "security_camera_viewing.png" textures have been moved from the "block" to the "entity/security_camera" folder
- Misc.: The protecto textures have been split up into a base texture and two emissive layers
- Misc.: The taser model and textures have been improved and optimized. Resource packs need to update
- Misc.: The minimum required NeoForge version is now 21.4.126

[1] Note: Frame blocks that already existed in the world prior to this update will lose their owner. These frames can be broken by anyone so they can be placed down with the proper owner again.

--------------------------Changelog for v1.9.12-beta5 of SecurityCraft--------------------------

- Fix: The Reinforced Dropper does not work
- Fix: Change entry buttons in the Block Change Detector UI cannot be clicked
- Fix: The Color Chooser's cursor can be moved by dragging outside the color box

--------------------------Changelog for v1.9.12-beta4 of SecurityCraft--------------------------

- Fix: "Designed by" text in the SC Manual is not rendered correctly
- Fix: The buttons to change the subpage in the SC Manual cannot be pressed
- Fix: Compatibility with NeoForge 21.4.84-beta
- Misc.: The minimum required NeoForge version is now 21.4.84-beta

--------------------------Changelog for v1.9.12-beta3 of SecurityCraft--------------------------

- API: Added IModuleInventoryWithContainer to discern between module inventory and normal inventory of a block, without relying on vanilla's Container
- Fix: Compatibility with NeoForge 21.4.49-beta
- Fix: Text in the Inventory Scanner and edit module screen is not rendered correctly
- Misc.: The minimum required NeoForge version is now 21.4.49-beta

--------------------------Changelog for v1.9.12-beta2 of SecurityCraft--------------------------

- Fix: Text in the SC Manual is not rendered correctly
- Fix: Reinforced Mud Bricks and Blackstone Slab/Stairs don't look 100% like their vanilla counterparts
- Fix: Compatibility with NeoForge 21.4.30-beta
- Misc.: The minimum required NeoForge version is now 21.4.30-beta

--------------------------Changelog for v1.9.12-beta1 of SecurityCraft--------------------------

- Change: The block mine overlay now shows in the first person hand models as well
- Fix: Module descriptions of security sea boats do not show up correctly
- Fix: Holding a camera monitor makes it possible to identify disguised cameras
- Fix: Items that can have linked positions (e.g. Camera Monitor) show the idle animation even when they have positions and are either dropped, or held by a non-player entity
- Misc.: Moved display case textures to the `textures/entity/display_case/` subfolder and renamed them
- Misc.: Renamed track mine textures