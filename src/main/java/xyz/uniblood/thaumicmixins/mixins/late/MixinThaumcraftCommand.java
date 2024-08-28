package xyz.uniblood.thaumicmixins.mixins.late;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.lib.events.CommandThaumcraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = CommandThaumcraft.class, remap = false)
public abstract class MixinThaumcraftCommand extends CommandBase
{
    @Unique
    private final static String ACTION_HELP = "help";
    @Unique
    private final static String ACTION_ASPECT = "aspect";
    @Unique
    private final static String ACTION_RESEARCH = "research";
    @Unique
    private final static String ACTION_RESEARCH_LIST = "list";
    @Unique
    private final static String ACTION_WARP = "warp";

    /**
     * @author rndmorris
     * @reason The base implementation only returns null.
     */
    @Overwrite
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, ACTION_HELP, ACTION_ASPECT, ACTION_RESEARCH, ACTION_WARP);
        }
        final var action = args[0];
        if (ACTION_HELP.equalsIgnoreCase(action)) {
            return null;
        }
        if (ACTION_ASPECT.equalsIgnoreCase(action)) {
            return switch (args.length) {
                case 2 -> thaumic_Mixins$usernameArg(args);
                case 3 -> thaumic_Mixins$actionAspectArg2(args);
                default -> null; // The final argument is a positive integer, no auto-complete needed.
            };
        }
        if (ACTION_RESEARCH.equalsIgnoreCase(action)) {
            return switch (args.length) {
                case 2 -> thaumic_Mixins$actionResearchArg1(args);
                case 3 -> thaumic_Mixins$actionResearchArg2(args);
                default -> null;
            };
        }
        if (ACTION_WARP.equalsIgnoreCase(action)) {
            return switch (args.length) {
                case 2 -> thaumic_Mixins$usernameArg(args);
                case 3 -> getListOfStringsMatchingLastWord(args, "add", "set");
                case 4 -> null;  // Positive integer, no auto-complete needed
                case 5 -> getListOfStringsMatchingLastWord(args, "PERM", "TEMP");
                default -> null;
            };
        }
        return null;
    }

    @Unique
    private static List<String> thaumic_Mixins$usernameArg(String[] args) {
        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
    }

    @Unique
    private static List<String> thaumic_Mixins$actionAspectArg2(String[] args) {
        final var aspects = Aspect.aspects.keySet();
        return getListOfStringsFromIterableMatchingLastWord(args, aspects);
    }

    @Unique
    private static List<String> thaumic_Mixins$actionResearchArg1(String[] args) {
        final var usernames = MinecraftServer.getServer().getAllUsernames();
        final var results = new ArrayList<String>(usernames.length + 1);
        results.add(ACTION_RESEARCH_LIST);
        Collections.addAll(results, usernames);
        return getListOfStringsFromIterableMatchingLastWord(args, results);
    }

    @Unique
    private static List<String> thaumic_Mixins$actionResearchArg2(String[] args) {
        if (ACTION_RESEARCH_LIST.equalsIgnoreCase(args[1])) {
            return null;
        }
        final var researchKeys = ResearchCategories.researchCategories
            .values()
            .stream()
            .flatMap(c -> c.research.keySet().stream())
            .sorted()
            .toArray(String[]::new);

        final var results = new ArrayList<String>(researchKeys.length + 2);
        Collections.addAll(results, "all", "reset");
        Collections.addAll(results, researchKeys);
        return getListOfStringsFromIterableMatchingLastWord(args, results);
    }
}
