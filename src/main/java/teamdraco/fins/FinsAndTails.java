package teamdraco.fins;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teamdraco.fins.client.ClientEvents;
import teamdraco.fins.common.entities.*;
import teamdraco.fins.common.items.charms.SpindlyGemCharm;
import teamdraco.fins.init.*;
import teamdraco.fins.network.INetworkPacket;
import teamdraco.fins.network.TriggerFlyingPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Mod(FinsAndTails.MOD_ID)
public class FinsAndTails {
    public static final String MOD_ID = "fins";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final SimpleChannel NETWORK = INetworkPacket.makeChannel("network", "1");
    public static final List<Runnable> CALLBACKS = new ArrayList<>();
    private static int currentNetworkId;

    public FinsAndTails() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::registerClient);
        bus.addListener(this::registerCommon);
        bus.addListener(this::registerEntityAttributes);

        FinsEnchantments.REGISTER.register(bus);
        FinsItems.REGISTER.register(bus);
        FinsBlocks.REGISTER.register(bus);
        FinsContainers.REGISTER.register(bus);
        FinsEntities.REGISTER.register(bus);
        FinsSounds.REGISTER.register(bus);
        FinsRecipes.SERIALIZERS.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FinsConfig.Common.SPEC);
        registerMessage(TriggerFlyingPacket.class, TriggerFlyingPacket::new, LogicalSide.SERVER);
    }

    private void registerCommon(FMLCommonSetupEvent event) {
        EntitySpawnPlacementRegistry.register(FinsEntities.BLU_WEE.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.PEA_WEE.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.BANDED_REDBACK_SHRIMP.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.SWAMP_MUCKER.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.TEAL_ARROWFISH.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.FLATBACK_SUCKER.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.HIGH_FINNED_BLUE.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.MUDHORSE.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::checkAnimalSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.PHANTOM_NUDIBRANCH.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.ORNATE_BUGFISH.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.PENGLIL.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PenglilEntity::canPenglilSpawn);
        EntitySpawnPlacementRegistry.register(FinsEntities.SPINDLY_GEM_CRAB.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.FLATBACK_LEAF_SNAIL.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::checkAnimalSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.RED_BULL_CRAB.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RedBullCrabEntity::canCrabSpawn);
        EntitySpawnPlacementRegistry.register(FinsEntities.WHITE_BULL_CRAB.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RedBullCrabEntity::canCrabSpawn);
        EntitySpawnPlacementRegistry.register(FinsEntities.WEE_WEE.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.VIBRA_WEE.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.RIVER_PEBBLE_SNAIL.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::checkAnimalSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.SIDEROL_WHISKERED_SNAIL.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::checkAnimalSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.GOLDEN_RIVER_RAY.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.NIGHT_LIGHT_SQUID.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, NightLightSquidEntity::checkSquidSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.GOPJET.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.PAPA_WEE.get(), EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
        EntitySpawnPlacementRegistry.register(FinsEntities.WHERBLE.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::checkAnimalSpawnRules);

        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)), Ingredient.of(FinsItems.NIGHT_LIGHT_SQUID.get()), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.NIGHT_VISION));
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(FinsEntities.BANDED_REDBACK_SHRIMP.get(), BandedRedbackShrimpEntity.createAttributes().build());
        event.put(FinsEntities.BLU_WEE.get(), BluWeeEntity.createAttributes().build());
        event.put(FinsEntities.FLATBACK_SUCKER.get(), FlatbackSuckerEntity.createAttributes().build());
        event.put(FinsEntities.HIGH_FINNED_BLUE.get(), HighFinnedBlueEntity.createAttributes().build());
        event.put(FinsEntities.MUDHORSE.get(), MudhorseEntity.createAttributes().build());
        event.put(FinsEntities.ORNATE_BUGFISH.get(), OrnateBugfishEntity.createAttributes().build());
        event.put(FinsEntities.PEA_WEE.get(), PeaWeeEntity.createAttributes().build());
        event.put(FinsEntities.PENGLIL.get(), PenglilEntity.createAttributes().build());
        event.put(FinsEntities.PHANTOM_NUDIBRANCH.get(), PhantomNudibranchEntity.createAttributes().build());
        event.put(FinsEntities.SPINDLY_GEM_CRAB.get(), SpindlyGemCrabEntity.createAttributes().build());
        event.put(FinsEntities.SWAMP_MUCKER.get(), SwampMuckerEntity.createAttributes().build());
        event.put(FinsEntities.TEAL_ARROWFISH.get(), TealArrowfishEntity.createAttributes().build());
        event.put(FinsEntities.FLATBACK_LEAF_SNAIL.get(), FlatbackLeafSnailEntity.createAttributes().build());
        event.put(FinsEntities.RUBBER_BELLY_GLIDER.get(), RubberBellyGliderEntity.registerRBGAttributes().build());
        event.put(FinsEntities.RED_BULL_CRAB.get(), RedBullCrabEntity.createAttributes().build());
        event.put(FinsEntities.WHITE_BULL_CRAB.get(), WhiteBullCrabEntity.createAttributes().build());
        event.put(FinsEntities.WEE_WEE.get(), AbstractFishEntity.createAttributes().build());
        event.put(FinsEntities.VIBRA_WEE.get(), AbstractFishEntity.createAttributes().build());
        event.put(FinsEntities.GOPJET.get(), GopjetEntity.createAttributes().build());
        event.put(FinsEntities.RIVER_PEBBLE_SNAIL.get(), RiverPebbleSnailEntity.createAttributes().build());
        event.put(FinsEntities.SIDEROL_WHISKERED_SNAIL.get(), SiderolWhiskeredSnailEntity.createAttributes().build());
        event.put(FinsEntities.GOLDEN_RIVER_RAY.get(), GoldenRiverRayEntity.createAttributes().build());
        event.put(FinsEntities.NIGHT_LIGHT_SQUID.get(), NightLightSquidEntity.createAttributes().build());
        event.put(FinsEntities.PAPA_WEE.get(), PapaWeeEntity.createAttributes().build());
        event.put(FinsEntities.WHERBLE.get(), WherbleEntity.createAttributes().build());
    }

    private void registerClient(FMLClientSetupEvent event) {
        ClientEvents.init();
        CALLBACKS.forEach(Runnable::run);
        CALLBACKS.clear();

        ItemModelsProperties.register(FinsItems.GEM_CRAB_AMULET.get(), new ResourceLocation(FinsAndTails.MOD_ID, "broken"), (stack, world, player) -> {
            return SpindlyGemCharm.isUsable(stack) ? 0.0F : 1.0F;
        });
    }

    private <T extends INetworkPacket> void registerMessage(Class<T> message, Function<PacketBuffer, T> reader, LogicalSide side) {
        NETWORK.registerMessage(currentNetworkId++, message, INetworkPacket::write, reader, (msg, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> msg.handle(context.getDirection().getOriginationSide().isServer() ? getClientPlayer() : context.getSender()));
            context.setPacketHandled(true);
        }, Optional.of(side.isClient() ? NetworkDirection.PLAY_TO_CLIENT : NetworkDirection.PLAY_TO_SERVER));
    }

    @OnlyIn(Dist.CLIENT)
    private static PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public final static ItemGroup GROUP = new ItemGroup(MOD_ID) {

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(FinsItems.BLU_WEE.get());
        }

        /* @Override
        public void fill(NonNullList<ItemStack> items) {
            for (int i = 0; i <= 4; i++) {
                ItemStack stack = new ItemStack(FinsItems.SPINDLY_GEM_CRAB.get());
                CompoundNBT tag = stack.getOrCreateTag();
                tag.putInt("Crab", i);
                items.add(stack);
            }

            super.fill(items);
        }*/
    };
}
