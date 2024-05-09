package tobinio.darksorcery.items;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tobinio.darksorcery.DarkSorcery;
import tobinio.darksorcery.blocks.ModBlocks;
import tobinio.darksorcery.blocks.bloodfunnel.BloodFunnelEntity;

import java.util.logging.Logger;

/**
 * Created: 20.04.24
 *
 * @author Tobias Frischmann
 */
public class BloodBlade extends SwordItem {

    public BloodBlade(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = target.getWorld();

        //multi block pos
        BlockPos blockBelow = target.getBlockPos();

        if (target.isDead() && world.getBlockState(blockBelow).getBlock() == ModBlocks.BLOOD_FUNNEL) {
            var block = world.getBlockEntity(blockBelow);

            if (block instanceof BloodFunnelEntity bloodFunnelEntity) {
                try (Transaction transaction = Transaction.openOuter()) {
                    long insert = bloodFunnelEntity.storage.insert(FluidVariant.of(Fluids.WATER), FluidConstants.BOTTLE, transaction);

                    if (insert == FluidConstants.BOTTLE) {
                        transaction.commit();
                    }
                }
            }
        }

        return false;
    }
}
