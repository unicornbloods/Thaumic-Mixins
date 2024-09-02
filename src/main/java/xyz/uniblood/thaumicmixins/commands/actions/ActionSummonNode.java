package xyz.uniblood.thaumicmixins.commands.actions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ActionSummonNode extends CommandAction
{
    public static final String USAGE_KEY = "commands.tmixins.summonnode.usage";

    private static final int ARG_INDEX_X = 1;
    private static final int ARG_INDEX_Y = 2;
    private static final int ARG_INDEX_Z = 3;

    private static final String FLAG_TYPE = "-t";
    private static final String FLAG_MODIFIER = "-m";
    private static final String FLAG_SMALL = "--small";
    private static final String FLAG_ASPECT = "-a";

    private static final String MODIFIER_NONE = "NONE";

    @Override
    public String getName()
    {
        return "summonNode";
    }

    @Override
    public String getUsage()
    {
        return "";
    }

    @Override
    public void process(ICommandSender sender, String[] args)
    {

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length <= ARG_INDEX_Z + 1) {
            return null;
        }

        final var searchResults = searchArgs(args);
        final var previousArgIndex = args.length - 2;

        if (searchResults.typeIndex == previousArgIndex) {
            return typeOptions(args);
        }
        if (searchResults.modifierIndex == previousArgIndex) {
            return modifierOptions(args);
        }
        if (searchResults.lastAspectIndex == previousArgIndex) {
            return aspectOptions(searchResults, args);
        }
        final var previousArg = args[previousArgIndex].toUpperCase();
        if (searchResults.selectedAspects.contains(previousArg)) {
            return null; // integer argument
        }

        return flagOptions(searchResults, args);
    }

    private static ArgsSearchResult searchArgs(String[] args) {
        final var searchResults = new ArgsSearchResult();
        for (var index = 1; index < args.length; ++index) {
            final var arg = args[index];
            final var argUpper = arg.toUpperCase();
            if (FLAG_TYPE.equalsIgnoreCase(arg) && searchResults.typeIndex < 0) {
                searchResults.typeIndex = index;
            }
            else if (FLAG_MODIFIER.equalsIgnoreCase(arg) && searchResults.modifierIndex < 0) {
                searchResults.modifierIndex = index;
            }
            else if (FLAG_SMALL.equalsIgnoreCase(arg) && searchResults.smallIndex < 0) {
                searchResults.smallIndex = index;
            }
            else if (FLAG_ASPECT.equalsIgnoreCase(arg)) {
                searchResults.lastAspectIndex = index;
            }
            else if (searchResults.possibleAspects.contains(argUpper)) {
                searchResults.selectedAspects.add(argUpper);
            }
        }
        return searchResults;
    }

    private static class ArgsSearchResult
    {
        public int typeIndex = -1;
        public int modifierIndex = -1;
        public int smallIndex = -1;
        public int lastAspectIndex = -1;

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

    private List<String> flagOptions(ArgsSearchResult searchResult, String[] args) {
        final var results = new ArrayList<String>(3);
        if (searchResult.typeIndex < 0) {
            results.add(FLAG_TYPE);
        }
        if (searchResult.modifierIndex < 0) {
            results.add(FLAG_MODIFIER);
        }
        if (searchResult.smallIndex < 0) {
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
        final var options = new ArrayList<String>(4);
        options.add(MODIFIER_NONE);
        final var modifiers = Arrays.stream(NodeModifier.values())
                .map(NodeModifier::toString)
                .toArray(String[]::new);
        Collections.addAll(options, modifiers);
        return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, options);
    }

    private List<String> aspectOptions(ArgsSearchResult searchResult, String[] args) {
        return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, searchResult.remainingAspects());
    }
}
