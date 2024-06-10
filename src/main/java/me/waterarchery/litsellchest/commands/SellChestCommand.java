package me.waterarchery.litsellchest.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import me.waterarchery.litlibs.LitLibs;
import me.waterarchery.litsellchest.LitSellChest;
import me.waterarchery.litsellchest.configuration.gui.DefaultMenu;
import me.waterarchery.litsellchest.handlers.ChestHandler;
import me.waterarchery.litsellchest.handlers.ConfigHandler;
import me.waterarchery.litsellchest.handlers.GUIHandler;
import me.waterarchery.litsellchest.handlers.SoundManager;
import me.waterarchery.litsellchest.models.SellChestType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

@Command(value = "litsellchest", alias = {"sellchest", "lsc"})
public class SellChestCommand extends BaseCommand {

    @Default
    public void defaultCmd(CommandSender sender) {
        if (sender instanceof Player) {
            GUIHandler guiHandler = GUIHandler.getInstance();
            DefaultMenu defaultMenu = guiHandler.getDefaultMenu();
            ((Player) sender).openInventory(defaultMenu.generateInventory((Player) sender));
            SoundManager.sendSound((Player) sender, "DefaultMenuOpened");
        }
        else {
            LitLibs libs = LitSellChest.getInstance().getLibs();
            libs.getLogger().log("You can only use this command on in game.");
        }
    }

    @SubCommand("shop")
    public void shop(CommandSender sender) {
        if (sender instanceof Player) {
            GUIHandler guiHandler = GUIHandler.getInstance();
            guiHandler.openShop((Player) sender);
        }
        else {
            LitLibs libs = LitSellChest.getInstance().getLibs();
            libs.getLogger().log("You can only use this command on in game.");
        }
    }

    @Permission("litsellchest.admin.give")
    @SubCommand("give")
    public void giveCommand(CommandSender sender, Player target,
                            @ArgName("chestType") @Suggestion("chest-types") String rawType,
                            @ArgName("amount") @Optional Integer amount) {
        ChestHandler chestHandler = ChestHandler.getInstance();
        SellChestType chestType = chestHandler.getType(rawType);
        ConfigHandler configHandler = ConfigHandler.getInstance();

        if (chestType != null) {
            chestHandler.giveChest(target, chestType, Objects.requireNonNullElse(amount, 1));
            String senderMes = configHandler.getMessageLang("ChestGaveAdmin")
                    .replace("%player%", target.getName())
                    .replace("%name%", chestType.getName());
            String targetMes = configHandler.getMessageLang("ChestGaveTarget").replace("%name%", chestType.getName());
            sender.sendMessage(senderMes);
            if (!sender.equals(target))
                target.sendMessage(targetMes);
        }
        else {
            configHandler.sendMessageLang(target, "NoChestWithType");
        }
    }

}
