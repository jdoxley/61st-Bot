import ch.qos.logback.core.hook.ShutdownHook;
import com.mewna.catnip.Catnip;
import com.mewna.catnip.entity.builder.EmbedBuilder;
import com.mewna.catnip.entity.guild.Guild;
import com.mewna.catnip.entity.impl.channel.TextChannelImpl;
import com.mewna.catnip.entity.impl.guild.GuildImpl;
import com.mewna.catnip.entity.misc.Emoji;
import com.mewna.catnip.shard.DiscordEvent;
import com.mewna.catnip.util.scheduler.TaskScheduler;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
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
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

public class Bot {

    public static void main(String[] args) throws IOException {
        Webhook webhook = new Webhook();
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

                        } else {
                            msg.channel().sendMessage("Unrecognized Command!");
                        }
                    });
            catnip.observable(DiscordEvent.READY)
                    .subscribe(bot -> {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
                        LocalDateTime localDateTime = LocalDateTime.now();
                        System.out.println("Bot ready at " + dateTimeFormatter.format(localDateTime));
                    });
            catnip.observable(DiscordEvent.MESSAGE_REACTION_ADD)
                    .subscribe(reactionUpdate -> {
                        catnip.rest().channel().getReactions(reactionUpdate.channelId(),reactionUpdate.messageId(),reactionUpdate.emoji()).forEach(System.out::println);
                    });
            catnip.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
