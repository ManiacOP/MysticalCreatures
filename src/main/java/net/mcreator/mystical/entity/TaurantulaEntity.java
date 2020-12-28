
package net.mcreator.mystical.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.network.IPacket;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.block.BlockState;

import net.mcreator.mystical.MysticalModElements;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;

@MysticalModElements.ModElement.Tag
public class TaurantulaEntity extends MysticalModElements.ModElement {
	public static EntityType entity = null;
	public TaurantulaEntity(MysticalModElements instance) {
		super(instance, 1);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@Override
	public void initElements() {
		entity = (EntityType.Builder.<CustomEntity>create(CustomEntity::new, EntityClassification.MONSTER).setShouldReceiveVelocityUpdates(true)
				.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(CustomEntity::new).size(0.6f, 1.8f)).build("taurantula")
						.setRegistryName("taurantula");
		elements.entities.add(() -> entity);
		elements.items.add(
				() -> new SpawnEggItem(entity, -13434829, -10092442, new Item.Properties().group(ItemGroup.COMBAT)).setRegistryName("taurantula"));
	}

	@Override
	public void init(FMLCommonSetupEvent event) {
		for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
			biome.getSpawns(EntityClassification.MONSTER).add(new Biome.SpawnListEntry(entity, 20, 9, 9));
		}
		EntitySpawnPlacementRegistry.register(entity, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
				MonsterEntity::canMonsterSpawn);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> {
			return new MobRenderer(renderManager, new Modelcustom_model(), 0.5f) {
				@Override
				public ResourceLocation getEntityTexture(Entity entity) {
					return new ResourceLocation("mystical:textures/heheheheheehehehehehehhe.png");
				}
			};
		});
	}
	public static class CustomEntity extends MonsterEntity {
		public CustomEntity(FMLPlayMessages.SpawnEntity packet, World world) {
			this(entity, world);
		}

		public CustomEntity(EntityType<CustomEntity> type, World world) {
			super(type, world);
			experienceValue = 25;
			setNoAI(false);
		}

		@Override
		public IPacket<?> createSpawnPacket() {
			return NetworkHooks.getEntitySpawningPacket(this);
		}

		@Override
		protected void registerGoals() {
			super.registerGoals();
			this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));
			this.goalSelector.addGoal(2, new RandomWalkingGoal(this, 1));
			this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
			this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
			this.goalSelector.addGoal(5, new SwimGoal(this));
			this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, AgeableEntity.class, true, true));
			this.goalSelector.addGoal(7, new MeleeAttackGoal(this, 1.2, true));
		}

		@Override
		public CreatureAttribute getCreatureAttribute() {
			return CreatureAttribute.UNDEFINED;
		}

		protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
			super.dropSpecialItems(source, looting, recentlyHitIn);
			this.entityDropItem(new ItemStack(Items.SPIDER_EYE, (int) (1)));
		}

		@Override
		public void playStepSound(BlockPos pos, BlockState blockIn) {
			this.playSound((net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.spider.step")), 0.15f,
					1);
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.spider.hurt"));
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.spider.death"));
		}

		@Override
		protected void registerAttributes() {
			super.registerAttributes();
			if (this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null)
				this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6);
			if (this.getAttribute(SharedMonsterAttributes.MAX_HEALTH) != null)
				this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(45);
			if (this.getAttribute(SharedMonsterAttributes.ARMOR) != null)
				this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0);
			if (this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) == null)
				this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5);
		}
	}

	// Made with Blockbench 3.7.4
	// Exported for Minecraft version 1.15
	// Paste this class into your mod and generate all required imports
	public static class Modelcustom_model extends EntityModel<Entity> {
		private final ModelRenderer bb_main;
		public Modelcustom_model() {
			textureWidth = 32;
			textureHeight = 32;
			bb_main = new ModelRenderer(this);
			bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
			bb_main.setTextureOffset(0, 3).addBox(5.0F, -3.0F, 4.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(4.0F, -3.0F, 4.0F, 1.0F, -2.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(5.0F, -3.0F, 1.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(5.0F, -3.0F, -2.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(4.0F, -3.0F, 1.0F, 1.0F, -2.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(4.0F, -3.0F, -2.0F, 1.0F, -2.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(5.0F, -3.0F, -5.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(4.0F, -3.0F, -5.0F, 1.0F, -2.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(-7.0F, -3.0F, -5.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(-7.0F, -3.0F, -2.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(-7.0F, -3.0F, 1.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(-7.0F, -3.0F, 4.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(-6.0F, -3.0F, -5.0F, 1.0F, -2.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(-6.0F, -3.0F, -2.0F, 1.0F, -2.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(-6.0F, -3.0F, 1.0F, 1.0F, -2.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(-6.0F, -3.0F, 4.0F, 1.0F, -2.0F, 1.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(-5.0F, -4.0F, -5.0F, 9.0F, -1.0F, 10.0F, 0.0F, false);
			bb_main.setTextureOffset(0, 0).addBox(-4.0F, -5.0F, -4.0F, 7.0F, -1.0F, 8.0F, 0.0F, false);
		}

		@Override
		public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			// previously the render function, render code was moved to a method below
		}

		@Override
		public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue,
				float alpha) {
			bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}
