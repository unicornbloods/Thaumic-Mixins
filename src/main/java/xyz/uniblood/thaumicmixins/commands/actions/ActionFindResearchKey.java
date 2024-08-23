package xyz.uniblood.thaumicmixins.commands.actions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import thaumcraft.api.research.ResearchCategories;

public class ActionFindResearchKey extends CommandAction
{
    public static final String USAGE_KEY = "commands.tmixins.findresearchkey.usage";
    public static final String CATEGORY_HEADER_KEY = "commands.tmixins.findresearchkey.category";
    public static final String NONE_FOUND_KEY = "commands.tmixins.findresearchkey.nonefound";

    public static final int ARG_INDEX_SEARCH_TEXT_START = 1;

    public ActionFindResearchKey(CommandBase owningCommand) {
        super(owningCommand);
    }

    @Override
    public String getName()
    {
        return "findResearchKey";
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
        final var searchText = argSearchText(args);
        final var searchTextUpper = searchText != null ? searchText.toUpperCase() : null;
        final var listMessageContents = new StringBuilder();

        var foundAny = false;

        final var categories = ResearchCategories.researchCategories;
        for (var categoryKey : categories.keySet()) {
            final var category = categories.get(categoryKey);
            var research = category.research
                .values()
                .stream();
            if (searchTextUpper != null) {
                research = research.filter(r -> r.getName().toUpperCase().contains(searchTextUpper));
            }
            final var iterator = research.iterator();
            if (!iterator.hasNext()) {
                continue;
            }
            foundAny = true;
            listMessageContents.delete(0, listMessageContents.length());
            while (iterator.hasNext()) {
                final var item = iterator.next();
                listMessageContents.append('\'').append(item.getName()).append("' (").append(item.key).append(')');

                if (iterator.hasNext()) {
                    listMessageContents.append(", ");
                }
            }
            sendColoredMessage(EnumChatFormatting.DARK_PURPLE, sender, CATEGORY_HEADER_KEY, ResearchCategories.getCategoryName(categoryKey));
            sender.addChatMessage(new ChatComponentText(listMessageContents.toString()));
        }

        if (!foundAny) {
            sendErrorMessage(sender, NONE_FOUND_KEY, searchText);
        }
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
