package xyz.uniblood.thaumicmixins.commands.actions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collections;
import java.util.List;

public abstract class CommandAction implements ICommandAction
{
    /**
     * The command this action belongs to.
     */
    protected final CommandBase owningCommand;

    /**
     * @param owningCommand The command this action belongs to.
     */
    public CommandAction(CommandBase owningCommand) {
        this.owningCommand = owningCommand;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return false;
    }

    protected static void sendErrorMessage(ICommandSender toPlayer, String translationKey, Object... args) {
        sendColoredMessage(EnumChatFormatting.RED, toPlayer, translationKey, args);
    }

    protected static void sendSuccessMessage(ICommandSender toPlayer, String translationKey, Object... args) {
        sendColoredMessage(EnumChatFormatting.GREEN, toPlayer, translationKey, args);
    }

    protected static void sendColoredMessage(EnumChatFormatting color, ICommandSender toPlayer, String translationKey, Object... args) {
        final var message = new ChatComponentTranslation(translationKey, args);
        message.getChatStyle().setColor(color);
        toPlayer.addChatMessage(message);
    }
}
