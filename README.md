# Thaumic Mixins

A mod to add more configuration to Thaumcraft 4. Possibly bugfixes as well.

# Required Dependencies

* [Thaumcraft](https://www.curseforge.com/minecraft/mc-mods/thaumcraft/files/2227552) (of course)

# Current Features

* Toggle for each individual structure
* Config for structure rarity
* Toggle for Rare crates/urns in Mound structure
* Toggle for champion mobs dropping loot bags
* Config for max loot bag rarity from champions
* Whitelist for what dimensions nodes spawn in
* Admin command to list and trim player research and scans
* Auto-completion support for the `/thaumcraft` command
* Whitelist config for adding more possible champion mobs
* Blacklist config to remove champion mobs added by other mods

# Planned Features

* Toggle for village structure spawns
* Fix the maze generation
* Fix Excavation and Equal Trade foci being able to mine anything.
* Whitelist for structures to spawn in other dimensions
* Up frequency of champion mobs in certain dimensions / biomes?
* Better tooltip coloring, like for vis discounts?

# `/tmixins` Command

<details>
<summary>Spoiler</summary>

## Sub-commands
### findResearch
Usage: `/tmixins findResearch <search text...>`

| Argument | Description |
|:-:|:-|
| search text | Only results containing this text will be returned. The text can contain spaces. |

Filters through all research registered with Thaumcraft, returning any whose name or key contains the search text. For ease of searching, results are grouped by category.

### forgetResearch
Usage: `/tmixins forgetResearch <player> <research key | *> [refund sticky warp]`

| Argument | Description |
|:-:|:-|
| player | The player whose completed research will be modified |
| research key | The key of the root research to remove. Providing `*` instead will remove all of that player's research. Research registered as auto-unlocking is unaffected, though its children might be. |
| refund sticky warp | If true, an amount of sticky warp equal to that given by completing any forgotten research will be removed. |

Uncompletes some or all of the named player's completed research, allowing it to be completed again. If the research gave permanent warp, an equivalent amount is removed. If refund sticky warp is set, also removes an amount of sticky warp equal to that gained from the removed research.

### forgetScanned
Usage: `/tmixins forgetScanned <player> <objects | entities | nodes | *>`

| Argument | Description |
|:-:|:-|
| player | The player whose completed scans will be modified |
| objects \| entities \| nodes | Specifies the type of scan to reset. If `*` is provided, all will be reset. |

Clears the appropriate list of scanned things for the player, allowing them to be scanned again. Due to technical limitations (the list of scanned things involves hashes), this is all-or-nothing.

### listResearch
Usage: `/tmixins listResearch <player> [search text...]`

| Argument | Description |
|:-:|:-|
| player | The player whose completed research will be queried |
| search text | Only completed research whose key contains this text will be returned. The text can contain spaces. |

Returns the named player's list of completed research keys. Can optionally be filtered by search text.

### updateNode
Usage: `/tmixins updateNode <x> <y> <z> [-t <node_type>] [-m <node_modifier>] [-a <aspect1> <amount1>[ -a <aspect2> <amount2>[ ...]]] [-r <aspect1>[ -r <aspect2>[ ...]]]`

|        Argument        | Description                                                                  |
|:----------------------:|:-----------------------------------------------------------------------------|
|     `<x> <y> <z>`      | Required. The coordinates of the node to update.                             |
|    `-t <node_type>`    | Optional. Replace the node's type (hungry, pure, etc).                       |
|  `-m <node_modifier>`  | Optional. Replace the node's modifier (bright, fading, pale, or none).       |
| `-a <aspect> <amount>` | Optional. Set the node's capacity in the chosen aspect to the chosen amount. |
|     `-r <aspect>`      | Optional. Remove the chosen aspect from the node.                            |

Update an existing node at the specified coordinate.

### summonNode
Usage: `/tmixins summonNode <x> <y> <z> [-t <node_type>] [-m <node_modifier>] [--small] [-a <aspect1> <amount1>[ -a <aspect2> <amount2>[ ...]]]`

|        Argument        | Description                                                                                                                                                                                                      |
|:----------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|     `<x> <y> <z>`      | Required. The coordinates at which to create a new node.                                                                                                                                                         |
|    `-t <node_type>`    | Optional. Specify the new node's type (hungry, pure, etc). If not set, node type will be random.                                                                                                                 |
|  `-m <node_modifier>`  | Optional. Specify the new node's modifier (bright, fading, pale, or none). If not set, node modifier will be random.                                                                                             |
|       `--small`        | Optional. If set, the node will generate with few aspects in low capacities. No effect if any aspects are specified by `-a`.                                                                                     |
| `-a <aspect> <amount>` | Optional. Overrides `--small`. If set, the node will have the specified amount of the specified aspect. Can be set multiple times, adding a new aspect each time. If not set, the node's aspects will be random. |

Summon a random node at the specified coordinates. The node's properties can be overridden by specifying additional optional arguments.

</details>

# Credits

* Unicorn Blood - Main Dev
* GTNH + jss2a98aj - Helping me with random bugs
* rndmorris - tmixins command
