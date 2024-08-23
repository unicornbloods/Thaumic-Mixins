package xyz.uniblood.thaumicmixins.commands.actions;

import net.minecraft.command.ICommandSender;

import java.util.List;

/**
 * A sub-command or action that belongs to a command
 */
public interface ICommandAction
{
    /**
     * The command-unique name of the action.
     * @return The action's name.
     */
    String getName();

    /**
     * A lang key that explains how to use the action.
     * @return The lang key.
     */
    String getUsage();

    /**
     * Execute the action.
     * @param sender The command's executor.
     * @param args The command's arguments.
     */
    void process(ICommandSender sender, String[] args);

    /**
     * Provide auto-completion for the action's arguments.
     * @param sender The command's executor.
     * @param args The command's arguments.
     * @return A list of valid auto-completion values.
     */
    List<String> addTabCompletionOptions(ICommandSender sender, String[] args);

    /**
     * Whether the argument at the given index should be a username.
     * @param args The command's arguments.
     * @param index The index to check.
     * @return True if the argument at the given index should be a username, false otherwise.
     */
    boolean isUsernameIndex(String[] args, int index);
}
