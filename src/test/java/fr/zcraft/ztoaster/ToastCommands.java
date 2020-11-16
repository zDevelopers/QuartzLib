package fr.zcraft.ztoaster;

import fr.zcraft.quartzlib.components.commands.attributes.Sender;
import fr.zcraft.quartzlib.components.gui.Gui;
import fr.zcraft.quartzlib.components.i18n.I;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ToastCommands {

    // public ToastSubCommand subCommand = new ToastSubCommand(); // ...

    public void add(@Sender Player cook) {
        Toast toast = ToasterWorker.addToast(cook);
        cook.sendMessage(I.t("Toast {0} added.", toast.getToastId()));
    }

    public void add(@Sender Player cook, int toastCount) {
        for(int i = toastCount; i --> 0;)
        {
            ToasterWorker.addToast(cook);
        }

        cook.sendMessage(I.tn("One toast added.", "{0} toasts added.", toastCount, toastCount));
    }

    public void list(@Sender CommandSender sender) {
        showToasts(sender, Arrays.asList(Toaster.getToasts()));
    }

    public void list(@Sender CommandSender sender, Toast.CookingStatus cookingStatus) {
        ArrayList<Toast> toasts = new ArrayList<Toast>();

        for(Toast toast : Toaster.getToasts())
        {
            if(toast.getStatus().equals(cookingStatus))
                toasts.add(toast);
        }

        showToasts(sender, toasts);
    }

    private void showToasts(CommandSender sender, Collection<Toast> toasts)
    {
        if(toasts.isEmpty())
        {
            // Output of the command /toaster list, without toasts.
            sender.sendMessage("§7" + I.t("There are no toasts here ..."));
        }

        for(Toast toast : toasts)
        {
            sender.sendMessage(I.t("  Toast #{0}", toast.getToastId()));
        }
    }

    public void open(@Sender Player player) {
        Gui.open(player, new ToastExplorer());
    }
}
