import com.mewna.catnip.Catnip;
import org.quartz.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimerTask;
import java.util.function.Consumer;

public class MyTask implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SchedulerContext schedulerContext = null;
        try {
            schedulerContext = context.getScheduler().getContext();
        } catch (SchedulerException e1) {
            e1.printStackTrace();
        }
        final Catnip catnip = (Catnip) schedulerContext.get("catnip");
        System.out.println("Job1 --->>> Hello geeks! Time is " + new Date());
    }
}
