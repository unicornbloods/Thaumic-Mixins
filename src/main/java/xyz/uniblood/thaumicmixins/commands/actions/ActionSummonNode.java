package xyz.uniblood.thaumicmixins.commands.actions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;
import thaumcraft.common.tiles.TileNode;
import xyz.uniblood.thaumicmixins.ThaumicMixins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ActionSummonNode extends CommandAction
{
    private static final String USAGE_KEY = "commands.tmixins.summonnode.usage";
    private static final String KEY_ERROR = "commands.tmixins.summonnode.error";
    private static final String KEY_INCOMPLETE = "commands.tmixins.summonnode.incomplete";
    private static final String KEY_MISSING = "commands.tmixins.summonnode.missing";
    private static final String KEY_MISSING_ASPECT = "commands.tmixins.summonnode.missingaspect";
    private static final String KEY_MISSING_MODIFIER = "commands.tmixins.summonnode.missingmodifier";
    private static final String KEY_MISSING_TYPE = "commands.tmixins.summonnode.missingtype";
    private static final String KEY_SUCCESS = "commands.tmixins.summonnode.success";

    private static final int ARG_INDEX_X = 1;
    private static final int ARG_INDEX_Y = 2;
    private static final int ARG_INDEX_Z = 3;

    private static final String FLAG_TYPE = "-t";
    private static final String FLAG_MODIFIER = "-m";
    private static final String FLAG_SMALL = "--small";
    private static final String FLAG_ASPECT = "-a";

    @Override
    public String getName()
    {
        return "summonNode";
    }

    @Override
    public String getUsage()
    {
        return USAGE_KEY;
    }

    @Override
    public void process(ICommandSender sender, String[] args)
    {
        if (args.length < ARG_INDEX_Z + 1) {
            throw new WrongUsageException(USAGE_KEY);
        }

        final var searchResults = searchArgs(args);
        AspectList aspects = null;
        ModifierOptions modifier = null;
        NodeType type = null;
        boolean small = false;
        final var coords = parseCoordinates(sender, args);
        final int x = (int) coords.xCoord, y = (int) coords.yCoord, z = (int) coords.zCoord;

        if (searchResults.typeFlagIndex > -1) {
            final var typeIndex = searchResults.typeFlagIndex + 1;
            if (typeIndex >= args.length) {
                sendErrorMessage(sender, KEY_INCOMPLETE, "node_type");
                return;
            }
            final var typeString = args[typeIndex];
            type = tryParseEnum(NodeType.class, typeString);
            if (type == null) {
                sendErrorMessage(sender, KEY_MISSING_TYPE, typeString);
                return;
            }
        }

        if (searchResults.modifierFlagIndex > -1) {
            final var modifierIndex = searchResults.modifierFlagIndex + 1;
            if (modifierIndex >= args.length) {
                sendErrorMessage(sender, KEY_INCOMPLETE, "node_modifier");
                return;
            }
            final var modifierString = args[modifierIndex];
            modifier = tryParseEnum(ModifierOptions.class, modifierString);
            if (modifier == null) {
                sendErrorMessage(sender, KEY_MISSING_MODIFIER, modifierString);
                return;
            }
        }

        if (searchResults.smallFlagIndex > -1) {
            small = true;
        }

        if (!searchResults.selectedAspects.isEmpty()) {
            aspects = new AspectList();
        }

        for (var index = 0; index < searchResults.aspectFlagIndices.size(); ++index) {
            final var aspectFlagNumber = index + 1;
            final var aspectFlagIndex = searchResults.aspectFlagIndices.get(index);

            final var aspectIndex = aspectFlagIndex + 1;
            if (aspectIndex >= args.length) {
                sendErrorMessage(sender, KEY_INCOMPLETE, "aspect" + aspectFlagNumber);
                return;
            }
            final var aspect = parseAspect(args[aspectIndex]);
            if (aspect == null) {
                sendErrorMessage(sender, KEY_MISSING_ASPECT, args[aspectIndex]);
                return;
            }

            final var amountIndex = aspectIndex + 1;
            if (amountIndex >= args.length) {
                sendErrorMessage(sender, KEY_INCOMPLETE, "amount" + aspectFlagNumber);
                return;
            }
            final var amount = tryParseInt(args, amountIndex);
            if (amount == null) {
                sendErrorMessage(sender, KEY_MISSING, "amount" + aspectFlagNumber);
                return;
            }
            Objects.requireNonNull(aspects).add(aspect, amount);
        }

        final var world = sender.getEntityWorld();
        ThaumcraftWorldGenerator.createRandomNodeAt(world, x, y, z, world.rand, false, false, small);
        final var tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof TileNode node)) {
            ThaumicMixins.LOG.error("Could not find a TileNode at ({}, {}, {})", x, y, z);
            sendErrorMessage(sender, KEY_ERROR);
            return;
        }

        var dirty = false;
        if (type != null) {
            node.setNodeType(type);
            dirty = true;
        }
        if (modifier != null) {
            node.setNodeModifier(modifier.toNodeModifier());
            dirty = true;
        }
        if (aspects != null) {
            node.setAspects(aspects);
            dirty = true;
        }
        if (dirty) {
            node.markDirty();
        }

        sendSuccessMessage(sender, KEY_SUCCESS, x, y, z);
    }

    private static ArgsSearchResult searchArgs(String[] args) {
        final var searchResults = new ArgsSearchResult();
        for (var index = 1; index < args.length; ++index) {
            final var arg = args[index];
            final var argUpper = arg.toUpperCase();
            if (FLAG_TYPE.equalsIgnoreCase(arg) && searchResults.typeFlagIndex < 0) {
                searchResults.typeFlagIndex = index;
            }
            else if (FLAG_MODIFIER.equalsIgnoreCase(arg) && searchResults.modifierFlagIndex < 0) {
                searchResults.modifierFlagIndex = index;
            }
            else if (FLAG_SMALL.equalsIgnoreCase(arg) && searchResults.smallFlagIndex < 0) {
                searchResults.smallFlagIndex = index;
            }
            else if (FLAG_ASPECT.equalsIgnoreCase(arg)) {
                searchResults.aspectFlagIndices.add(index);
                searchResults.lastAspectFlagIndex = index;
            }
            else if (searchResults.possibleAspects.contains(argUpper) && FLAG_ASPECT.equalsIgnoreCase(args[index - 1])) {
                searchResults.selectedAspects.add(argUpper);
            }
        }
        return searchResults;
    }

    private Vec3 parseCoordinates(ICommandSender sender, String[] args) {
        final var playerCoords = sender.getPlayerCoordinates();
        var x = playerCoords.posX;
        var y = playerCoords.posY;
        var z = playerCoords.posZ;
        x = MathHelper.floor_double(CommandBase.func_110666_a(sender, x, args[ARG_INDEX_X]));
        y = MathHelper.floor_double(CommandBase.func_110666_a(sender, y, args[ARG_INDEX_Y]));
        z = MathHelper.floor_double(CommandBase.func_110666_a(sender, z, args[ARG_INDEX_Z]));
        return Vec3.createVectorHelper(x, y, z);
    }

    private Integer tryParseInt(String[] args, int atIndex) {
        if (atIndex >= args.length) {
            return null;
        }
        try {
            return Integer.parseInt(args[atIndex]);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private <E extends Enum<E>> E tryParseEnum(Class<E> clazz, String arg) {
        try {
            return Enum.valueOf(clazz, arg);
        }
        catch (IllegalArgumentException iae) {
            return null;
        }
    }

    private Aspect parseAspect(String tag) {
        var aspect = Aspect.getAspect(tag);
        if (aspect != null) {
            return aspect;
        }
        for (var iAspect : Aspect.aspects.values()) {
            if (iAspect.getTag().equalsIgnoreCase(tag))
            {
                return iAspect;
            }
        }
        return null;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length > 1 && args.length <= 4) {
            return Collections.singletonList("~");
        }

        final var searchResults = searchArgs(args);
        final var previousArgIndex = args.length - 2;

        if (searchResults.typeFlagIndex == previousArgIndex) {
            return typeOptions(args);
        }
        if (searchResults.modifierFlagIndex == previousArgIndex) {
            return modifierOptions(args);
        }
        if (searchResults.lastAspectFlagIndex == previousArgIndex) {
            return aspectOptions(searchResults, args);
        }
        final var previousArg = args[previousArgIndex].toUpperCase();
        if (searchResults.selectedAspects.contains(previousArg)) {
            return null; // integer argument
        }

        return flagOptions(searchResults, args);
    }

    private List<String> flagOptions(ArgsSearchResult searchResult, String[] args) {
        final var results = new ArrayList<String>(3);
        if (searchResult.typeFlagIndex < 0) {
            results.add(FLAG_TYPE);
        }
        if (searchResult.modifierFlagIndex < 0) {
            results.add(FLAG_MODIFIER);
        }
        if (searchResult.smallFlagIndex < 0) {
            results.add(FLAG_SMALL);
        }
        if (searchResult.possibleAspects.size() > searchResult.selectedAspects.size()) {
            results.add(FLAG_ASPECT);
        }
        return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, results);
    }

    private List<String> typeOptions(String[] args) {
        final var types = Arrays.stream(NodeType.values())
            .map(NodeType::toString)
            .toArray(String[]::new);
        return CommandBase.getListOfStringsMatchingLastWord(args, types);
    }

    private List<String> modifierOptions(String[] args) {
        final var modifiers = Arrays.stream(ModifierOptions.values())
                .map(ModifierOptions::toString)
                .toArray(String[]::new);
        return CommandBase.getListOfStringsMatchingLastWord(args, modifiers);
    }

    private List<String> aspectOptions(ArgsSearchResult searchResult, String[] args) {
        return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, searchResult.remainingAspects());
    }

    private static class ArgsSearchResult {
        public int typeFlagIndex = -1;
        public int modifierFlagIndex = -1;
        public int smallFlagIndex = -1;
        public int lastAspectFlagIndex = -1;

        public final List<Integer> aspectFlagIndices = new ArrayList<>();
        public final Set<String> selectedAspects = new HashSet<>();
        public final Set<String> possibleAspects;

        public ArgsSearchResult() {
            this.possibleAspects = Aspect.aspects.keySet().stream().map(String::toUpperCase).collect(Collectors.toSet());
        }

        /**
         * Get a set of all aspect keys that are not yet selected.
         * @return a set of string
         */
        public Set<String> remainingAspects() {
            final var result = new HashSet<>(possibleAspects);
            result.removeAll(selectedAspects);
            return result;
        }
    }

    private enum ModifierOptions {
        NONE,
        BRIGHT,
        PALE,
        FADING;

        public NodeModifier toNodeModifier() {
            return switch (this) {
                case NONE -> null;
                case BRIGHT -> NodeModifier.BRIGHT;
                case PALE -> NodeModifier.PALE;
                case FADING -> NodeModifier.FADING;
            };
        }
    }
}
