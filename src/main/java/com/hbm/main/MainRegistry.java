package com.hbm.main;

import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.Logger;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.BombConfig;
import com.hbm.config.GeneralConfig;
import com.hbm.config.MachineConfig;
import com.hbm.config.MobConfig;
import com.hbm.config.PotionConfig;
import com.hbm.config.RadiationConfig;
import com.hbm.config.ToolConfig;
import com.hbm.config.WeaponConfig;
import com.hbm.config.WorldConfig;
import com.hbm.creativetabs.*;
import com.hbm.entity.HBMEntity;
import com.hbm.entity.effect.*;
import com.hbm.entity.grenade.*;
import com.hbm.entity.item.*;
import com.hbm.entity.logic.*;
import com.hbm.entity.missile.*;
import com.hbm.entity.mob.*;
import com.hbm.entity.mob.botprime.EntityBOTPrimeBody;
import com.hbm.entity.mob.botprime.EntityBOTPrimeHead;
import com.hbm.entity.particle.*;
import com.hbm.entity.projectile.*;
import com.hbm.handler.*;
import com.hbm.handler.FluidTypeHandler.FluidType;
import com.hbm.inventory.*;
import com.hbm.items.ModItems;
import com.hbm.lib.HbmWorld;
import com.hbm.lib.Library;
import com.hbm.lib.RefStrings;
import com.hbm.packet.PacketDispatcher;
import com.hbm.potion.HbmPotion;
import com.hbm.saveddata.satellites.Satellite;
import com.hbm.tileentity.HBMTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.TileEntityProxyEnergy;
import com.hbm.tileentity.TileEntityProxyInventory;
import com.hbm.tileentity.bomb.*;
import com.hbm.tileentity.conductor.*;
import com.hbm.tileentity.deco.*;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.TileEntityMachineReactorLarge.ReactorFuelType;
import com.hbm.tileentity.machine.rbmk.*;
import com.hbm.tileentity.turret.*;
import com.hbm.world.generator.CellularDungeonFactory;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = RefStrings.MODID, name = RefStrings.NAME, version = RefStrings.VERSION, acceptedMinecraftVersions = RefStrings.MINECRAFT_VERSION)
public class MainRegistry {
	@Instance(RefStrings.MODID)
	public static MainRegistry instance;

	@SidedProxy(clientSide = RefStrings.CLIENTSIDE, serverSide = RefStrings.SERVERSIDE)
	public static ServerProxy proxy;

	@Metadata
	public static ModMetadata meta;

	public static Logger logger;

	// Tool Materials
	public static ToolMaterial tMatSchrab = EnumHelper.addToolMaterial("SCHRABIDIUM", 3, 10000, 50.0F, 100.0F, 200);
	public static ToolMaterial tMatHammmer = EnumHelper.addToolMaterial("SCHRABIDIUMHAMMER", 3, 0, 50.0F, 999999996F, 200);
	public static ToolMaterial tMatChainsaw = EnumHelper.addToolMaterial("CHAINSAW", 3, 1500, 50.0F, 22.0F, 0);
	public static ToolMaterial tMatSteel = EnumHelper.addToolMaterial("HBM_STEEL", 2, 500, 7.5F, 2.0F, 10);
	public static ToolMaterial tMatTitan = EnumHelper.addToolMaterial("HBM_TITANIUM", 3, 750, 9.0F, 2.5F, 15);
	public static ToolMaterial tMatAlloy = EnumHelper.addToolMaterial("HBM_ALLOY", 3, 2000, 15.0F, 5.0F, 5);
	public static ToolMaterial tMatCMB = EnumHelper.addToolMaterial("HBM_CMB", 3, 8500, 40.0F, 55F, 100);
	public static ToolMaterial tMatElec = EnumHelper.addToolMaterial("HBM_ELEC", 3, 0, 30.0F, 12.0F, 2);
	public static ToolMaterial tMatDesh = EnumHelper.addToolMaterial("HBM_DESH", 2, 0, 7.5F, 2.0F, 10);
	public static ToolMaterial tMatCobalt = EnumHelper.addToolMaterial("HBM_COBALT", 3, 750, 9.0F, 2.5F, 15);

	public static ToolMaterial enumToolMaterialSaw = EnumHelper.addToolMaterial("SAW", 2, 750, 2.0F, 3.5F, 25);
	public static ToolMaterial enumToolMaterialBat = EnumHelper.addToolMaterial("BAT", 0, 500, 1.5F, 3F, 25);
	public static ToolMaterial enumToolMaterialBatNail = EnumHelper.addToolMaterial("BATNAIL", 0, 450, 1.0F, 4F, 25);
	public static ToolMaterial enumToolMaterialGolfClub = EnumHelper.addToolMaterial("GOLFCLUB", 1, 1000, 2.0F, 5F, 25);
	public static ToolMaterial enumToolMaterialPipeRusty = EnumHelper.addToolMaterial("PIPERUSTY", 1, 350, 1.5F, 4.5F, 25);
	public static ToolMaterial enumToolMaterialPipeLead = EnumHelper.addToolMaterial("PIPELEAD", 1, 250, 1.5F, 5.5F, 25);

	public static ToolMaterial enumToolMaterialBottleOpener = EnumHelper.addToolMaterial("OPENER", 1, 250, 1.5F, 0.5F, 200);
	public static ToolMaterial enumToolMaterialSledge = EnumHelper.addToolMaterial("SHIMMERSLEDGE", 1, 0, 25.0F, 26F, 200);

	public static ToolMaterial enumToolMaterialMultitool = EnumHelper.addToolMaterial("MULTITOOL", 3, 5000, 25F, 5.5F, 25);

	// Armor Materials
	public static ArmorMaterial enumArmorMaterialEmerald = EnumHelper.addArmorMaterial("HBM_TEST", 2500, new int[] { 3, 8, 6, 3 }, 30);
	public static ArmorMaterial aMatSchrab = EnumHelper.addArmorMaterial("HBM_SCHRABIDIUM", 100, new int[] { 3, 8, 6, 3 }, 50);
	public static ArmorMaterial aMatEuph = EnumHelper.addArmorMaterial("HBM_EUPHEMIUM", 15000000, new int[] { 3, 8, 6, 3 }, 100);
	public static ArmorMaterial aMatHaz = EnumHelper.addArmorMaterial("HBM_HAZMAT", 60, new int[] { 2, 5, 4, 1 }, 5);
	public static ArmorMaterial aMatHaz2 = EnumHelper.addArmorMaterial("HBM_HAZMAT2", 60, new int[] { 2, 5, 4, 1 }, 5);
	public static ArmorMaterial aMatHaz3 = EnumHelper.addArmorMaterial("HBM_HAZMAT3", 60, new int[] { 2, 5, 4, 1 }, 5);
	public static ArmorMaterial aMatSteel = EnumHelper.addArmorMaterial("HBM_STEEL", 20, new int[] { 2, 6, 5, 2 }, 5);
	public static ArmorMaterial aMatAsbestos = EnumHelper.addArmorMaterial("HBM_ASBESTOS", 20, new int[] { 1, 4, 3, 1 }, 5);
	public static ArmorMaterial aMatTitan = EnumHelper.addArmorMaterial("HBM_TITANIUM", 25, new int[] { 3, 8, 6, 3 }, 9);
	public static ArmorMaterial aMatAlloy = EnumHelper.addArmorMaterial("HBM_ALLOY", 40, new int[] { 3, 8, 6, 3 }, 12);
	public static ArmorMaterial aMatPaa = EnumHelper.addArmorMaterial("HBM_PAA", 75, new int[] { 3, 8, 6, 3 }, 25);
	public static ArmorMaterial aMatCMB = EnumHelper.addArmorMaterial("HBM_CMB", 60, new int[] { 3, 8, 6, 3 }, 50);
	public static ArmorMaterial aMatAus3 = EnumHelper.addArmorMaterial("HBM_AUSIII", 375, new int[] { 2, 6, 5, 2 }, 0);
	public static ArmorMaterial aMatSecurity = EnumHelper.addArmorMaterial("HBM_SECURITY", 100, new int[] { 3, 8, 6, 3 }, 15);
	public static ArmorMaterial aMatCobalt = EnumHelper.addArmorMaterial("HBM_COBALT", 70, new int[] { 3, 8, 6, 3 }, 25);
	public static ArmorMaterial aMatStarmetal = EnumHelper.addArmorMaterial("HBM_STARMETAL", 150, new int[] { 3, 8, 6, 3 }, 100);

	// Creative Tabs
	// ingots, nuggets, wires, machine parts
	public static CreativeTabs partsTab = new PartsTab(CreativeTabs.getNextID(), "tabParts");
	// items that belong in machines, fuels, etc
	public static CreativeTabs controlTab = new ControlTab(CreativeTabs.getNextID(), "tabControl");
	// templates, siren tracks
	public static CreativeTabs templateTab = new TemplateTab(CreativeTabs.getNextID(), "tabTemplate");
	// ore and mineral blocks
	public static CreativeTabs blockTab = new BlockTab(CreativeTabs.getNextID(), "tabBlocks");
	// machines, structure parts
	public static CreativeTabs machineTab = new MachineTab(CreativeTabs.getNextID(), "tabMachine");
	// bombs
	public static CreativeTabs nukeTab = new NukeTab(CreativeTabs.getNextID(), "tabNuke");
	// missiles, satellites
	public static CreativeTabs missileTab = new MissileTab(CreativeTabs.getNextID(), "tabMissile");
	// turrets, weapons, ammo
	public static CreativeTabs weaponTab = new WeaponTab(CreativeTabs.getNextID(), "tabWeapon");
	// drinks, kits, tools
	public static CreativeTabs consumableTab = new ConsumableTab(CreativeTabs.getNextID(), "tabConsumable");

	// Achievements
	public static Achievement achSacrifice;
	public static Achievement achImpossible;
	public static Achievement achTOB;
	public static Achievement achFreytag;
	public static Achievement achSelenium;
	public static Achievement achPotato;
	public static Achievement achC44;
	public static Achievement achC20_5;
	public static Achievement achSpace;
	public static Achievement achFOEQ;
	public static Achievement achFiend;
	public static Achievement achFiend2;
	public static Achievement achSoyuz;
	public static Achievement achRadPoison;
	public static Achievement achRadDeath;
	public static Achievement achStratum;
	public static Achievement achMeltdown;
	public static Achievement achOmega12;
	public static Achievement bobMetalworks;
	public static Achievement bobAssembly;
	public static Achievement bobChemistry;
	public static Achievement bobOil;
	public static Achievement bobNuclear;
	public static Achievement bobHidden;
	public static Achievement horizonsStart;
	public static Achievement horizonsEnd;
	public static Achievement horizonsBonus;
	public static Achievement bossCreeper;
	public static Achievement bossMeltdown;
	public static Achievement bossMaskman;
	public static Achievement bossWorm;
	public static Achievement digammaSee;
	public static Achievement digammaFeel;
	public static Achievement digammaKnow;
	public static Achievement digammaKauaiMoho;
	public static Achievement digammaUpOnTop;

	public static int generalOverride = 0;
	public static int polaroidID = 1;

	public static int x;
	public static int y;
	public static int z;
	public static long time;

	Random rand = new Random();

	@EventHandler
	public void PreLoad(FMLPreInitializationEvent PreEvent) {
		if(logger == null)
			logger = PreEvent.getModLog();

		// Reroll Polaroid

		if(generalOverride > 0 && generalOverride < 19) {
			polaroidID = generalOverride;
		} else {
			polaroidID = rand.nextInt(18) + 1;
			while(polaroidID == 4 || polaroidID == 9)
				polaroidID = rand.nextInt(18) + 1;
		}

		loadConfig(PreEvent);
		HbmPotion.init();
		
		ModBlocks.mainRegistry();
		ModItems.mainRegistry();
		proxy.registerRenderInfo();
		HbmWorld.mainRegistry();
		GameRegistry.registerFuelHandler(new FuelHandler());
		BulletConfigSyncingUtil.loadConfigsForSync();
		CellularDungeonFactory.init();
		Satellite.register();
		HTTPHandler.loadStats();
		CraftingManager.mainRegistry();
		AssemblerRecipes.preInit(PreEvent.getModConfigurationDirectory());

		Library.superuser.add("192af5d7-ed0f-48d8-bd89-9d41af8524f8");
		Library.superuser.add("5aee1e3d-3767-4987-a222-e7ce1fbdf88e");
		Library.superuser.add("937c9804-e11f-4ad2-a5b1-42e62ac73077");
		Library.superuser.add("3af1c262-61c0-4b12-a4cb-424cc3a9c8c0");
		Library.superuser.add("4729b498-a81c-42fd-8acd-20d6d9f759e0");
		Library.superuser.add("c3f5e449-6d8c-4fe3-acc9-47ef50e7e7ae");

		aMatSchrab.customCraftingMaterial = ModItems.ingot_schrabidium;
		aMatHaz.customCraftingMaterial = ModItems.hazmat_cloth;
		aMatHaz2.customCraftingMaterial = ModItems.hazmat_cloth_red;
		aMatHaz3.customCraftingMaterial = ModItems.hazmat_cloth_grey;
		aMatTitan.customCraftingMaterial = ModItems.ingot_titanium;
		aMatSteel.customCraftingMaterial = ModItems.ingot_steel;
		aMatAsbestos.customCraftingMaterial = ModItems.asbestos_cloth;
		aMatAlloy.customCraftingMaterial = ModItems.ingot_advanced_alloy;
		aMatPaa.customCraftingMaterial = ModItems.plate_paa;
		aMatCMB.customCraftingMaterial = ModItems.ingot_combine_steel;
		aMatAus3.customCraftingMaterial = ModItems.ingot_australium;
		aMatSecurity.customCraftingMaterial = ModItems.plate_kevlar;
		aMatCobalt.customCraftingMaterial = ModItems.ingot_cobalt;
		aMatStarmetal.customCraftingMaterial = ModItems.ingot_starmetal;
		tMatSchrab.setRepairItem(new ItemStack(ModItems.ingot_schrabidium));
		tMatHammmer.setRepairItem(new ItemStack(Item.getItemFromBlock(ModBlocks.block_schrabidium)));
		tMatChainsaw.setRepairItem(new ItemStack(ModItems.ingot_steel));
		tMatTitan.setRepairItem(new ItemStack(ModItems.ingot_titanium));
		tMatSteel.setRepairItem(new ItemStack(ModItems.ingot_steel));
		tMatAlloy.setRepairItem(new ItemStack(ModItems.ingot_advanced_alloy));
		tMatCMB.setRepairItem(new ItemStack(ModItems.ingot_combine_steel));
		enumToolMaterialBottleOpener.setRepairItem(new ItemStack(ModItems.plate_steel));
		tMatDesh.setRepairItem(new ItemStack(ModItems.ingot_desh));

		ChestGenHooks.addItem(ChestGenHooks.VILLAGE_BLACKSMITH, new WeightedRandomChestContent(new ItemStack(ModItems.armor_polish), 1, 1, 3));
		ChestGenHooks.addItem(ChestGenHooks.VILLAGE_BLACKSMITH, new WeightedRandomChestContent(new ItemStack(ModItems.bathwater), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(ModItems.bathwater), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(ModItems.serum), 1, 1, 5));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(ModItems.heart_piece), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(new ItemStack(ModItems.heart_piece), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(new ItemStack(ModItems.heart_piece), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(ModItems.scrumpy), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(new ItemStack(ModItems.scrumpy), 1, 1, 1));

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GUIHandler());
		HBMTileEntity.preInitialization();

		HBMEntity.preInitialization();

		ForgeChunkManager.setForcedChunkLoadingCallback(this, new LoadingCallback() {

			@Override
			public void ticketsLoaded(List<Ticket> tickets, World world) {
				for(Ticket ticket : tickets) {

					if(ticket.getEntity() instanceof IChunkLoader) {
						((IChunkLoader) ticket.getEntity()).init(ticket);
					}
				}
			}
		});

		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_generic, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeGeneric(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_strong, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeStrong(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_frag, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeFrag(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_fire, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeFire(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_cluster, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeCluster(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_flare, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeFlare(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_electric, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeElectric(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_poison, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadePoison(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_gas, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeGas(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_schrabidium, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeSchrabidium(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_nuke, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeNuke(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_nuclear, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeNuclear(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_pulse, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadePulse(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_plasma, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadePlasma(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_tau, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeTau(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_lemon, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeLemon(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_mk2, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeMk2(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_aschrab, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeASchrab(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_zomg, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeZOMG(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_shrapnel, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeShrapnel(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_black_hole, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeBlackHole(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_gascan, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeGascan(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_cloud, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeCloud(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_pink_cloud, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadePC(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_smart, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeSmart(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_mirv, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeMIRV(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_breach, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeBreach(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_burst, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeBurst(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_generic, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFGeneric(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_he, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFHE(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_bouncy, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFBouncy(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_sticky, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFSticky(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_impact, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFImpact(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_incendiary, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFIncendiary(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_toxic, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFToxic(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_concussion, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFConcussion(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_brimstone, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFBrimstone(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_mystery, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFMystery(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_spark, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFSpark(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_hopwire, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFHopwire(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.grenade_if_null, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityGrenadeIFNull(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(ModItems.nuclear_waste_pearl, new BehaviorProjectileDispense() {

			protected IProjectile getProjectileEntity(World world, IPosition position) {
				return new EntityWastePearl(world, position.getX(), position.getY(), position.getZ());
			}
		});
	}

	@EventHandler
	public static void load(FMLInitializationEvent event) {

		HBMTileEntity.Initialization();
		HBMEntity.Initialization();
		achSacrifice = new Achievement("achievement.sacrifice", "sacrifice", 0, -2, ModItems.burnt_bark, null).initIndependentStat().setSpecial().registerStat();
		achImpossible = new Achievement("achievement.impossible", "impossible", 2, -2, ModItems.nothing, null).initIndependentStat().setSpecial().registerStat();
		achTOB = new Achievement("achievement.tasteofblood", "tasteofblood", 0, 0, new ItemStack(ModItems.fluid_icon, 1, FluidType.ASCHRAB.getID()), null).initIndependentStat().setSpecial().registerStat();
		achFreytag = new Achievement("achievement.freytag", "freytag", 0, -4, ModItems.gun_mp40, null).initIndependentStat().setSpecial().registerStat();
		achSelenium = new Achievement("achievement.selenium", "selenium", -2, -4, ModItems.ingot_starmetal, null).initIndependentStat().setSpecial().registerStat();
		achPotato = new Achievement("achievement.potato", "potato", -2, -2, ModItems.battery_potatos, null).initIndependentStat().setSpecial().registerStat();
		achC44 = new Achievement("achievement.c44", "c44", 2, -4, ModItems.gun_revolver_pip, null).initIndependentStat().setSpecial().registerStat();
		achC20_5 = new Achievement("achievement.c20_5", "c20_5", 4, -4, ModItems.gun_dampfmaschine, null).initIndependentStat().setSpecial().registerStat();
		achSpace = new Achievement("achievement.space", "space", 4, -2, ModItems.missile_carrier, null).initIndependentStat().setSpecial().registerStat();
		achFOEQ = new Achievement("achievement.FOEQ", "FOEQ", 4, 0, ModItems.sat_foeq, null).initIndependentStat().setSpecial().registerStat();
		achFiend = new Achievement("achievement.fiend", "fiend", 6, -2, ModItems.shimmer_sledge, null).initIndependentStat().setSpecial().registerStat();
		achFiend2 = new Achievement("achievement.fiend2", "fiend2", 6, 0, ModItems.shimmer_axe, null).initIndependentStat().setSpecial().registerStat();
		achSoyuz = new Achievement("achievement.soyuz", "soyuz", -2, 0, Items.baked_potato, null).initIndependentStat().setSpecial().registerStat();
		achStratum = new Achievement("achievement.stratum", "stratum", -4, -2, new ItemStack(ModBlocks.stone_gneiss), null).initIndependentStat().setSpecial().registerStat();
		achMeltdown = new Achievement("achievement.meltdown", "meltdown", -4, 0, new ItemStack(ModBlocks.iter), null).initIndependentStat().setSpecial().registerStat();
		achOmega12 = new Achievement("achievement.omega12", "omega12", -4, 2, ModItems.particle_digamma, null).initIndependentStat().setSpecial().registerStat();

		bobMetalworks = new Achievement("achievement.metalworks", "metalworks", -2, 2, ModItems.bob_metalworks, null).initIndependentStat().registerStat();
		bobAssembly = new Achievement("achievement.assembly", "assembly", 0, 2, ModItems.bob_assembly, bobMetalworks).initIndependentStat().registerStat();
		bobChemistry = new Achievement("achievement.chemistry", "chemistry", 2, 2, ModItems.bob_chemistry, bobAssembly).initIndependentStat().registerStat();
		bobOil = new Achievement("achievement.oil", "oil", 4, 2, ModItems.bob_oil, bobChemistry).initIndependentStat().registerStat();
		bobNuclear = new Achievement("achievement.nuclear", "nuclear", 6, 2, ModItems.bob_nuclear, bobOil).initIndependentStat().registerStat();
		bobHidden = new Achievement("achievement.hidden", "hidden", 8, 2, ModItems.gun_dampfmaschine, bobNuclear).initIndependentStat().registerStat();

		horizonsStart = new Achievement("achievement.horizonsStart", "horizonsStart", -2, 4, ModItems.sat_gerald, null).initIndependentStat().registerStat();
		horizonsEnd = new Achievement("achievement.horizonsEnd", "horizonsEnd", 0, 4, ModItems.sat_gerald, horizonsStart).initIndependentStat().registerStat();
		horizonsBonus = new Achievement("achievement.horizonsBonus", "horizonsBonus", 2, 4, ModItems.sat_gerald, horizonsEnd).initIndependentStat().registerStat().setSpecial();

		bossCreeper = new Achievement("achievement.bossCreeper", "bossCreeper", 8, 0, ModItems.coin_creeper, null).initIndependentStat().registerStat();
		bossMeltdown = new Achievement("achievement.bossMeltdown", "bossMeltdown", 9, -1, ModItems.coin_radiation, bossCreeper).initIndependentStat().registerStat();
		bossMaskman = new Achievement("achievement.bossMaskman", "bossMaskman", 9, 1, ModItems.coin_maskman, bossCreeper).initIndependentStat().registerStat();
		bossWorm = new Achievement("achievement.bossWorm", "bossWorm", 11, 1, ModItems.coin_worm, bossMaskman).initIndependentStat().registerStat().setSpecial();

		achRadPoison = new Achievement("achievement.radPoison", "radPoison", -2, 6, ModItems.geiger_counter, null).initIndependentStat().registerStat();
		achRadDeath = new Achievement("achievement.radDeath", "radDeath", 0, 6, Items.skull, achRadPoison).initIndependentStat().registerStat().setSpecial();

		digammaSee = new Achievement("achievement.digammaSee", "digammaSee", -2, 8, ModItems.digamma_see, null).initIndependentStat().registerStat();
		digammaFeel = new Achievement("achievement.digammaFeel", "digammaFeel", 0, 8, ModItems.digamma_feel, digammaSee).initIndependentStat().registerStat();
		digammaKnow = new Achievement("achievement.digammaKnow", "digammaKnow", 2, 8, ModItems.digamma_know, digammaFeel).initIndependentStat().registerStat().setSpecial();
		digammaKauaiMoho = new Achievement("achievement.digammaKauaiMoho", "digammaKauaiMoho", 4, 8, ModItems.digamma_kauai_moho, digammaKnow).initIndependentStat().registerStat().setSpecial();
		digammaUpOnTop = new Achievement("achievement.digammaUpOnTop", "digammaUpOnTop", 6, 8, ModItems.digamma_up_on_top, digammaKauaiMoho).initIndependentStat().registerStat().setSpecial();

		AchievementPage.registerAchievementPage(new AchievementPage("Nuclear Tech", new Achievement[] {
				achSacrifice,
				achImpossible,
				achTOB,
				achFreytag,
				achSelenium,
				achPotato,
				achC44,
				achC20_5,
				achSpace,
				achFOEQ,
				achFiend,
				achFiend2,
				achSoyuz,
				achStratum,
				achMeltdown,
				achOmega12,
				bobMetalworks,
				bobAssembly,
				bobChemistry,
				bobOil,
				bobNuclear,
				bobHidden,
				horizonsStart,
				horizonsEnd,
				horizonsBonus,
				achRadPoison,
				achRadDeath,
				bossCreeper,
				bossMeltdown,
				bossMaskman,
				bossWorm,
				digammaSee,
				digammaFeel,
				digammaKnow,
				digammaKauaiMoho,
				digammaUpOnTop
		}));

		// MUST be initialized AFTER achievements!!
		BobmazonOfferFactory.init();
		OreDictManager.registerOres();
	}

	@EventHandler
	public static void PostLoad(FMLPostInitializationEvent PostEvent) {
		ShredderRecipes.registerShredder();
		ShredderRecipes.registerOverrides();
		CrystallizerRecipes.register();
		CentrifugeRecipes.register();
		BreederRecipes.registerFuels();
		BreederRecipes.registerRecipes();
		AssemblerRecipes.loadRecipes();
		CyclotronRecipes.register();
		HadronRecipes.register();
		MagicRecipes.register();
		SILEXRecipes.register();

		TileEntityNukeCustom.registerBombItems();

		HazmatRegistry.registerHazmats();
		HBMTileEntity.postInitialization();
		HBMEntity.postInitialization();
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(Items.water_bucket), new ItemStack(Items.bucket), FluidType.WATER, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(Items.lava_bucket), new ItemStack(Items.bucket), FluidType.LAVA, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.bucket_mud), new ItemStack(Items.bucket), FluidType.WATZ, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.bucket_schrabidic_acid), new ItemStack(Items.bucket), FluidType.SCHRABIDIC, 1000));

		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.rod_water), new ItemStack(ModItems.rod_empty), FluidType.WATER, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.rod_dual_water), new ItemStack(ModItems.rod_dual_empty), FluidType.WATER, 2000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.rod_quad_water), new ItemStack(ModItems.rod_quad_empty), FluidType.WATER, 4000));

		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.rod_coolant), new ItemStack(ModItems.rod_empty), FluidType.COOLANT, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.rod_dual_coolant), new ItemStack(ModItems.rod_dual_empty), FluidType.COOLANT, 2000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.rod_quad_coolant), new ItemStack(ModItems.rod_quad_empty), FluidType.COOLANT, 4000));

		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_oil), new ItemStack(ModItems.canister_empty), FluidType.OIL, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_smear), new ItemStack(ModItems.canister_empty), FluidType.SMEAR, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_heavyoil), new ItemStack(ModItems.canister_empty), FluidType.HEAVYOIL, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_bitumen), new ItemStack(ModItems.canister_empty), FluidType.BITUMEN, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_heatingoil), new ItemStack(ModItems.canister_empty), FluidType.HEATINGOIL, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_reoil), new ItemStack(ModItems.canister_empty), FluidType.RECLAIMED, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_petroil), new ItemStack(ModItems.canister_empty), FluidType.PETROIL, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_canola), new ItemStack(ModItems.canister_empty), FluidType.LUBRICANT, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_naphtha), new ItemStack(ModItems.canister_empty), FluidType.NAPHTHA, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_fuel), new ItemStack(ModItems.canister_empty), FluidType.DIESEL, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_lightoil), new ItemStack(ModItems.canister_empty), FluidType.LIGHTOIL, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_kerosene), new ItemStack(ModItems.canister_empty), FluidType.KEROSENE, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_biofuel), new ItemStack(ModItems.canister_empty), FluidType.BIOFUEL, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.canister_NITAN), new ItemStack(ModItems.canister_empty), FluidType.NITAN, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.gas_full), new ItemStack(ModItems.gas_empty), FluidType.GAS, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.gas_petroleum), new ItemStack(ModItems.gas_empty), FluidType.PETROLEUM, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.gas_biogas), new ItemStack(ModItems.gas_empty), FluidType.BIOGAS, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModBlocks.red_barrel), new ItemStack(ModItems.tank_steel), FluidType.DIESEL, 10000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModBlocks.pink_barrel), new ItemStack(ModItems.tank_steel), FluidType.KEROSENE, 10000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModBlocks.lox_barrel), new ItemStack(ModItems.tank_steel), FluidType.OXYGEN, 10000));
		
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModBlocks.ore_gneiss_gas), new ItemStack(ModBlocks.stone_gneiss), FluidType.PETROLEUM, 250));

		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.cell_deuterium), new ItemStack(ModItems.cell_empty), FluidType.DEUTERIUM, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.cell_tritium), new ItemStack(ModItems.cell_empty), FluidType.TRITIUM, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.rod_tritium), new ItemStack(ModItems.rod_empty), FluidType.TRITIUM, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.rod_dual_tritium), new ItemStack(ModItems.rod_dual_empty), FluidType.TRITIUM, 2000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.rod_quad_tritium), new ItemStack(ModItems.rod_quad_empty), FluidType.TRITIUM, 4000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.cell_uf6), new ItemStack(ModItems.cell_empty), FluidType.UF6, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.cell_puf6), new ItemStack(ModItems.cell_empty), FluidType.PUF6, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.cell_antimatter), new ItemStack(ModItems.cell_empty), FluidType.AMAT, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.cell_anti_schrabidium), new ItemStack(ModItems.cell_empty), FluidType.ASCHRAB, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.cell_sas3), new ItemStack(ModItems.cell_empty), FluidType.SAS3, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.bottle_mercury), new ItemStack(Items.glass_bottle), FluidType.MERCURY, 1000));

		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.tank_waste, 1, 1), new ItemStack(ModItems.tank_waste, 1, 0), FluidType.WATZ, 8000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.tank_waste, 1, 2), new ItemStack(ModItems.tank_waste, 1, 1), FluidType.WATZ, 8000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.tank_waste, 1, 3), new ItemStack(ModItems.tank_waste, 1, 2), FluidType.WATZ, 8000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.tank_waste, 1, 4), new ItemStack(ModItems.tank_waste, 1, 3), FluidType.WATZ, 8000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.tank_waste, 1, 5), new ItemStack(ModItems.tank_waste, 1, 4), FluidType.WATZ, 8000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.tank_waste, 1, 6), new ItemStack(ModItems.tank_waste, 1, 5), FluidType.WATZ, 8000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.tank_waste, 1, 7), new ItemStack(ModItems.tank_waste, 1, 6), FluidType.WATZ, 8000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.tank_waste, 1, 8), new ItemStack(ModItems.tank_waste, 1, 7), FluidType.WATZ, 8000));

		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.particle_hydrogen), new ItemStack(ModItems.particle_empty), FluidType.HYDROGEN, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.particle_amat), new ItemStack(ModItems.particle_empty), FluidType.AMAT, 1000));
		FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.particle_aschrab), new ItemStack(ModItems.particle_empty), FluidType.ASCHRAB, 1000));

		for(int i = 1; i < FluidType.values().length; i++) {
			FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.fluid_tank_full, 1, i), new ItemStack(ModItems.fluid_tank_empty), FluidType.getEnum(i), 1000));
			FluidContainerRegistry.registerContainer(new FluidContainer(new ItemStack(ModItems.fluid_barrel_full, 1, i), new ItemStack(ModItems.fluid_barrel_empty), FluidType.getEnum(i), 16000));
		}

		TileEntityMachineReactorLarge.registerFuelEntry(1, ReactorFuelType.URANIUM, ModItems.nugget_uranium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(9, ReactorFuelType.URANIUM, ModItems.ingot_uranium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(6, ReactorFuelType.URANIUM, ModItems.rod_uranium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(12, ReactorFuelType.URANIUM, ModItems.rod_dual_uranium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(24, ReactorFuelType.URANIUM, ModItems.rod_quad_uranium_fuel);
		TileEntityMachineReactorLarge.registerWasteEntry(6, ReactorFuelType.URANIUM, ModItems.rod_empty, ModItems.rod_uranium_fuel_depleted);
		TileEntityMachineReactorLarge.registerWasteEntry(12, ReactorFuelType.URANIUM, ModItems.rod_dual_empty, ModItems.rod_dual_uranium_fuel_depleted);
		TileEntityMachineReactorLarge.registerWasteEntry(24, ReactorFuelType.URANIUM, ModItems.rod_quad_empty, ModItems.rod_quad_uranium_fuel_depleted);

		TileEntityMachineReactorLarge.registerFuelEntry(1, ReactorFuelType.PLUTONIUM, ModItems.nugget_plutonium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(9, ReactorFuelType.PLUTONIUM, ModItems.ingot_plutonium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(6, ReactorFuelType.PLUTONIUM, ModItems.rod_plutonium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(12, ReactorFuelType.PLUTONIUM, ModItems.rod_dual_plutonium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(24, ReactorFuelType.PLUTONIUM, ModItems.rod_quad_plutonium_fuel);
		TileEntityMachineReactorLarge.registerWasteEntry(6, ReactorFuelType.PLUTONIUM, ModItems.rod_empty, ModItems.rod_plutonium_fuel_depleted);
		TileEntityMachineReactorLarge.registerWasteEntry(12, ReactorFuelType.PLUTONIUM, ModItems.rod_dual_empty, ModItems.rod_dual_plutonium_fuel_depleted);
		TileEntityMachineReactorLarge.registerWasteEntry(24, ReactorFuelType.PLUTONIUM, ModItems.rod_quad_empty, ModItems.rod_quad_plutonium_fuel_depleted);

		TileEntityMachineReactorLarge.registerFuelEntry(1, ReactorFuelType.MOX, ModItems.nugget_mox_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(9, ReactorFuelType.MOX, ModItems.ingot_mox_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(6, ReactorFuelType.MOX, ModItems.rod_mox_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(12, ReactorFuelType.MOX, ModItems.rod_dual_mox_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(24, ReactorFuelType.MOX, ModItems.rod_quad_mox_fuel);
		TileEntityMachineReactorLarge.registerWasteEntry(6, ReactorFuelType.MOX, ModItems.rod_empty, ModItems.rod_mox_fuel_depleted);
		TileEntityMachineReactorLarge.registerWasteEntry(12, ReactorFuelType.MOX, ModItems.rod_dual_empty, ModItems.rod_dual_mox_fuel_depleted);
		TileEntityMachineReactorLarge.registerWasteEntry(24, ReactorFuelType.MOX, ModItems.rod_quad_empty, ModItems.rod_quad_mox_fuel_depleted);

		TileEntityMachineReactorLarge.registerFuelEntry(10, ReactorFuelType.SCHRABIDIUM, ModItems.nugget_schrabidium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(90, ReactorFuelType.SCHRABIDIUM, ModItems.ingot_schrabidium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(60, ReactorFuelType.SCHRABIDIUM, ModItems.rod_schrabidium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(120, ReactorFuelType.SCHRABIDIUM, ModItems.rod_dual_schrabidium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(240, ReactorFuelType.SCHRABIDIUM, ModItems.rod_quad_schrabidium_fuel);
		TileEntityMachineReactorLarge.registerWasteEntry(60, ReactorFuelType.SCHRABIDIUM, ModItems.rod_empty, ModItems.rod_schrabidium_fuel_depleted);
		TileEntityMachineReactorLarge.registerWasteEntry(120, ReactorFuelType.SCHRABIDIUM, ModItems.rod_dual_empty, ModItems.rod_dual_schrabidium_fuel_depleted);
		TileEntityMachineReactorLarge.registerWasteEntry(240, ReactorFuelType.SCHRABIDIUM, ModItems.rod_quad_empty, ModItems.rod_quad_schrabidium_fuel_depleted);

		TileEntityMachineReactorLarge.registerFuelEntry(1, ReactorFuelType.THORIUM, ModItems.nugget_thorium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(9, ReactorFuelType.THORIUM, ModItems.ingot_thorium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(6, ReactorFuelType.THORIUM, ModItems.rod_thorium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(12, ReactorFuelType.THORIUM, ModItems.rod_dual_thorium_fuel);
		TileEntityMachineReactorLarge.registerFuelEntry(24, ReactorFuelType.THORIUM, ModItems.rod_quad_thorium_fuel);
		TileEntityMachineReactorLarge.registerWasteEntry(6, ReactorFuelType.THORIUM, ModItems.rod_empty, ModItems.rod_thorium_fuel_depleted);
		TileEntityMachineReactorLarge.registerWasteEntry(12, ReactorFuelType.THORIUM, ModItems.rod_dual_empty, ModItems.rod_dual_thorium_fuel_depleted);
		TileEntityMachineReactorLarge.registerWasteEntry(24, ReactorFuelType.THORIUM, ModItems.rod_quad_empty, ModItems.rod_quad_thorium_fuel_depleted);

		proxy.registerMissileItems();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if(logger == null)
			logger = event.getModLog();

		FMLCommonHandler.instance().bus().register(new ModEventHandler());
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
		MinecraftForge.TERRAIN_GEN_BUS.register(new ModEventHandler());
		MinecraftForge.ORE_GEN_BUS.register(new ModEventHandler());
		PacketDispatcher.registerPackets();
	}
	
	private void loadConfig(FMLPreInitializationEvent event) {

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		GeneralConfig.loadFromConfig(config);
		WorldConfig.loadFromConfig(config);
		MachineConfig.loadFromConfig(config);
		BombConfig.loadFromConfig(config);
		RadiationConfig.loadFromConfig(config);
		PotionConfig.loadFromConfig(config);
		ToolConfig.loadFromConfig(config);
		WeaponConfig.loadFromConfig(config);
		MobConfig.loadFromConfig(config);

		config.save();
	}
}
