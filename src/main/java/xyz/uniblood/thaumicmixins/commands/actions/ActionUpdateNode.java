package xyz.uniblood.thaumicmixins.commands.actions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.tiles.TileNode;
import xyz.uniblood.thaumicmixins.ThaumicMixins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ActionUpdateNode extends CommandAction
{
    private static final String USAGE_KEY = "commands.tmixins.updatenode.usage";
    private static final String KEY_INCOMPLETE = "commands.tmixins.updatenode.incomplete";
    private static final String KEY_MISSING = "commands.tmixins.updatenode.missing";
    private static final String KEY_MISSING_ASPECT = "commands.tmixins.updatenode.missingaspect";
    private static final String KEY_MISSING_MODIFIER = "commands.tmixins.updatenode.missingmodifier";
    private static final String KEY_MISSING_NODE = "commands.tmixins.updatenode.missingnode";
    private static final String KEY_MISSING_TYPE = "commands.tmixins.updatenode.missingtype";
    private static final String KEY_SUCCESS = "commands.tmixins.updatenode.success";

    private static final int ARG_INDEX_X = 1;
    private static final int ARG_INDEX_Y = 2;
    private static final int ARG_INDEX_Z = 3;

    private static final String FLAG_TYPE = "-t";
    private static final String FLAG_MODIFIER = "-m";
    private static final String FLAG_ADD_ASPECT = "-a";
    private static final String FLAG_REMOVE_ASPECT = "-r";

    @Override
    public String getName()
    {
        return "updateNode";
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
        AspectList addAspects = null;
        Collection<Aspect> removeAspects = null;
        ModifierOptions modifier = null;
        NodeType type = null;
        final var coords = parseCoordinates(sender, args[ARG_INDEX_X], args[ARG_INDEX_Y], args[ARG_INDEX_Z]);
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

        // Aspects to add or adjust in the node
        if (!searchResults.addAspectFlagIndices.isEmpty()) {
            addAspects = new AspectList();
        }
        for (var index = 0; index < searchResults.addAspectFlagIndices.size(); ++index) {
            final var aspectFlagNumber = index + 1;
            final var aspectFlagIndex = searchResults.addAspectFlagIndices.get(index);

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
            Objects.requireNonNull(addAspects).add(aspect, amount);
        }

        // Aspects to remove from the node
        if (!searchResults.removeAspectFlagIndices.isEmpty()) {
            removeAspects = new LinkedList<>();
        }
        for (var index = 0; index < searchResults.removeAspectFlagIndices.size(); ++index) {
            final var aspectFlagNumber = index + 1;
            final var aspectFlagIndex = searchResults.removeAspectFlagIndices.get(index);

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

            Objects.requireNonNull(removeAspects).add(aspect);
        }

        final var world = sender.getEntityWorld();
        final var tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof TileNode node)) {
            sendErrorMessage(sender, KEY_MISSING_NODE, x, y, z);
            return;
        }

        var dirty = false;
        var aspectsDirty = false;
        if (type != null) {
            node.setNodeType(type);
            dirty = true;
        }
        if (modifier != null) {
            node.setNodeModifier(modifier.toNodeModifier());
            dirty = true;
        }
        final var nodeAspects = node.getAspectsBase();
        if (removeAspects != null) {
            for (var aspect : removeAspects) {
                nodeAspects.remove(aspect);
            }
            aspectsDirty = true;
        }
        if (addAspects != null) {
            for (var aspect : addAspects.getAspects()) {
                nodeAspects.remove(aspect);
                nodeAspects.add(aspect, addAspects.getAmount(aspect));
            }
            aspectsDirty = true;
        }
        if (aspectsDirty) {
            node.setAspects(nodeAspects);
            dirty = true;
        }
        if (dirty) {
            node.markDirty();
            world.markBlockForUpdate(x, y, z);
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
            else if (FLAG_ADD_ASPECT.equalsIgnoreCase(arg)) {
                searchResults.addAspectFlagIndices.add(index);
                searchResults.lastAspectFlagIndex = index;
            }
            else if (FLAG_REMOVE_ASPECT.equalsIgnoreCase(arg)) {
                searchResults.removeAspectFlagIndices.add(index);
                searchResults.lastAspectFlagIndex = index;
            }
            else if (searchResults.possibleAspects.contains(argUpper) && FLAG_ADD_ASPECT.equalsIgnoreCase(args[index - 1])) {
                searchResults.selectedAspects.add(argUpper);
            }
        }
        return searchResults;
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
        if (searchResults.selectedAspects.contains(previousArg) && args[previousArgIndex - 1].equalsIgnoreCase(FLAG_ADD_ASPECT)) {
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
        if (searchResult.possibleAspects.size() > searchResult.selectedAspects.size()) {
            results.add(FLAG_ADD_ASPECT);
            results.add(FLAG_REMOVE_ASPECT);
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
        public int lastAspectFlagIndex = -1;

        public final List<Integer> addAspectFlagIndices = new ArrayList<>();
        public final List<Integer> removeAspectFlagIndices = new ArrayList<>();
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
