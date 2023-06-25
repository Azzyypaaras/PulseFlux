package works.azzyys.pulseflux.util.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;

public interface StorageProvider<T extends TransferVariant<?>> {

    abstract Storage<T> get();
}
