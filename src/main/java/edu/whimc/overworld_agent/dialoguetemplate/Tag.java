package edu.whimc.overworld_agent.dialoguetemplate;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.dialoguetemplate.models.DialogueTag;
import edu.whimc.overworld_agent.utils.Utils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Tag {
    private static final List<Tag> tags = new ArrayList<>();
    private static final Map<Player, Map<World,Integer>> playerTags = new HashMap<>();
    private Player player;
    private Timestamp tagTime;
    private Timestamp tagExpiration;
    private Hologram hologram;
    private Location viewLocation;
    private Location holoLocation;
    private Material hologramItem = Material.NAME_TAG;
    private OverworldAgent plugin;
    private String tagText;
    private boolean active;
    private int id;
    private static boolean display;
    private static boolean tagFeedbackEnabled;
    private static Map<World, List<DialogueTag>> dialogueTags;
    private static String defaultTagFeedback;
    private static Map<World,Integer> numTagsAllowed;
    public Tag(OverworldAgent plugin, Player player, String tag){
        this.plugin = plugin;
        this.player = player;
        viewLocation = player.getLocation();
        holoLocation = viewLocation.clone().add(0, 3, 0).add(viewLocation.getDirection().multiply(2));
        this.tagText = tag;
        int days = plugin.getConfig().getInt("expiration-days");
        tagExpiration = Timestamp.from(Instant.now().plus(days, ChronoUnit.DAYS));
        tagTime = new Timestamp(System.currentTimeMillis());
        active = true;

        if(!playerTags.containsKey(player)){
            playerTags.putIfAbsent(player,new HashMap<>());
            playerTags.get(player).putIfAbsent(player.getWorld(),1);
        } else if (!playerTags.get(player).containsKey(player.getWorld())){
            playerTags.get(player).putIfAbsent(player.getWorld(),1);
        } else {
            int numTags = playerTags.get(player).get(player.getWorld())+1;
            playerTags.get(player).put(player.getWorld(), numTags);
        }
    }

    public static void instantiate(OverworldAgent plugin){
        dialogueTags = new HashMap<>();
        numTagsAllowed = new HashMap<>();
        String path = "tags";
        FileConfiguration config = plugin.getConfig();
        for (String key : config.getConfigurationSection(path).getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(path + "." + key);
            if (key.equalsIgnoreCase("feedback")) {
                defaultTagFeedback = section.getString("default");
                tagFeedbackEnabled = section.getBoolean("enabled");
                display = section.getBoolean("holo_visible");
            } else if (key.equalsIgnoreCase("num_tags")) {
                for (String world : config.getConfigurationSection(path + "." + key).getKeys(false)) {
                    numTagsAllowed.put(Bukkit.getWorld(world), config.getConfigurationSection(path + "." + key).getInt(world));
                }
            } else {
                List<Map<?, ?>> tagEntries = plugin.getConfig().getMapList(path + "." + key);
                for (Map<?, ?> tagEntry : tagEntries) {
                    dialogueTags.putIfAbsent(Bukkit.getWorld(key), new ArrayList<>());
                    dialogueTags.get(Bukkit.getWorld(key)).add(new DialogueTag(tagEntry));
                }
            }
        }
    }
    public void sendFeedback(){
        String[] words = tagText.split(" ");
        boolean tagSeen = false;
        if (tagFeedbackEnabled) {
            if(dialogueTags.containsKey(player.getWorld())) {
                for (DialogueTag tag : dialogueTags.get(player.getWorld())) {
                    for (String alias : tag.getAliases()) {
                        for (String word : words) {
                            word = word.toLowerCase();
                            if (word.contains(alias)) {
                                player.sendMessage(tag.getFeedback());
                                tagSeen = true;
                            }
                        }
                    }
                }
                if (!tagSeen) {
                    player.sendMessage(defaultTagFeedback);
                }
            } else {
                player.sendMessage("Sorry, feedback is not currently implemented on this world");
            }
        }
        this.plugin.getQueryer().storeNewTag(this, id -> {
            if(display) {
                createHologram();
                Utils.msg(player,
                        "&7Your tag has been placed:",
                        "  &8\"&f&l" + tagText + "&8\"");
                this.id = id;
                tags.add(this);
            }
            int maxTagsAllowedOnWorld = numTagsAllowed.get(player.getWorld());
            int numTags = playerTags.get(player).get(player.getWorld());
            int numTagsLeft = maxTagsAllowedOnWorld-numTags;
            player.sendMessage("You have " + numTagsLeft + " tags left!");
        });
    }
    private void createHologram() {

        Hologram holo = HologramsAPI.createHologram(this.plugin, holoLocation);

        holo.appendItemLine(new ItemStack(hologramItem));
        holo.appendTextLine(Utils.color(tagText));
        holo.appendTextLine(ChatColor.GRAY + player.getName() + " - " + Utils.getDate(tagTime));
        holo.getVisibilityManager().setVisibleByDefault(false);
        holo.getVisibilityManager().showTo(player);
        if (this.tagExpiration != null) {
            holo.appendTextLine(ChatColor.GRAY + "Expires " + Utils.getDate(this.tagExpiration));
        }
        this.hologram = holo;
    }
    public static void startExpiredObservationScanningTask(OverworldAgent plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Utils.debug("Scanning for expired tags...");
            List<Tag> toRemove = tags.stream()
                    .filter(Tag::hasExpired)
                    .collect(Collectors.toList());

            int count = toRemove.size();
            toRemove.forEach(tag -> tag.deleteTag());

            if (count > 0) {
                plugin.getQueryer().makeExpiredTagsInactive(dbCount -> {
                    Utils.debug("Removed " + count + " expired observation(s). (" + dbCount + " from the database)");
                });
            }
        }, 20 * 60, 20 * 60);
    }
    public static List<String> getTagsTabComplete(String hint) {
        return tags.stream()
                .filter(v -> Integer.toString(v.getId()).startsWith(hint))
                .sorted(Comparator.comparing(Tag::getId))
                .map(v -> Integer.toString(v.getId()))
                .collect(Collectors.toList());
    }
    public static Tag getTagByID(int id) {
        for (Tag tag : tags) {
            if (tag.getId() == id) return tag;
        }

        return null;
    }
    public void deleteAndSetInactive(Runnable callback) {
        this.plugin.getQueryer().makeSingleTagInactive(this.id, callback);
        deleteTag();
    }
    public boolean hasExpired() {
        return this.tagExpiration != null && Instant.now().isAfter(tagExpiration.toInstant());
    }
    public void deleteHologramOnly() {
        if (this.hologram != null) {
            this.hologram.delete();
            this.hologram = null;
        }
    }

    public String getTag(){return tagText;}
    public void deleteTag() {
        deleteHologramOnly();
        active = false;
        tags.remove(this);
    }
    public Location getHoloLocation(){return holoLocation;}
    public Player getPlayer(){return player;}
    public Timestamp getTagTime(){return tagTime;}
    public Timestamp getExpiration(){return tagExpiration;}
    public boolean getActive(){return active;}
    public int getId(){return id;}
    public static Map<Player, Map<World, Integer>> getPlayerTags(){
        return playerTags;
    }
    public static Integer maxTags(World world){
        return numTagsAllowed.get(world);
    }
}
