package xyz.uniblood.thaumicmixins.commands.actions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import thaumcraft.common.lib.research.ResearchManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ActionForgetResearch extends CommandAction
{
    public static final String USAGE_KEY = "commands.tmixins.forgetresearch.usage";
    public static final String NONEXISTENT_RESEARCH_KEY = "commands.tmixins.forgetresearch.noresearch";
    public static final String MISSING_RESEARCH_KEY = "commands.tmixins.forgetresearch.doesnotknow";
    public static final String SUCCESS_KEY = "commands.tmixins.forgetresearch.success";
    public static final String NOTIFY_KEY = "commands.tmixins.forgetresearch.notify";

    public static final int ARG_INDEX_TARGET_PLAYER = 1;
    public static final int ARG_INDEX_RESEARCH_KEY = 2;
    public static final int ARG_INDEX_REFUND_STICKY = 3;

    public ActionForgetResearch(CommandBase owningCommand)
    {
        super(owningCommand);
    }

    @Override
    public String getName()
    {
        return "forgetResearch";
    }

    @Override
    public String getUsage()
    {
        return USAGE_KEY;
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length < 3) {
            throw new WrongUsageException(USAGE_KEY);
        }
        final var targetPlayer = argTargetPlayer(sender, args);
        final var targetResearch = argTargetResearch(args);
        if (targetResearch == null) {
            sendErrorMessage(sender, NONEXISTENT_RESEARCH_KEY, args[ARG_INDEX_RESEARCH_KEY]);
            return;
        }
        final var refundStickyWarp = argRefundStickyWarp(sender, args);
        forgetResearchAndRelated(sender, targetPlayer, targetResearch, refundStickyWarp);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 1;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == ARG_INDEX_TARGET_PLAYER + 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        if (args.length == ARG_INDEX_RESEARCH_KEY + 1) {
            final var player = argTargetPlayer(sender, args);
            if (player == null) {
                return Collections.emptyList();
            }
            return CommandBase.getListOfStringsFromIterableMatchingLastWord(
                args,
                new ArrayList<>(getResearchKeys(player.getCommandSenderName()))
            );
        }
        if (args.length == ARG_INDEX_REFUND_STICKY + 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "true", "false");
        }
        return Collections.emptyList();
    }

    private static EntityPlayerMP argTargetPlayer(ICommandSender sender, String[] args) {
        if (args == null || args.length < ARG_INDEX_TARGET_PLAYER + 1) {
            return null;
        }
        return CommandBase.getPlayer(sender, args[ARG_INDEX_TARGET_PLAYER]);
    }

    private static ResearchItem argTargetResearch(String[] args) {
        if (args == null || args.length < ARG_INDEX_RESEARCH_KEY + 1) {
            return null;
        }
        return ResearchCategories.getResearch(args[ARG_INDEX_RESEARCH_KEY]);
    }

    private static boolean argRefundStickyWarp(ICommandSender sender, String[] args) {
        if (args == null || args.length < ARG_INDEX_REFUND_STICKY + 1) {
            return false;
        }
        return CommandBase.parseBoolean(sender, args[ARG_INDEX_REFUND_STICKY]);
    }

    private List<String> getResearchKeys(String playerName) {
        return Thaumcraft.proxy
            .getPlayerKnowledge()
            .researchCompleted
            .get(playerName);
    }

    private void forgetResearchAndRelated(ICommandSender sender, EntityPlayerMP targetPlayer, ResearchItem rootResearch, boolean refundStickyWarp) {
        final var playerName = targetPlayer.getCommandSenderName();
        final var researchName = rootResearch.getName();
        if (!ResearchManager.isResearchComplete(playerName, rootResearch.key)) {
            sendErrorMessage(sender, MISSING_RESEARCH_KEY, playerName, researchName);
            return;
        }

        // This should always be a reference to the exact list TC4 uses in-memory
        final var knownResearchKeys = getResearchKeys(playerName);

        // Used for looking up sibling and descendant research
        final var knownResearchData = knownResearchKeys.stream()
            .filter(Objects::nonNull)
            .map(ResearchCategories::getResearch)
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(r -> r.key, r -> r));

        final var forgetQueue = new LinkedList<String>();
        final var hasBeenQueued = new HashSet<String>();

        forgetQueue.add(rootResearch.key);
        hasBeenQueued.add(rootResearch.key);

        var permanentWarpRefund = 0;
        var stickyWarpRefund = 0;

        while (!forgetQueue.isEmpty()) {
            final var currentKey = forgetQueue.poll();
            final var researchItem = knownResearchData.get(currentKey);

            // auto-unlocked research is *always* available,
            // even if it has parents, so we skip those
            if (!researchItem.isAutoUnlock()) {
                final var index = knownResearchKeys.indexOf(currentKey);
                if (index >= 0) {
                    knownResearchKeys.remove(index);
                }

                // see ResearchManager.completeResearch(...)
                final var totalWarp = ThaumcraftApi.getWarp(currentKey);
                if(totalWarp > 0 && !Config.wuss && !targetPlayer.worldObj.isRemote) {
                    final var stickyWarp = totalWarp / 2;
                    final var permanentWarp = totalWarp - stickyWarp;
                    permanentWarpRefund += permanentWarp;
                    stickyWarpRefund += stickyWarp;
                }
            }

            // queue up any related research that needs to be forgotten
            knownResearchData.values()
                .stream()
                .filter(item -> !hasBeenQueued.contains(item.key) && isRelated(item, currentKey))
                .forEach(item -> {
                    forgetQueue.add(item.key);
                    hasBeenQueued.add(item.key);
                });
        }

        adjustPermanentWarp(targetPlayer, permanentWarpRefund);
        if (refundStickyWarp) {
            adjustStickyWarp(targetPlayer, stickyWarpRefund);
        }
        ResearchManager.scheduleSave(targetPlayer);

        sendSuccessMessage(sender, SUCCESS_KEY, playerName, researchName);
        sendColoredMessage(EnumChatFormatting.DARK_PURPLE, targetPlayer, NOTIFY_KEY, sender.getCommandSenderName(), researchName);
    }

    private static boolean isRelated(ResearchItem item, String query) {
        return checkArray(item.siblings, query) || checkArray(item.parents, query) || checkArray(item.parentsHidden, query);
    }

    private static boolean checkArray(String[] arr, String query) {
        return arr != null && arr.length > 0 && Arrays.asList(arr).contains(query);
    }

    private void adjustPermanentWarp(EntityPlayerMP player, int amount) {
        final var name = player.getCommandSenderName();
        final var warp = Thaumcraft.proxy.playerKnowledge.getWarpPerm(name);
        Thaumcraft.proxy.playerKnowledge.setWarpPerm(name, Integer.max(warp - amount, 0));
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte)0), player);
    }

    private void adjustStickyWarp(EntityPlayerMP player, int amount) {
        final var name = player.getCommandSenderName();
        final var warp = Thaumcraft.proxy.playerKnowledge.getWarpSticky(name);
        Thaumcraft.proxy.playerKnowledge.setWarpSticky(name, Integer.max(warp - amount, 0));
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte)1), player);
    }
}
