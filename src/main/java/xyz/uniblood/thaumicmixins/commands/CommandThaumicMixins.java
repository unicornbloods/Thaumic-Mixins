package xyz.uniblood.thaumicmixins.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import xyz.uniblood.thaumicmixins.commands.actions.ActionFindResearchKey;
import xyz.uniblood.thaumicmixins.commands.actions.ActionForgetResearch;
import xyz.uniblood.thaumicmixins.commands.actions.ActionForgetScanned;
import xyz.uniblood.thaumicmixins.commands.actions.ActionListResearch;
import xyz.uniblood.thaumicmixins.commands.actions.ICommandAction;
import xyz.uniblood.thaumicmixins.config.ThaumicMixinsConfig;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CommandThaumicMixins extends CommandBase
{
    private static final String USAGE_KEY = "commands.tmixins.usage";

    private final ICommandAction[] actions;

    public CommandThaumicMixins() {
        final var actions = new LinkedList<ICommandAction>();
        if (ThaumicMixinsConfig.enableFindResearch) {
            actions.add(new ActionFindResearchKey());
        }
        if (ThaumicMixinsConfig.enableForgetResearch) {
            actions.add(new ActionForgetResearch());
        }
        if (ThaumicMixinsConfig.enableForgetScanned) {
            actions.add(new ActionForgetScanned());
        }
        if (ThaumicMixinsConfig.enableListResearch) {
            actions.add(new ActionListResearch());
        }
        this.actions = actions.toArray(new ICommandAction[0]);
    }

    @Override
    public String getCommandName()
    {
        return "tmixins";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return ThaumicMixinsConfig.commandPermissionLevel;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return USAGE_KEY;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1) {
            throw new WrongUsageException(USAGE_KEY);
        }
        final var action = this.getActionByName(args[0]);
        if (action == null) {
            throw new WrongUsageException(USAGE_KEY);
        }
        action.process(sender, args);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length <= 1) {
            return getListOfStringsMatchingLastWord(args, Arrays.stream(actions).map(ICommandAction::getName).toArray(String[]::new));
        }
        final var action = this.getActionByName(args[0]);
        if (action == null) {
            throw new WrongUsageException(USAGE_KEY);
        }
        return action.addTabCompletionOptions(sender, args);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        if (index == 0 || args.length <= 1) {
            return false;
        }
        final var action = this.getActionByName(args[0]);
        return action != null && action.isUsernameIndex(args, index);
    }

    private ICommandAction getActionByName(String name) {
        for (var action : this.actions) {
            if (action.getName().equalsIgnoreCase(name)) {
                return action;
            }
        }
        return null;
    }
}
