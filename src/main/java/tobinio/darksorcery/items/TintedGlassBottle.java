package tobinio.darksorcery.items;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;

/**
 * Created: 20.04.24
 *
 * @author Tobias Frischmann
 */
public class TintedGlassBottle extends Item {
    public TintedGlassBottle(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        Storage<FluidVariant> storage = FluidStorage.SIDED.find(context.getWorld(), context.getBlockPos(), context.getSide());

        if (storage == null || context.getPlayer() == null) {
            return super.useOnBlock(context);
        }

        try (Transaction transaction = Transaction.openOuter()) {
            long extract = storage.extract(FluidVariant.of(Fluids.WATER), FluidConstants.BOTTLE, transaction);

            if (extract == FluidConstants.BOTTLE) {
                transaction.commit();
                context.getPlayer()
                        .setStackInHand(context.getHand(), ItemUsage.exchangeStack(context.getStack(), context.getPlayer(), new ItemStack(ModItems.BLOODY_TINTED_GLASS_BOTTLE)));
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.FAIL;
    }
}
