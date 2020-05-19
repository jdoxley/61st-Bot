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
import com.mongodb.client.MongoDatabase;
import kong.unirest.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

public class Bot {

    public static void main(String[] args) throws IOException {
        Webhook webhook = new Webhook("https://script.google.com/macros/s/AKfycbxXVD3EadkJEUkwVeLp8iLY49QuKhS0SR5pLlONmZUSZUNHq84/exec");
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
            final Catnip catnip = Catnip.catnip("NzEwOTQxMDQxMzQzMjAxNDMy.Xr74Bw.rZRDWtAc7tK-arbhveEtXIStsoI");
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

                                msg.guild().channel(710990688803749938L).asTextChannel()
                                        .sendMessage(msg.author().username() + " is filing an loa, COC Please react to this post.")
                                        .subscribe(react -> react.react("✅"));

//                                    webhook.sendMessage("{'userId':"+msg.author().id()+"}");
                            } else if (msg.content().contains("Ending LOA")) {
                                msg.guild().channel(710990688803749938L).asTextChannel().sendMessage(msg.author().username() + " is ending an loa");
                            } else {
                                msg.channel().sendMessage("Err: Incorect Syntax");
                            }

                        } else if (msg.content().startsWith("test", 1)){
                            String name = Objects.requireNonNull(Objects.requireNonNull(msg.guild()).member(msg.author().id())).effectiveName();
                            List<String> $n = new ArrayList<>(Arrays.asList(name.split("\"")));
                            $n = new ArrayList<>(Arrays.asList($n.get(0).split(" ")));
                            if ($n.get(0).length()!=2) $n.remove(0);
                            String $n2 = String.join(" ",$n);
                            JSONObject response = webhook.getRequset($n2).getBody().getObject();
                            System.out.println(response.toString());
                            try{
                                Member squadLead =(Member) msg.guild().members().findByNameContains(response.getString("squadLead")).toArray()[0];
                                Member teamLead = (Member) msg.guild().members().findByNameContains(response.getString("teamLead")).toArray()[0];
                                MessageOptions messageOptions = new MessageOptions();
                                messageOptions.content(squadLead.asMention()+" "+teamLead.asMention());
                                EmbedBuilder embedBuilder = new EmbedBuilder();
                                embedBuilder.title(msg.author().effectiveName(msg.guild())+" is filling LOA");
                                embedBuilder.field("Start Date","5/17/2020",true);
                                embedBuilder.field("End Date", "5/20/2020",true);
                                embedBuilder.field("Reason", "Craft gay lmao", false);
                                embedBuilder.footer("ID: 1","");
                                messageOptions.embed(embedBuilder.build());
                                msg.guild().channel(710990688803749938L).asTextChannel().sendMessage(messageOptions)
                                        .subscribe(post -> {
                                            String temp = post.embeds().get(0).footer().text();
                                            System.out.println(temp);
                                        });
                            }catch (Exception e){
                                e.printStackTrace();
                                msg.channel().sendMessage("ERR you broke it");
                            }
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
                        catnip.rest().channel().getReactions(reactionUpdate.channelId(),reactionUpdate.messageId(),reactionUpdate.emoji()).forEach(System.out::println);
                    }, Throwable::printStackTrace);
            catnip.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
