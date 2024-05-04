package kr.toxicity.healthbar.nms.v1_20_R4

import com.mojang.math.Transformation
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.papermc.paper.adventure.PaperAdventure
import io.papermc.paper.chunk.system.entity.EntityLookup
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap
import kr.toxicity.healthbar.api.BetterHealthBar
import kr.toxicity.healthbar.api.trigger.HealthBarTriggerType
import kr.toxicity.healthbar.api.nms.NMS
import kr.toxicity.healthbar.api.nms.VirtualTextDisplay
import kr.toxicity.healthbar.api.player.HealthBarPlayer
import kr.toxicity.healthbar.api.trigger.PacketTrigger
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.pointer.Pointers
import net.kyori.adventure.text.Component
import net.minecraft.network.Connection
import net.minecraft.network.protocol.game.*
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerCommonPacketListenerImpl
import net.minecraft.util.Brightness
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Display.TextDisplay
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.entity.LevelEntityGetter
import net.minecraft.world.level.entity.PersistentEntitySectionManager
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.WorldBorder
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftLivingEntity
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.PlayerInventory
import org.bukkit.permissions.Permission
import org.bukkit.util.Vector
import org.joml.Vector3f
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

@Suppress("UNUSED")
class NMSImpl: NMS {
    private val plugin
        get() = BetterHealthBar.inst()

    private val getConnection: (ServerCommonPacketListenerImpl) -> Connection = if (plugin.isPaper) {
        {
            it.connection
        }
    } else {
        ServerCommonPacketListenerImpl::class.java.declaredFields.first { f ->
            f.type == Connection::class.java
        }.apply {
            isAccessible = true
        }.let { get ->
            {
                get[it] as Connection
            }
        }
    }
    private val entityTracker = ServerLevel::class.java.fields.firstOrNull {
        it.type == PersistentEntitySectionManager::class.java
    }

    private val getEntityById: (LevelEntityGetter<net.minecraft.world.entity.Entity>, Int) -> net.minecraft.world.entity.Entity? = if (plugin.isPaper) EntityLookup::class.java.declaredFields.first {
        Int2ReferenceOpenHashMap::class.java.isAssignableFrom(it.type)
    }.let {
        it.isAccessible = true
        { e, i ->
            (it[e] as Int2ReferenceOpenHashMap<*>)[i] as? net.minecraft.world.entity.Entity
        }
    } else PersistentEntitySectionManager::class.java.declaredFields.first {
        net.minecraft.world.level.entity.EntityLookup::class.java.isAssignableFrom(it.type)
    }.let {
        it.isAccessible = true
        { e, i ->
            (it[e] as net.minecraft.world.level.entity.EntityLookup<*>).getEntity(i) as? net.minecraft.world.entity.Entity
        }
    }
    private val getEntityFromMovePacket: (ClientboundMoveEntityPacket) -> Int = ClientboundMoveEntityPacket::class.java.declaredFields.first {
        Integer.TYPE.isAssignableFrom(it.type)
    }.let {
        it.isAccessible = true
        { p ->
            it[p] as Int
        }
    }

    override fun foliaAdapt(player: Player): Player {
        val handle = (player as CraftPlayer).handle
        return object : CraftPlayer(Bukkit.getServer() as CraftServer, handle) {
            override fun getPersistentDataContainer(): CraftPersistentDataContainer {
                return player.persistentDataContainer
            }
            override fun getHandle(): ServerPlayer {
                return handle
            }
            override fun getHealth(): Double {
                return player.health
            }
            override fun getScaledHealth(): Float {
                return player.scaledHealth
            }
            override fun getFirstPlayed(): Long {
                return player.firstPlayed
            }
            override fun getInventory(): PlayerInventory {
                return player.inventory
            }
            override fun getEnderChest(): Inventory {
                return player.enderChest
            }
            override fun isOp(): Boolean {
                return player.isOp
            }
            override fun getGameMode(): GameMode {
                return player.gameMode
            }
            override fun getEquipment(): EntityEquipment {
                return player.equipment
            }
            override fun hasPermission(name: String): Boolean {
                return player.hasPermission(name)
            }
            override fun hasPermission(perm: Permission): Boolean {
                return player.hasPermission(perm)
            }
            override fun isPermissionSet(name: String): Boolean {
                return player.isPermissionSet(name)
            }
            override fun isPermissionSet(perm: Permission): Boolean {
                return player.isPermissionSet(perm)
            }
            override fun hasPlayedBefore(): Boolean {
                return player.hasPlayedBefore()
            }
            override fun getWorldBorder(): WorldBorder? {
                return player.getWorldBorder()
            }
            override fun showBossBar(bar: BossBar) {
                player.showBossBar(bar)
            }
            override fun hideBossBar(bar: BossBar) {
                player.hideBossBar(bar)
            }
            override fun sendMessage(message: String) {
                player.sendMessage(message)
            }
            override fun getLastDamageCause(): EntityDamageEvent? {
                return player.lastDamageCause
            }
            override fun pointers(): Pointers {
                return player.pointers()
            }
            override fun spigot(): Player.Spigot {
                return player.spigot()
            }
        }
    }

    override fun foliaAdapt(entity: org.bukkit.entity.LivingEntity): org.bukkit.entity.LivingEntity {
        val handle = (entity as CraftLivingEntity).handle
        return object : CraftLivingEntity(Bukkit.getServer() as CraftServer, handle) {
            override fun getPersistentDataContainer(): CraftPersistentDataContainer {
                return entity.persistentDataContainer
            }
            override fun getHandle(): LivingEntity {
                return handle
            }
            override fun getHealth(): Double {
                return entity.health
            }
            override fun isOp(): Boolean {
                return entity.isOp
            }
            override fun getEquipment(): EntityEquipment? {
                return entity.equipment
            }
            override fun hasPermission(name: String): Boolean {
                return entity.hasPermission(name)
            }
            override fun hasPermission(perm: Permission): Boolean {
                return entity.hasPermission(perm)
            }
            override fun isPermissionSet(name: String): Boolean {
                return entity.isPermissionSet(name)
            }
            override fun isPermissionSet(perm: Permission): Boolean {
                return entity.isPermissionSet(perm)
            }
            override fun showBossBar(bar: BossBar) {
                entity.showBossBar(bar)
            }
            override fun hideBossBar(bar: BossBar) {
                entity.hideBossBar(bar)
            }
            override fun sendMessage(message: String) {
                entity.sendMessage(message)
            }
            override fun getLastDamageCause(): EntityDamageEvent? {
                return entity.lastDamageCause
            }
            override fun pointers(): Pointers {
                return entity.pointers()
            }

            override fun spigot(): Entity.Spigot {
                return entity.spigot()
            }
        }
    }

    override fun createTextDisplay(player: Player, location: Location, component: Component): VirtualTextDisplay {
        val connection = (player as CraftPlayer).handle.connection
        val display = TextDisplay(EntityType.TEXT_DISPLAY, (player.world as CraftWorld).handle).apply {
            billboardConstraints = Display.BillboardConstraints.CENTER
            entityData.run {
                set(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID, 1)
                set(TextDisplay.DATA_BACKGROUND_COLOR_ID, 0)
                set(TextDisplay.DATA_LINE_WIDTH_ID, Int.MAX_VALUE)
            }
            brightnessOverride = Brightness(15, 15)
            text = PaperAdventure.asVanilla(component)
            moveTo(
                location.x,
                location.y,
                location.z,
                location.yaw,
                location.pitch
            )
            connection.send(ClientboundAddEntityPacket(this))
            connection.send(ClientboundSetEntityDataPacket(id, entityData.nonDefaultValues!!))
        }
        return object : VirtualTextDisplay {
            override fun teleport(location: Location) {
                display.moveTo(
                    location.x,
                    location.y,
                    location.z,
                    location.yaw,
                    location.pitch
                )
                connection.send(ClientboundTeleportEntityPacket(display))
            }

            override fun text(component: Component) {
                display.text = PaperAdventure.asVanilla(component)
                connection.send(ClientboundSetEntityDataPacket(display.id, display.entityData.nonDefaultValues!!))
            }

            override fun scale(vector: Vector) {
                display.setTransformation(Transformation(null, null, Vector3f(vector.x.toFloat(), vector.y.toFloat(), vector.z.toFloat()), null))
                connection.send(ClientboundSetEntityDataPacket(display.id, display.entityData.nonDefaultValues!!))
            }

            override fun remove() {
                connection.send(ClientboundRemoveEntitiesPacket(display.id))
            }
        }
    }

    private val injectionMap = ConcurrentHashMap<UUID, PlayerInjection>()

    override fun inject(player: HealthBarPlayer) {
        injectionMap.computeIfAbsent(player.player().uniqueId) {
            PlayerInjection(player)
        }
    }

    override fun uninject(player: HealthBarPlayer) {
        injectionMap.remove(player.player().uniqueId)?.uninject()
    }

    private inner class PlayerInjection(val player: HealthBarPlayer): ChannelDuplexHandler() {
        private val serverPlayer = (player.player() as CraftPlayer).handle
        private val connection = serverPlayer.connection

        init {
            val pipeLine = getConnection(connection).channel.pipeline()
            pipeLine.toMap().forEach {
                if (it.value is Connection) pipeLine.addBefore(it.key, BetterHealthBar.NAMESPACE, this)
            }
        }

        fun uninject() {
            val channel = getConnection(connection).channel
            channel.eventLoop().submit {
                channel.pipeline().remove(BetterHealthBar.NAMESPACE)
            }
        }

        private fun show(handle: Any, trigger: HealthBarTriggerType, entity: net.minecraft.world.entity.Entity?) {
            val playerX = serverPlayer.x
            val playerY = serverPlayer.y
            val playerZ = serverPlayer.z
            fun Double.square() = this * this
            entity?.let { e ->
                if (sqrt((playerX - e.x).square()  + (playerY - e.y).square() + (playerZ - e.z).square()) > plugin.configManager().lookDistance()) return
                plugin.healthBarManager().allHealthBars().filter {
                    it.triggers().contains(trigger)
                }.forEach {
                    val bukkit = e.bukkitEntity
                    if (bukkit is CraftLivingEntity && bukkit.isValid) {
                        if (plugin.isFolia) {
                            plugin.scheduler().task(bukkit.location) {
                                player.showHealthBar(it, PacketTrigger(trigger, handle), foliaAdapt(bukkit))
                            }
                        } else player.showHealthBar(it, PacketTrigger(trigger, handle), foliaAdapt(bukkit))
                    }
                }
            }
        }
        private fun getViewedEntity(): List<LivingEntity> {
            val playerX = serverPlayer.x
            val playerY = serverPlayer.y
            val playerZ = serverPlayer.z

            var yaw = serverPlayer.yHeadRot.toDouble()
            if (yaw < 90) yaw += 90
            else yaw = -(360 - (yaw + 90))
            val playerYaw = Math.toRadians(yaw)
            val playerPitch = Math.toRadians(-serverPlayer.xRot.toDouble())

            val degree = plugin.configManager().lookDegree()

            return getLevelGetter().all.mapNotNull {
                if (it is LivingEntity && it !== serverPlayer) {
                    val x = it.x
                    val y = it.y
                    val z = it.z

                    if (abs(atan2(y - playerY, abs(x - playerX)) - playerPitch) <= degree && abs(atan2(z - playerZ, x - playerX) - playerYaw) <= degree) it
                    else null
                } else null
            }
        }

        @Suppress("UNCHECKED_CAST")
        private fun getLevelGetter(): LevelEntityGetter<net.minecraft.world.entity.Entity> {
            return if (plugin.isPaper) {
                serverPlayer.serverLevel().entityLookup
            } else {
                entityTracker?.get(serverPlayer.serverLevel())?.let {
                    (it as PersistentEntitySectionManager<*>).entityGetter as LevelEntityGetter<net.minecraft.world.entity.Entity>
                } ?: throw RuntimeException("LevelEntityGetter")
            }
        }

        private fun Int.toEntity() = getEntityById(getLevelGetter(), this)

        override fun write(ctx: ChannelHandlerContext?, msg: Any?, promise: ChannelPromise?) {
            when (msg) {
                is ClientboundDamageEventPacket -> show(msg, HealthBarTriggerType.DAMAGE, msg.entityId.toEntity())
                is ClientboundMoveEntityPacket -> show(msg, HealthBarTriggerType.MOVE, getEntityFromMovePacket(msg).toEntity())
            }
            super.write(ctx, msg, promise)
        }

        override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
            when (msg) {
                is ServerboundMovePlayerPacket -> getViewedEntity().forEach {
                    show(msg, HealthBarTriggerType.LOOK, it)
                }
            }
            super.channelRead(ctx, msg)
        }
    }
}