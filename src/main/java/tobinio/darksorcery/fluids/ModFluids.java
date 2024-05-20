package tobinio.darksorcery.fluids;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import tobinio.darksorcery.DarkSorcery;

/**
 * Created: 18.05.24
 *
 * @author Tobias Frischmann
 */
public class ModFluids {

    public static FlowableFluid BLOOD = Registry.register(Registries.FLUID, new Identifier(DarkSorcery.MOD_ID, "blood"), new BloodFluid.Still());
    public static FlowableFluid FLOWING_BLOOD = Registry.register(Registries.FLUID, new Identifier(DarkSorcery.MOD_ID, "flowing_blood"), new BloodFluid.Flowing());

    public static void initialize() {
    }
}
