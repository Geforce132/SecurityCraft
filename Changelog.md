--------------------------Changelog for v1.9.12 of SecurityCraft--------------------------

- New: The Panic Button now supports the Allowlist Module
- New: Config setting to disable the ability of the Universal Block Reinforcer to un-/reinforce blocks that are placed in the world
- API: New method IPasscodeProtected#setPasscodeInAdjacentBlock for updating an adjacent block (e.g. the second half of a chest) with the passcode when setting it
- Fix: Placing a Passcode-protected Chest or Barrel directly leads to unnecessary data being saved
- Fix: Bouncing betties are rendered incorrectly
- Fix: The Universal Block Reinforcer's screen title does not display the item's actual name
- Misc.: Salt keys are now stored as integer arrays instead of strings, reducing file size

--------------------------Changelog for v1.9.11-beta3 of SecurityCraft--------------------------

- Fix: Entity translations of security sea boats do not exist

--------------------------Changelog for v1.9.11-beta2 of SecurityCraft--------------------------

- Fix: Security sea boats are not invulnerable to damage types they should be invulnerable to
- Fix: Jade and WTHIT integration
- Fix: In certain scenarios, it's not possible to join any world or multiplayer server
- Fix: Some pages in the SecurityCraft Manual appear twice

--------------------------Changelog for v1.9.11-beta1 of SecurityCraft--------------------------

- Change: Item rarities of the Admin Tool and Universal Block Reinforcer (Lvl 3) have been adjusted
- Misc: Security sea boat entity types have been split up just like vanilla's boats