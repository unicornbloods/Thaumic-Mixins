package xyz.uniblood.thaumicmixins.commands.actions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import thaumcraft.common.Thaumcraft;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ActionListResearch extends CommandAction
{
    public static final String USAGE_KEY = "commands.tmixins.listresearch.usage";

    public static final int ARG_INDEX_TARGET_PLAYER = 1;
    public static final int ARG_INDEX_SEARCH_TEXT_START = 2;

    public ActionListResearch(CommandBase owningCommand) {
        super(owningCommand);
    }

    @Override
    public String getName()
    {
        return "listResearch";
    }

    @Override
    public String getUsage() {
        return USAGE_KEY;
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException(USAGE_KEY);
        }
        final var targetPlayer = argTargetPlayer(sender, args);
        final var searchText = argSearchText(args);

        var researchList = Optional
            .ofNullable((List<String>) Thaumcraft.proxy.getPlayerKnowledge().researchCompleted.get(targetPlayer.getCommandSenderName()))
            .orElse(Collections.emptyList())
            .stream();
        if (searchText != null) {
            final var search = searchText.toUpperCase();
            researchList = researchList.filter(key -> key.toUpperCase().contains(search));
        }

        final var messageContents = new StringBuilder();
        final var iterator = researchList.iterator();
        while (iterator.hasNext()) {
            final var key = iterator.next();
            messageContents.append(key);
            if (iterator.hasNext()) {
                messageContents.append(", ");
            }
        }
        final var message = new ChatComponentText(messageContents.toString());
        message.getChatStyle().setColor(EnumChatFormatting.DARK_PURPLE);
        sender.addChatMessage(message);
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
        return Collections.emptyList();
    }

    private static EntityPlayerMP argTargetPlayer(ICommandSender sender, String[] args) {
        if (args == null || args.length < ARG_INDEX_TARGET_PLAYER + 1) {
            return null;
        }
        return CommandBase.getPlayer(sender, args[ARG_INDEX_TARGET_PLAYER]);
    }
    private static String argSearchText(String[] args) {
        if (args == null || args.length < ARG_INDEX_SEARCH_TEXT_START + 1) {
            return null;
        }
        final var stringBuilder = new StringBuilder();
        for (var index = ARG_INDEX_SEARCH_TEXT_START; index < args.length; ++index) {
            stringBuilder.append(args[index]);
            if (index < args.length - 1) {
                stringBuilder.append(' ');
            }
        }
        return stringBuilder.toString();
    }
}
