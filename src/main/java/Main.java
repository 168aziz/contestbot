import notification.PingTask;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Timer;

public class Main {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new ContestBot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

        Timer timer = new Timer("Timer");
        PingTask task = new PingTask();
        long delay = 1000L;
        long period = 1000L * 20L * 60L;
        timer.scheduleAtFixedRate(task, delay, period);
    }
}
