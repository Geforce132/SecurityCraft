--------------------------Changelog for v1.9.10 of SecurityCraft--------------------------

- New: Server config setting "allow_camera_night_vision" to set whether players are able to activate night vision without having the actual potion effect
- New: Pressing "Enter" while typing a player name in an Allowlist/Denylist Module will now add the player to the list without needing to press the "Add Player" button
- Change: The cameraSpeed client side config setting has been moved to be a per-block option, accessible with the Universal Block Modifier
- Change: Some SecurityCraft tip messages have been reworded for clarity
- Change: Increased suffocation damage inside reinforced blocks no longer affects non-player entities and players owning the reinforced blocks
- Change: The "preventReinforcedFloorGlitching" configuration option no longer affects players trying to glitch through reinforced blocks that they are the owner of
- Change: Players in creative mode can once again use the codebreaker on their own blocks
- Change: The "codebreaker_chance" config setting has been moved to the "securitycraft:success_chance" item component
- Change: When picking up a placed sentry, the resulting sentry item will now be named according to the custom name of the removed sentry
- API: Changed constructors for IntOption and DoubleOption, they are now always sliders by default
- API: Removed FloatOption. Use DoubleOption instead
- Fix: Trying to place a Panic Button on top of powdered snow crashes the game
- Fix: Occasional crash when opening the inventory in creative mode in certain situations
- Fix: Reinforced fence gates don't properly retain their owner when reloading the world
- Fix: The debug world does not work with SecurityCraft installed
- Fix: The block pocket can be assembled without the necessary items
- Fix: Reinforcing a placed end rod will make the resulting reinforced end rod behave as if it had no owner until rejoining the world
- Fix: The Reinforced Lever has incorrect break/place sounds
- Fix: SecurityCraft's WTHIT config does not work on the client
- Fix: Crash when trying to toggle the redstone state of a camera immediately after mounting it
- Fix: Crash when trying to remove the passcode of a Briefcase using a Universal Key Changer
- Fix: The Display Case doesn't drop inserted modules when the block the display case is placed on is removed
- Fix: A previously open Display Case would replay its opening animation when joining a world or teleporting to it
- Fix: Fake Water/Fake Lava can be brewed using any kind of potion instead of only harming/healing potions
- Fix: Randomizing the signature of a Keycard Reader stops working when interacting with the block from certain angles
- Fix: Floor Trap cloud particles do not spawn when standing at certain positions relative to the Floor Trap
- Fix: Cloning a passcode-protected block using the /clone command will invalidate the passcode of the original block if the clone is removed
- Fix: Sonic Security System settings sometimes do not persist through world reloads

--------------------------Changelog for v1.9.9-beta2 of SecurityCraft--------------------------

- Fix: The block display in disguise modules and projectors is rendered too dark

--------------------------Changelog for v1.9.9-beta1 of SecurityCraft--------------------------

- Change: The "taser_effects" and "powered_taser_effects" config settings have been moved to the item component "minecraft:potion_contents" on the taser
- Fix: The Mine/Sentry Remote Access Tool and Sonic Security System neither save nor respect the level of the bound objects
- Misc.: The minimum required NeoForge version is 20.6.14-beta