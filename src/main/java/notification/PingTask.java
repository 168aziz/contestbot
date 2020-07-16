package notification;

import repository.ContestRepository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;

public class PingTask extends TimerTask {

    @Override
    public void run() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            httpURLConnection.disconnect();
            ContestRepository.getInstance().removeAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
