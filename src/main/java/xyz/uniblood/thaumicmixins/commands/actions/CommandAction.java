package xyz.uniblood.thaumicmixins.commands.actions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import thaumcraft.api.aspects.Aspect;

import java.util.Collections;
import java.util.List;

public abstract class CommandAction implements ICommandAction
{
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

    protected static Vec3 parseCoordinates(ICommandSender sender, String xStr, String yStr, String zStr) {
        final var playerCoords = sender.getPlayerCoordinates();
        var x = playerCoords.posX;
        var y = playerCoords.posY;
        var z = playerCoords.posZ;
        x = MathHelper.floor_double(CommandBase.func_110666_a(sender, x, xStr));
        y = MathHelper.floor_double(CommandBase.func_110666_a(sender, y, yStr));
        z = MathHelper.floor_double(CommandBase.func_110666_a(sender, z, zStr));
        return Vec3.createVectorHelper(x, y, z);
    }

    protected static Integer tryParseInt(String[] args, int atIndex) {
        if (atIndex >= args.length) {
            return null;
        }
        try {
            return Integer.parseInt(args[atIndex]);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    protected static Aspect parseAspect(String tag) {
        var aspect = Aspect.getAspect(tag);
        if (aspect != null) {
            return aspect;
        }
        for (var iAspect : Aspect.aspects.values()) {
            if (iAspect.getTag().equalsIgnoreCase(tag))
            {
                return iAspect;
            }
        }
        return null;
    }

    protected static <E extends Enum<E>> E tryParseEnum(Class<E> clazz, String arg) {
        try {
            return Enum.valueOf(clazz, arg);
        }
        catch (IllegalArgumentException iae) {
            return null;
        }
    }
}
