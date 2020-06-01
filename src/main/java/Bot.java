import Models.LOA;
import ch.qos.logback.core.hook.ShutdownHook;
import com.mewna.catnip.Catnip;
import com.mewna.catnip.entity.builder.EmbedBuilder;
import com.mewna.catnip.entity.guild.Guild;
import com.mewna.catnip.entity.guild.Member;
import com.mewna.catnip.entity.impl.channel.TextChannelImpl;
import com.mewna.catnip.entity.impl.guild.GuildImpl;
import com.mewna.catnip.entity.message.Embed;
import com.mewna.catnip.entity.message.Message;
import com.mewna.catnip.entity.message.MessageOptions;
import com.mewna.catnip.entity.misc.Emoji;
import com.mewna.catnip.shard.DiscordEvent;
import com.mewna.catnip.util.scheduler.TaskScheduler;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.quartz.*;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;

import javax.xml.crypto.Data;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

public class Bot {
    private static Dotenv dotenv = Dotenv.load();

    public static void main(String[] args) throws IOException {
        Webhook webhook = new Webhook("https://script.google.com/macros/s/AKfycbxXVD3EadkJEUkwVeLp8iLY49QuKhS0SR5pLlONmZUSZUNHq84/exec");
        MongoCollection<Document> collection = Database.database.getCollection("loa");
        try {
            JobDetail job1 = JobBuilder.newJob(MyTask.class)
                    .withIdentity("job1", "group1").build();
            Trigger trigger1 = TriggerBuilder.newTrigger()
                    .withIdentity("cronTrigger1", "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *"))
                    .build();
            Scheduler scheduler1 = new StdSchedulerFactory().getScheduler();
//            scheduler1.start();
            scheduler1.scheduleJob(job1, trigger1);
            final Catnip catnip = Catnip.catnip(dotenv.get("BOT_TOKEN", ""));
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyy");
            scheduler1.getContext().put("catnip", catnip);
            catnip.observable(DiscordEvent.MESSAGE_CREATE)
                    .filter(msg -> msg.content().startsWith("$"))
                    .subscribe(msg -> {
                        if ("ping".equals(msg.content().substring(1))) {
                            long start = System.currentTimeMillis();
                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.color(Color.BLUE);
                            embedBuilder.title("Pong :ping_pong:");
                            embedBuilder.timestamp(ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/Los_Angeles")));
                            msg.channel().sendMessage(embedBuilder.build())
                                    .subscribe(pong -> {
                                        long end = System.currentTimeMillis();
                                        EmbedBuilder $e4 = embedBuilder.description(String.format("Took %dms.", (end - start)));
                                        pong.edit($e4.build());
                                    });
                        } else if (msg.content().startsWith("loa", 1)) {
                            if (msg.content().contains("Filing LOA")) {
                                String[] org = msg.content().replace(" ", "").split("\\|");
                                String[] strings = Arrays.copyOfRange(org, 1, org.length);

                                String name = Objects.requireNonNull(Objects.requireNonNull(msg.guild()).member(msg.author().id())).effectiveName();
                                List<String> $n = new ArrayList<>(Arrays.asList(name.split("\"")));
                                $n = new ArrayList<>(Arrays.asList($n.get(0).split(" ")));
                                if ($n.get(0).length() != 2) $n.remove(0);
                                String $n2 = String.join(" ", $n);
                                Boolean sl = Objects.requireNonNull(Objects.requireNonNull(msg.guild()).member(msg.author().id())).orderedRoles().contains(Objects.requireNonNull(msg.guild()).role(710938869754888192L));
                                JSONObject response = webhook.getRequset($n2, sl).getBody().getObject();
                                System.out.println(response.toString());
                                try {
                                    String squadLead = null;
                                    String teamLead = null;
                                    try {
                                        squadLead = ((Member) msg.guild().members().findByNameContains(response.getString("co")).toArray()[0]).asMention();
                                    } catch (Exception e) {
                                        squadLead = response.getString("co");
                                    }
                                    try {
                                        teamLead = ((Member) msg.guild().members().findByNameContains(response.getString("xo")).toArray()[0]).asMention();
                                    } catch (Exception e) {
                                        teamLead = response.getString("xo");
                                    }
                                    MessageOptions messageOptions = new MessageOptions();
                                    messageOptions.content(squadLead + " " + teamLead);
                                    LOA loa = new LOA(msg.author().idAsLong(), format.parse(strings[0]), format.parse(strings[1]), strings[2]);
                                    try {
                                        loa.setNeedApprovedBy(Arrays.asList(((Member) msg.guild().members().findByNameContains(response.getString("co")).toArray()[0]).idAsLong(), ((Member) msg.guild().members().findByNameContains(response.getString("xo")).toArray()[0]).idAsLong()));
                                        loa.setApprovedBy(new ArrayList<>());

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        loa.setApprovedBy(new ArrayList<>());
                                        loa.setNeedApprovedBy(new ArrayList<>());
                                    }
                                    Database.createDocument(collection, loa);
                                    loa.setId(Database.searchCollection(collection, "userId", loa.getUserId()).tryNext().getObjectId("_id"));
                                    EmbedBuilder embedBuilder = new EmbedBuilder();
                                    embedBuilder.title(msg.author().effectiveName(msg.guild()) + " is filling LOA");
                                    embedBuilder.field("Start Date", format.format(loa.getStartDate()), true);
                                    embedBuilder.field("End Date", format.format(loa.getEndDate()), true);
                                    embedBuilder.field("Reason", loa.getReason(), false);
                                    embedBuilder.footer("ID: " + loa.getId().toHexString(), "");
                                    messageOptions.embed(embedBuilder.build());
                                    msg.guild().channel(710990688803749938L).asTextChannel().sendMessage(messageOptions)
                                            .subscribe(post -> {
                                                String temp = post.embeds().get(0).footer().text();
                                                System.out.println(temp);
                                                post.react("✅");
                                            });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    msg.channel().sendMessage("ERR you broke it");
                                }
                            } else if (msg.content().contains("Ending LOA")) {
                                msg.guild().channel(710990688803749938L).asTextChannel().sendMessage(msg.author().username() + " is ending an loa");
                            } else {
                                msg.channel().sendMessage("Err: Incorect Syntax");
                            }

                        } else if (msg.content().startsWith("test", 1)) {
                            msg.channel().sendMessage("Depreciated");
                        } else {
                            msg.channel().sendMessage("Unrecognized Command!");
                        }
                    }, Throwable::printStackTrace);
            catnip.observable(DiscordEvent.READY)
                    .subscribe(bot -> {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
                        LocalDateTime localDateTime = LocalDateTime.now();
                        System.out.println("Bot ready at " + dateTimeFormatter.format(localDateTime));
                    }, Throwable::printStackTrace);
            catnip.observable(DiscordEvent.MESSAGE_REACTION_ADD)
                    .subscribe(reactionUpdate -> {
//                        catnip.rest().channel().getReactions(reactionUpdate.channelId(), reactionUpdate.messageId(), reactionUpdate.emoji()).forEach(System.out::println);
                        catnip.rest().channel().getMessage(reactionUpdate.channelId(), reactionUpdate.messageId()).subscribe(message -> {
                            if (message.embeds().size() > 0) {
                                if (message.embeds().get(0).footer().text().contains("ID: ")) {
                                    ObjectId id = new ObjectId(message.embeds().get(0).footer().text().replace(" ", "").split(":")[1]);
                                    LOA loa = Database.getLOA(collection, id);
                                    System.out.println(loa.getApprovedBy());
                                    if (loa.getNeedApprovedBy().contains(Long.parseLong(reactionUpdate.userId()))) {
                                        loa.addAprrovedBy(Long.parseLong(reactionUpdate.userId()));
                                        Database.updateDocument(collection, loa, "_id", id);
                                        if (loa.getApprovedBy().size() == 2) {
                                            MessageOptions messageOptions = new MessageOptions();
                                            messageOptions.content("Your LOA has been approved");
                                            EmbedBuilder embedBuilder = new EmbedBuilder();
                                            embedBuilder.title(catnip.rest().user().getUser(String.valueOf(loa.getUserId())).blockingGet().effectiveName(catnip.rest().guild().getGuild(reactionUpdate.guildId()).blockingGet()) + " is filling LOA");
                                            embedBuilder.field("Start Date", format.format(loa.getStartDate()), true);
                                            embedBuilder.field("End Date", format.format(loa.getEndDate()), true);
                                            embedBuilder.field("Reason", loa.getReason(), false);
                                            embedBuilder.footer("ID: " + loa.getId().toHexString(), "");
                                            messageOptions.embed(embedBuilder.build());
                                            catnip.rest().user().createDM(String.valueOf(loa.getUserId())).blockingGet().sendMessage(messageOptions);
                                        }
                                    }
                                }
                            }
                        });
                    }, Throwable::printStackTrace);

            catnip.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
