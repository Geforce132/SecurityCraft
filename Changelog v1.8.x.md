--------------------------Changelog for v1.8.20 of SecurityCraft--------------------------

- New: Reinforced Blocks: Crimson Nylium, Warped Nylium, Crimson Planks, Warped Planks, Crimson Stem, Warped Stem, Stripped Crimson Stem, Stripped Warped Stem, Stripped Crimson Hyphae, Stripped Warped Hyphae, Crimson Hyphae, Warped Hyphae, Crismon Slab, Warped Slab, Soul Soil, Basalt, Polished Basalt, Cracked Nether Bricks, Chiseled Nether Bricks, Crimson Stairs, Warped Stairs, Quartz Bricks, Warped Wart Block, Netherite Block, Crying Obsidian, Blackstone, Blackstone Slab, Blackstone Stairs, Polished Blackstone, Polished Blackstone Slab, Polished Blackstone Stairs, Polished Blackstone Bricks, Polished Blackstone Brick Slab, Polished Blackstone Brick Stairs, Chain, Blackstone Wall, Polished Blackstone Wall, Polished Blackstone Brick Wall, Soul Lantern, Shroomlight, Crimson Pressure Plate, Warped Pressure Plate, Polished Blackstone Pressure Plate, Crimson Button, Warped Button, Polished Blackstone Button (Thanks Redstone_Dubstep!)
- New: Reinforced Lantern (Thanks Redstone_Dubstep!)
- New: Ancient Debris Mine, Gilded Blackstone Mine, Nether Gold Ore Mine (Thanks Redstone_Dubstep!)
- New: Secret Crimson and Secret Warped Signs (Thanks Redstone_Dubstep!)
- New: Piglins now love Reinforced Gold Blocks (Thanks Redstone_Dubstep!)
- New: Striders now find Fake Lava comfortably warm (Thanks Redstone_Dubstep!)
- New: Reinforced Blocks are now immune to lava (Thanks Redstone_Dubstep!)
- New: Block/item tags: securitycraft:reinforced/crimson_stems, securitycraft:reinforced/nylium, securitycraft:reinforced/pressure_plates, securitycraft:reinforced/warped_stems (Thanks Redstone_Dubstep!)
- New: Support for the following block/item tags: minecraft:infiniburn_overworld, minecraft:mushroom_grow_block, minecraft:nylium, minecraft:pressure_plates, minecraft:soul_fire_base_blocks, minecraft:soul_speed_blocks, minecraft:strider_warm_blocks, minecraft:wither_summon_base_blocks, minecraft:piglin_loved (Thanks Redstone_Dubstep!)
- New: The name of a camera is now displayed in the top right when the player is mounted to it
- New: Inventory Scanner modifying option to have inventory scanner fields be horizontal
- New: A briefcase's owner can now be changed if its owner rightclicks while holding the briefcase in their off hand and a named Universal Owner Changer in their main hand (Thanks Redstone_Dubstep!)
- New: The codebreaker can now be used on a briefcase by holding the briefcase in the off hand and the codebreaker in the main hand and rightclicking (Thanks Redstone_Dubstep!)
- New: Customization option to change how long the Keycard Reader emits a redstone signal when it has been activated (Thanks Redstone_Dubstep!)
- New: Secret Signs can now have a whitelist (Thanks Redstone_Dubstep!)
- New: New customization option to make the text of Secret Signs visible to everyone (Thanks Redstone_Dubstep!)
- Change: Sounds of reinforced blocks now match the sounds of their vanilla equivalent (Thanks Redstone_Dubstep!)
- Change: Inventory Scanner Fields now cannot be destroyed when between two Inventory Scanners (Thanks Redstone_Dubstep!)
- Change: Laser and Taser damage no longer bypasses armor
- Change: The Admin Tool now only works in creative mode
- Change: The inventory scanner's texture is now up to date with the new Minecraft textures
- Change: Instead of using a crafting table, a briefcase's code can now be reset if its owner rightclicks while holding the briefcase in their off hand and a Universal Key Changer in their main hand. (Thanks Redstone_Dubstep!)
- Change: Balanced Fake Liquids: Reduced damage of Fake Water by 70% and added regeneration effect to Fake Lava instead of healing instantly
- Change: The Panic Button now emits a light level of 4 when turned on
- Change: Just Enough Items now shows proper recipes for un-/reinforcing blocks - the old info screen has been removed in favor of this
- Change: The level 1 Universal Block Reinforcer can no longer unreinforce blocks. At least level 2 is required for this now
- Change: The codebreaker is now 100% effective when using in creative mode
- API: Added hook for mods to define blocks that can extract from Password-protected Chests/Furnaces. Fore more info, see IExtractionBlock
- Fix: Taser does not reliably hit entities (Thanks Redstone_Dubstep!)
- Fix: Camera overlay's time and hotkey texts sometimes get obscured or render partly offscreen
- Fix: Walking through a block pocket wall blocks vision
- Fix: Anyone can reset a briefcase's passcode (Thanks Redstone_Dubstep!)
- Fix: Confirm button in the Universal Key Changer's GUI doesn't properly react to changes in the textboxes (Thanks Redstone_Dubstep!)
- Fix: Defused Claymore model is incorrect (Thanks Redstone_Dubstep!)
- Fix: Keycard Reader sometimes sends incorrect messages (Thanks Redstone_Dubstep!)
- Fix: Tooltips in SecurityCraft Manual don't completely show on the screen
- Fix: Secret Signs can be colored by anyone (Thanks Redstone_Dubstep!)
- Fix: Reinforced Hopper does not drop
- Fix: Redstone cannot be placed on Reinforced Hoppers
- Fix: Fire doesn't get removed after exiting Fake Lava
- Fix: Reinforced Doors and Scanner Doors cannot be placed by rightclick the side of a block
- Fix: Reinforced Doors and Scanner Doors can remove blocks
- Fix: Rare crash
- Fix: Transparent pixels in Codebreaker and Sentry Remote Access Tool textures
- Fix: Panic button has no sound
- Fix: Sliders in GUIs sometimes don't stop sliding after the mouse button has been released
- Fix: Universal Block Reinforcer dupe
- Fix: Pipe interaction with Password-protected Chest/Furnace
- Fix: Portable Radar sometimes doesn't turn off the redstone signal when it's supposed to
- Fix: Rotating/mirroring some blocks (e.g. using mods like World Edit) does not work correctly
- Fix: Sentry bullets don't disappear upon hitting a block
- Fix: Sentry bullets don't damage shields
- Fix: Vanilla redstone power sources can still activate Reinforced Doors and similar in specific cases
- Removed: Taser Bullet entity
- Removed: Fix for Cyclic's Sack of Holding (Cyclic has a fix in place itself)
- Potential Fix: First Alarm sound sometimes does not play
- Misc.: Various French language fixes (Thanks supercat95!)
- Misc.: The minimum required Forge version is now 34.0.3

--------------------------Changelog for v1.8.19.3 of SecurityCraft--------------------------

- Fix: Harming Module recipe doesn't work
- Fix: Incompatibility with Forge 33.0.22+

--------------------------Changelog for v1.8.19.2 of SecurityCraft--------------------------

- Fix: Incompatibility with Forge 33.0.10+
- Fix: WAILA exploit
- Fix: Reinforced double stone slab is not tinted (Thanks Redstone_Dubstep!)
- Fix: Incorrect item group name

--------------------------Changelog for v1.8.19.1 of SecurityCraft (since beta2)--------------------------

- Fix: Server crash with disguised blocks

--------------------------Changelog for v1.8.19.1-beta2 of SecurityCraft--------------------------

- Misc.: Re-enable fix for Cyclic's Sack of Holding
- Misc.: Re-enable HWYLA integration

--------------------------Changelog for v1.8.19.1-beta1 of SecurityCraft--------------------------

- Fix: Welcome message doesn't show
- Fix: Stuttering when quickly reinforcing blocks placed in the world
- Misc.: The minimum required Forge version is 32.0.67