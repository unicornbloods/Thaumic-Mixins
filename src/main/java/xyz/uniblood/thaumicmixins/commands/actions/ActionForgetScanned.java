package xyz.uniblood.thaumicmixins.commands.actions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.research.ResearchManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ActionForgetScanned extends CommandAction
{
    public static final String USAGE_KEY = "commands.tmixins.forgetscanned.usage";
    public static final String INVALID_RESET_TYPE_KEY = "commands.tmixins.forgetscanned.invalidtype";
    public static final String SUCCESS_KEY = "commands.tmixins.forgetscanned.success";
    public static final String NOTIFY_KEY = "commands.tmixins.forgetscanned.notify";
    public static final String SUCCESS_ALL_KEY = "commands.tmixins.forgetscanned.successall";
    public static final String NOTIFY_ALL_KEY = "commands.tmixins.forgetscanned.notifyall";

    public static final int ARG_INDEX_TARGET_PLAYER = 1;
    public static final int ARG_INDEX_TARGET_TYPE = 2;

    @Override
    public String getName()
    {
        return "forgetScanned";
    }

    @Override
    public String getUsage()
    {
        return USAGE_KEY;
    }

    @Override
    public void process(ICommandSender sender, String[] args)
    {
        if (args.length < 3) {
            throw new WrongUsageException(USAGE_KEY);
        }
        final var player = argTargetPlayer(sender, args);
        final var resetType = argResetType(args);
        if (resetType == null) {
            sendErrorMessage(sender, INVALID_RESET_TYPE_KEY, args[ARG_INDEX_TARGET_TYPE]);
            return;
        }

        final var playerName = player.getCommandSenderName();
        final var playerKnowledge = Thaumcraft.proxy.getPlayerKnowledge();
        final var resetCount = switch (resetType) {
            case Objects -> forgetScanned(player, playerKnowledge.objectsScanned);
            case Entities -> forgetScanned(player, playerKnowledge.entitiesScanned);
            case Phenomena -> forgetScanned(player, playerKnowledge.phenomenaScanned);
            case All -> forgetScanned(player, playerKnowledge.objectsScanned, playerKnowledge.entitiesScanned, playerKnowledge.phenomenaScanned);
        };

        if (resetCount > 0) {
            ResearchManager.scheduleSave(player);
        }

        final var senderName = sender.getCommandSenderName();
        if (resetType == ResetType.All) {
            sendSuccessMessage(sender, SUCCESS_ALL_KEY, playerName, resetCount);
            sendColoredMessage(EnumChatFormatting.DARK_PURPLE, player, NOTIFY_ALL_KEY, senderName);
            return;
        }
        sendSuccessMessage(sender, SUCCESS_KEY, playerName, resetCount, resetType.toString());
        sendColoredMessage(EnumChatFormatting.DARK_PURPLE, player, NOTIFY_KEY, senderName, resetType.toString());
    }

    @SafeVarargs
    private int forgetScanned(EntityPlayerMP player, Map<String, ArrayList<String>>... resetMaps) {
        final var playerName = player.getCommandSenderName();
        var forgotten = 0;
        for (var map : resetMaps) {
            if (map == null) {
                continue;
            }
            final var scanList = map.get(playerName);
            if (scanList == null) {
                continue;
            }
            forgotten += scanList.size();
            scanList.clear();
        }
        return forgotten;
    }

    private static EntityPlayerMP argTargetPlayer(ICommandSender sender, String[] args) {
        if (args == null || args.length < ARG_INDEX_TARGET_PLAYER + 1) {
            return null;
        }
        return CommandBase.getPlayer(sender, args[ARG_INDEX_TARGET_PLAYER]);
    }

    private static ResetType argResetType(String[] args) {
        if (args == null || args.length < ARG_INDEX_TARGET_TYPE + 1) {
            return null;
        }
        return ResetType.fromString(args[ARG_INDEX_TARGET_TYPE]);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == ARG_INDEX_TARGET_PLAYER;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == ARG_INDEX_TARGET_PLAYER + 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        if (args.length == ARG_INDEX_TARGET_TYPE + 1) {
            final var options = Arrays.stream(ResetType.values()).map(ResetType::toString).toArray(String[]::new);
            return CommandBase.getListOfStringsMatchingLastWord(args, options);
        }
        return Collections.emptyList();
    }

    private enum ResetType {
        Objects,
        Entities,
        Phenomena,
        All;

        @Override
        public String toString() {
            return switch (this) {
                case Objects -> "objects";
                case Entities -> "entities";
                case Phenomena -> "nodes";
                case All -> "*";
            };
        }

        public static ResetType fromString(String string) {
            for (var val : ResetType.values()) {
                if (val.toString().equalsIgnoreCase(string)) {
                    return val;
                }
            }
            return null;
        }
    }
}
