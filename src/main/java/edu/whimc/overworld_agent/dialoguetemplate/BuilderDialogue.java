package edu.whimc.overworld_agent.dialoguetemplate;

import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.dialoguetemplate.models.BuildTemplate;
import edu.whimc.overworld_agent.dialoguetemplate.runnables.RebuildRunnable;
import edu.whimc.overworld_agent.utils.Utils;
import net.citizensnpcs.trait.FollowTrait;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.whimxiqal.journey.Cell;
import net.whimxiqal.journey.Journey;
import net.whimxiqal.journey.bukkit.util.BukkitUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Consumer;

public class BuilderDialogue {
    private Player player;
    private OverworldAgent plugin;
    private SpigotCallback spigotCallback;
    private static final String BULLET = "\u2022";
    private boolean makingTemplate;
    public BuilderDialogue(OverworldAgent plugin, Player player){
        this.plugin = plugin;
        this.player = player;
        this.spigotCallback = new SpigotCallback(plugin);
        this.makingTemplate = false;
    }

    public void doDialogue(){
        HashMap<Player,List<BuildTemplate>> templates = plugin.getBuildTemplates();
        Utils.msgNoPrefix(player, "&lWhat do you want to do?", "");
        if(player.isOp()){
            sendComponent(
                    player,
                    "&8" + BULLET + "&f&nI want to demo a build using the build ID!",
                    "&aClick here to demo a build!",
                    p -> this.plugin.getSignMenuFactory()
                            .newMenu(Collections.singletonList(Utils.color("")))
                            .reopenIfFail(true)
                            .response((signPlayer, strings) -> {
                                String response = StringUtils.join(Arrays.copyOfRange(strings, 0, strings.length), ' ').trim();
                                if (response.isEmpty()) {
                                    return false;
                                }
                                int buildID;
                                try {
                                    buildID = Integer.parseInt(response);
                                } catch(NumberFormatException e){
                                    return false;
                                }
                                    plugin.getQueryer().getBuildTemplate(buildID, template -> {
                                        if(template != null) {
                                            this.plugin.getQueryer().storeNewBuildInteraction(new Interaction(plugin, player, "Demo Build"), buildID, id -> {
                                            BuildTemplate bt = (BuildTemplate) template;
                                            bt.build();
                                            });
                                        } else {
                                            player.sendMessage("Template with ID " + buildID +" does not exist!");
                                        }
                                });
                                this.spigotCallback.clearCallbacks(player);
                                return true;
                            })
                            .open(p)
            );
        }
        if(!makingTemplate) {
            sendComponent(
                    player,
                    "&8" + BULLET + "&f&nI want to start a template!",
                    "&aClick here to start a build template!",
                        p -> this.plugin.getSignMenuFactory()
                                .newMenu(Collections.singletonList(Utils.color("")))
                                .reopenIfFail(true)
                                .response((signPlayer, strings) -> {
                                    String response = StringUtils.join(Arrays.copyOfRange(strings, 0, strings.length), ' ').trim();
                                    if (response.isEmpty()) {
                                        return false;
                                    }
                                    if(templates.get(player) != null){
                                        for(BuildTemplate template: templates.get(player)) {
                                            if (template.getName().equalsIgnoreCase(response)) {
                                                player.sendMessage("A build template with this name already exists! Templates must have different names.");
                                                this.spigotCallback.clearCallbacks(player);
                                                return true;
                                            }
                                        }
                                    }
                                        this.plugin.getQueryer().storeNewBuildInteraction(new Interaction(plugin, player, "Start Template"), -1, id -> {
                                            BuildTemplate template = new BuildTemplate(plugin, player, response, new Timestamp(System.currentTimeMillis()), null);
                                            plugin.addTemplate(player, template);
                                            plugin.addInProgressTemplate(player, this);
                                            player.sendMessage("You just started a build template called " + template.getName());
                                            this.makingTemplate = true;
                                        });
                                    this.spigotCallback.clearCallbacks(player);
                                    return true;
                                })
                                .open(p)
            );
        } else {
            sendComponent(
                    player,
                    "&8" + BULLET + "&f&nI want to finish my template!",
                    "&aClick here to finish my build template!",
                    p -> {
                            plugin.removeInProgressTemplate(player);
                            for(BuildTemplate template: templates.get(player)){
                                if(template.getEndTime() == null){
                                    template.setEndTime(new Timestamp(System.currentTimeMillis()));
                                    this.plugin.getQueryer().storeNewTemplate(template, buildId -> {
                                        this.plugin.getQueryer().storeNewBuildInteraction(new Interaction(plugin, player, "Finish Template"), buildId, id -> {
                                        template.setID(buildId);
                                        player.sendMessage("You just created a build template called " + template.getName());
                                        });
                                    });
                                    break;
                                }
                            }
                            this.makingTemplate = false;
                        this.spigotCallback.clearCallbacks(player);
                    });
            sendComponent(
                    player,
                    "&8" + BULLET + "&f&nI want to cancel my template!",
                    "&aClick here to cancel my template!",
                    p -> {
                        this.plugin.getQueryer().storeNewBuildInteraction(new Interaction(plugin, player, "Cancel Template"), -1, id -> {
                            plugin.removeInProgressTemplate(player);
                            for(int k = 0; k < templates.get(player).size(); k++){
                                BuildTemplate template = templates.get(player).get(k);
                                if(template.getEndTime() == null){
                                    templates.get(player).remove(k);
                                    player.sendMessage("You just canceled a build template called " + template.getName());
                                    break;
                                }
                            }
                            this.makingTemplate = false;
                            this.spigotCallback.clearCallbacks(player);
                        });

                    });
        }

        if(templates.get(player) != null){
            List<BuildTemplate> builds = templates.get(player);
            List<BuildTemplate> finishedBuilds = new ArrayList<>();
            for (BuildTemplate build : builds) {
                if(build.getEndTime() != null) {
                    finishedBuilds.add(build);
                }
            }
            if(finishedBuilds.size() > 0) {
                sendComponent(
                        player,
                        "&8" + BULLET + "&f&nI want my agent to build something!",
                        "&aClick here to have your agent build something!",
                        p -> {
                            this.spigotCallback.clearCallbacks(player);
                            Utils.msgNoPrefix(player, "&lClick the template you want to build:", "");
                            for (BuildTemplate finished : finishedBuilds) {
                                sendComponent(
                                        player,
                                        "&8" + BULLET + " &r" + finished.getName(),
                                        "&aClick here to select \"&r" + finished.getName() + "&a\"",
                                        l -> {
                                            this.plugin.getQueryer().storeNewBuildInteraction(new Interaction(plugin, player, "Build Template"), finished.getID(), id -> {
                                                finished.build();
                                                this.spigotCallback.clearCallbacks(player);
                                            });
                                        });
                            }
                        });
            }
        }
    }
    private void sendComponent(Player player, String text, String hoverText, Consumer<Player> onClick) {
        player.spigot().sendMessage(createComponent(text, hoverText, onClick));
    }

    private TextComponent createComponent(String text, String hoverText, Consumer<Player> onClick) {
        TextComponent message = new TextComponent(Utils.color(text));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.color(hoverText)).create()));
        addCallback(message, this.player.getUniqueId(), onClick);
        return message;
    }

    private void addCallback(TextComponent component, UUID playerUUID, Consumer<Player> onClick) {
        this.spigotCallback.createCommand(playerUUID, component, onClick);
    }
}
