package org.arsparadox.mobtalkerredux;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import java.util.*;

public class WaifuManager {
    private static final String WAIFU_DATA_KEY = "WaifuManagerData";
    private final Map<UUID, Waifu> waifus;
    private final Player player;

    public WaifuManager(Player player) {
        this.player = player;
        this.waifus = new HashMap<>();
        loadWaifus();
    }

    public void addWaifu(Waifu waifu) {
        waifus.put(waifu.getId(), waifu);
        saveWaifus();
    }

    public void removeWaifu(UUID waifuId) {
        waifus.remove(waifuId);
        saveWaifus();
    }

    public Waifu getWaifu(UUID waifuId) {
        return waifus.get(waifuId);
    }

    public Collection<Waifu> getAllWaifus() {
        return Collections.unmodifiableCollection(waifus.values());
    }

    public void saveWaifus() {
        CompoundTag playerData = player.getPersistentData();
        CompoundTag waifuManager = new CompoundTag();
        ListTag waifuList = new ListTag();

        for (Waifu waifu : waifus.values()) {
            waifuList.add(waifu.saveToNBT());
        }

        waifuManager.put("Waifus", waifuList);
        playerData.put(WAIFU_DATA_KEY, waifuManager);
    }

    private void loadWaifus() {
        CompoundTag playerData = player.getPersistentData();
        if (playerData.contains(WAIFU_DATA_KEY)) {
            CompoundTag waifuManager = playerData.getCompound(WAIFU_DATA_KEY);
            ListTag waifuList = waifuManager.getList("Waifus", 10); // 10 is the NBT type for CompoundTag

            for (int i = 0; i < waifuList.size(); i++) {
                CompoundTag waifuTag = waifuList.getCompound(i);
                Waifu waifu = createWaifuFromNBT(waifuTag);
                if (waifu != null) {
                    waifus.put(waifu.getId(), waifu);
                }
            }
        }
    }

    private Waifu createWaifuFromNBT(CompoundTag tag) {
        if (!tag.hasUUID("UUID")) return null;

        Waifu waifu = new Waifu(
                tag.getString("Name"),
                tag.getString("MobType"),
                tag.getString("SpritesFolder"),
                tag.getString("DialoguesFolder"),
                tag.getString("QuestsFolder"),
                tag.getString("LastConversation"),
                tag.getString("LastDialogue"),
                tag.getInt("Affection"),
                tag.getInt("Reputation"),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        // Load the rest of the data
        waifu.loadFromNBT(tag);
        return waifu;
    }

    // Utility methods for managing waifus
    public List<Waifu> getWaifusByAffectionThreshold(int threshold) {
        return waifus.values().stream()
                .filter(w -> w.getAffection() >= threshold)
                .sorted(Comparator.comparingInt(Waifu::getAffection).reversed())
                .toList();
    }

    public Optional<Waifu> getWaifuByName(String name) {
        return waifus.values().stream()
                .filter(w -> w.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public boolean hasWaifu(UUID waifuId) {
        return waifus.containsKey(waifuId);
    }

    public int getWaifuCount() {
        return waifus.size();
    }
}
