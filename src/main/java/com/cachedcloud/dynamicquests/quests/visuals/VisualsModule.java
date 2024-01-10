package com.cachedcloud.dynamicquests.quests.visuals;

import com.cachedcloud.dynamicquests.DynamicQuests;
import com.cachedcloud.dynamicquests.events.PlayerQuestChangeStatusEvent;
import com.cachedcloud.dynamicquests.events.PlayerQuestProgressEvent;
import com.cachedcloud.dynamicquests.quests.tracking.QuestProgress;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedRegistrable;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import me.lucko.helper.text3.Text;
import me.lucko.helper.utils.Players;
import net.jodah.expiringmap.ExpiringMap;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
This module is for visualizing quest progress using.. a sign
I was thinking about adding a bossbar as well as that's fairly easy with lucko's
helper utilities, but I ultimately decided not to do that
 */
@RequiredArgsConstructor
public class VisualsModule implements TerminableModule {

  private final ExpiringMap<UUID, Long> recentUpdates = ExpiringMap.builder()
      .expiration(10, TimeUnit.SECONDS)
      .build();

  private final FileConfiguration config;

  @Override
  public void setup(@NotNull TerminableConsumer consumer) {
    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    if (!config.contains("sign")) return;
    int signX = config.getInt("sign.x");
    int signY = config.getInt("sign.y");
    int signZ = config.getInt("sign.z");

    Vector signLocation = new Vector(signX, signY, signZ); // -1175, 131, 648
    BlockPosition signPosition = new BlockPosition(signLocation);

    Events.subscribe(PlayerQuestChangeStatusEvent.class)
        .handler(event -> {
          Player player = Players.getNullable(event.getPlayerUuid());

          // Check the quest status
          if (event.getStatus() == PlayerQuestChangeStatusEvent.Status.START) {
            // If it just started, set the progress to 0%
            this.setSignContent(protocolManager, signLocation, player,
                " ", Text.colorize("&0&lQUEST PROGRESS"), Text.colorize("&00%"), " ");

          } else if (event.getStatus() == PlayerQuestChangeStatusEvent.Status.CONTINUE) {
            // Calculate progress and show that
            int percentage = this.getQuestProgressPercentage(event.getProgress());
            Schedulers.sync().runLater(() -> {
              this.setSignContent(protocolManager, signLocation, player,
                  " ", Text.colorize("&0&lQUEST PROGRESS"), Text.colorize("&0" + percentage + "%"), " ");
            }, 50);

            recentUpdates.put(event.getPlayerUuid(), System.currentTimeMillis());

          } else if (event.getStatus() == PlayerQuestChangeStatusEvent.Status.COMPLETE) {
            // The quest is complete, so show that to the player
            this.setSignContent(protocolManager, signLocation, player,
                " ", Text.colorize("&0&lQUEST PROGRESS"), Text.colorize("&aComplete"), " ");
          } else {
            // The quest progress is deleted or the quest is cancelled (same thing)
            this.setSignContent(protocolManager, signLocation, player,
                " ", Text.colorize("&0No active quest."), Text.colorize("&0See &6/quests"), " ");
          }
        }).bindWith(consumer);

    Events.subscribe(PlayerQuestProgressEvent.class)
        .handler(event -> {
          // Check if progress was updated recently
          if (recentUpdates.containsKey(event.getPlayerUuid())) return;

          // Calculate and update progress
          int percentage = this.getQuestProgressPercentage(event.getProgress());

          this.setSignContent(protocolManager, signLocation, Players.getNullable(event.getPlayerUuid()),
              " ", Text.colorize("&0&lQUEST PROGRESS"), Text.colorize("&0" + percentage + "%"), " ");
          recentUpdates.put(event.getPlayerUuid(), System.currentTimeMillis());
        }).bindWith(consumer);

    Events.subscribe(PlayerJoinEvent.class)
        .handler(event -> {
          // Tell the player they don't have an active quest
          Schedulers.sync().runLater(() -> {
            if (recentUpdates.containsKey(event.getPlayer().getUniqueId())) return;

            this.setSignContent(protocolManager, signLocation, event.getPlayer(),
                " ", Text.colorize("&0No active quest."), Text.colorize("&0See &6/quests"), " ");
          }, 30L);

        }).bindWith(consumer);

    // Cancel sign editor for the quest progress sign
    protocolManager.addPacketListener(new PacketAdapter(DynamicQuests.getInstance(), PacketType.Play.Server.OPEN_SIGN_EDITOR) {
      @Override
      public void onPacketSending(PacketEvent event) {
        BlockPosition pos = event.getPacket().getBlockPositionModifier().getValues().get(0);
        if (pos.equals(signPosition)) event.setCancelled(true);
      }
    });
  }

  private void setSignContent(ProtocolManager manager, Vector location, Player player,
                              String firstLine, String secondLine, String thirdLine, String fourthLine) {
    BlockPosition position = new BlockPosition(location);
    // block change
    PacketContainer blockChangePacket = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
    blockChangePacket.getBlockPositionModifier().write(0, position);
    blockChangePacket.getBlockData().write(0, WrappedBlockData.createData(Material.OAK_SIGN));

    // tile entity
    NBTTagCompound tileEntityNbt = new NBTTagCompound();
    tileEntityNbt.a("front_text", getSideCompound(firstLine, secondLine, thirdLine, fourthLine));
    tileEntityNbt.a("back_text", getSideCompound(firstLine, secondLine, thirdLine, fourthLine));
    tileEntityNbt.a("is_waxed", NBTTagByte.a(false));

    PacketContainer tileEntityPacket = new PacketContainer(PacketType.Play.Server.TILE_ENTITY_DATA);
    tileEntityPacket.getBlockPositionModifier().write(0, position);
    tileEntityPacket.getBlockEntityTypeModifier().write(0, WrappedRegistrable.blockEntityType("sign"));
    tileEntityPacket.getNbtModifier().write(0, NbtFactory.fromNMSCompound(tileEntityNbt));

    manager.sendServerPacket(player, blockChangePacket);
    manager.sendServerPacket(player, tileEntityPacket);
  }

  private NBTTagCompound getSideCompound(String firstLine, String secondLine, String thirdLine, String fourthLine) {
    NBTTagCompound nbtTagCompound = new NBTTagCompound();
    nbtTagCompound.a("has_glowing_text", NBTTagByte.a(false));
    nbtTagCompound.a("color", "black");

    NBTTagList messagesCompound = new NBTTagList();
    messagesCompound.add(NBTTagString.a("{\"text\":\"" + firstLine + "\"}"));
    messagesCompound.add(NBTTagString.a("{\"text\":\"" + secondLine + "\"}"));
    messagesCompound.add(NBTTagString.a("{\"text\":\"" + thirdLine + "\"}"));
    messagesCompound.add(NBTTagString.a("{\"text\":\"" + fourthLine + "\"}"));

    nbtTagCompound.a("messages", messagesCompound);
    return nbtTagCompound;
  }

  /* Get the quest progress as a total percentage, this will include all objectives */
  public int getQuestProgressPercentage(QuestProgress progress) {
    // Calculate total progress and required progress
    int totalRequired = 0;
    int totalComplete = 0;

    // Loop through the progress entries for all objectives
    for (QuestProgress.ProgressEntry entry : progress.getProgress().values()) {
      totalRequired += entry.getRequirement();
      totalComplete += entry.getProgress().intValue();
    }
    totalRequired = Math.max(totalRequired, 1); // For debugging in case there are no objectives

    // Calculate percentage
    return (int) (((double) totalComplete / totalRequired) * 100);
  }
}
