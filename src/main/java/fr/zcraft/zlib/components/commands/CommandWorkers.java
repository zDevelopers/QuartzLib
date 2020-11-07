package fr.zcraft.zlib.components.commands;

import fr.zcraft.zlib.components.i18n.I;
import fr.zcraft.zlib.components.worker.Worker;
import fr.zcraft.zlib.components.worker.WorkerAttributes;
import fr.zcraft.zlib.components.worker.WorkerCallback;
import fr.zcraft.zlib.components.worker.WorkerRunnable;
import fr.zcraft.zlib.tools.PluginLogger;
import fr.zcraft.zlib.tools.mojang.UUIDFetcher;

import java.util.UUID;
import java.util.function.Consumer;

@WorkerAttributes(name = "Command's worker", queriesMainThread = true)
public class CommandWorkers extends Worker{

    public void OfflineNameFetch(final String playerName, final Consumer<UUID> callback) {

        final WorkerCallback wCallback = new WorkerCallback<UUID>() {
            @Override
            public void finished(UUID result) {
                callback.accept(result);  // Si tout va bien on passe l'UUID au callback
            }
            @Override
            public void errored(Throwable exception) {
                PluginLogger.warning(I.t("Error while getting player UUID"));
                callback.accept(null);  // En cas d'erreur on envoie `null` au callback
            }
        };
        WorkerRunnable wr = new WorkerRunnable<UUID>() {
            @Override
            public UUID run() throws Throwable {
                return UUIDFetcher.fetch(playerName);
            }
        };
        submitQuery(wr, wCallback);
    }

}
