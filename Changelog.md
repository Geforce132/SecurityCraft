--------------------------Changelog for v1.9.9 of SecurityCraft--------------------------

- New: Items in open Display Cases can now be picked using the "Pick Block" key
- New: The Sentry Remote Access tool now displays the last known name of a Sentry if the Sentry is out of range
- New: The Portable Radar now always shows its coordinates in messages, even when it has a custom name
- New: Crafting recipes for Reinforced Pistons and Reinforced Sticky Pistons
- New: Reinforced Dispenser and Reinforced Dropper
- New: Reinforced fences and fence gates
- New: Config option "preventReinforcedFloorGlitching" to control whether players can glitch through a floor made of reinforced blocks using a boat
- New: Reinforced Lectern
- New: Wire Cutters can now be used to remove the passcode protection from a block via sneak-rightclicking
- New: SecurityCraft's /sc command can now be accessed via /securitycraft as well
- New: "/sc owner" command to set/fill the owner of blocks
- Change: The Reinforced Hopper screen now shows "Reinforced Hopper" instead of "Item Hopper" as its default inventory title
- Change: The hitbox of the Sentry has been changed, which means that only the base of Sentries can be interacted with now
- Change: The recipe for the Electrified Iron Fence/Fence Gate now requires any wooden reinforced fence/fence gate
- Change: Players now take 5 instead of 0.5 hearts of damage when suffocating inside reinforced blocks
- API: Refactored IPasscodeConvertible to account for the new Wire Cutters functionality
- Fix: Duplication exploit involving keycards
- Fix: Error involving SecurityCraft's creative tabs when running SecurityCraft alongside certain other mods
- Fix: Duplication exploit involving Reinforced Pistons
- Fix: The name of certain SecurityCraft blocks is displayed within brackets by WTHIT and similar mods
- Fix: Using bone meal on a Reinforced Grass Block does not grow plants on adjacent ones
- Fix: The wrong explosive icon is used in the Mine Remote Access Tool screen
- Fix: Inserting fuel into any type of passcode-protected furnace using hoppers/pipes/etc. puts the fuel into the wrong slot
- Fix: Items other than colored lenses can be inserted into some lens slots using hoppers/pipes/etc.
- Fix: More than one colored lens can be inserted into some lens slots using hoppers/pipes/etc.
- Fix: The color of a Claymore/Trophy System does not update when inserting a lens using hoppers/pipes/etc.
- Fix: Reinforced Ladders have no owner and thus cannot be removed (enable "allowBlockClaim" in the server config to set the owner of already placed ladders)
- Fix: Vanishmod support does not work with the Protecto
- Fix: Universal Block Reinforcers can be used under spawn protection
- Fix: Several blocks (Key Panel, Passcode-protected Chest, Rift Stabilizer, Security Camera) don't keep their custom name when they're broken
- Fix: Inventory Scanner dupe
- Fix: Items can be inserted into the prohibited item slots and storage of an Inventory Scanner
- Fix: Vanilla Hoppers and Reinforced Hoppers from different owners can take out items from a Reinforced Hopper
- Fix: A Portable Radar does not send a message to its owner if the owner is not part of a team
- Fix: Adding an empty Disguise Module to a Retinal Scanner makes the player face disappear
- Fix: The head of a Sentry in camouflage mode can visually retract while the Sentry is shooting at a target
- Fix: Sentries always animate their head upwards when they first appear for a player
- Fix: The position tooltip for a named Sentry in the Sentry Remote Access Tool is misplaced
- Fix: The Trophy System does not drop installed modules when the block below it is broken
- Fix: Manually editing the time in an Alarm and exiting the screen using the escape key does not save the time
- Fix: The Disguise Module still applies a disguise after the block inside the module was removed
- Fix: The "return" button in the Keycard Reader does not display correctly sometimes
- Fix: The block state preview does not show in the Disguise Module's and Projector's screen
- Fix: Fake Lava can ignite non-flammable blocks around itself
- Fix: Briefcases lose their passcode when they are moved to another slot in the Creative inventory screen
- Fix: Modules that are inserted in reinforced blocks get deleted when unreinforcing these blocks
- Fix: Mobs can be converted multiple times by an Electrified Iron Fence (Gate)
- Fix: The Electrified Iron Fence Gate can be used as fuel in a furnace
- Fix: Anyone can convert a Frame/Reinforced Iron Trapdoor to a Keypad/Passcode-protected Trapdoor
- Removed: Ability to change the subpage in the SecurityCraft Manual using the arrow keys. This is still possible by scrolling while holding down CTRL
- Misc.: The minimum required NeoForge version is 20.2.88 (necessary to fix players getting kicked from the server when placing a Sentry)

--------------------------Changelog for v1.9.8-beta4 of SecurityCraft--------------------------

- New: Reinforced Sand and Reinforced Red Sand are now in the minecraft:camel_sand_step_sound_blocks tag
- Fix: Compatibility with newer NeoForge versions
- Misc.: The minimum required NeoForge version is 20.2.86

--------------------------Changelog for v1.9.8-beta3 of SecurityCraft--------------------------

- Fix: Compatibility with NeoForge version 20.2.64-beta and later
- Misc.: The minimum required NeoForge version is 20.2.64-beta

--------------------------Changelog for v1.9.8-beta2 of SecurityCraft--------------------------

- Fix: Compatibility with NeoForge version 20.2.59-beta and later
- Misc.: The minimum required NeoForge version is 20.2.59-beta

--------------------------Changelog for v1.9.8-beta1 of SecurityCraft--------------------------

- Misc.: Several gui textures have been split up into sprites
- Misc.: The minimum required NeoForge version is 20.2.41-beta