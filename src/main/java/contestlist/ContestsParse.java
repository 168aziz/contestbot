package contestlist;

import model.Contest;
import model.ContestList;
import org.json.JSONArray;
import org.json.JSONObject;
import repository.ContestRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;
import static java.time.format.DateTimeFormatter.ofPattern;
import static model.ContestList.ALL;

public class ContestsParse {

    //    private static final String API_KEY = "dd61c83b18529ddc2fbd34c4db975b8024f812f7";
//    private static final String USERNAME = "aziz_168";
    private static final DateTimeFormatter dtformatter = ofPattern("yyyy-MM-dd kk:mm:ss");
    private static final ZoneId tashkentId = ZoneId.of("Asia/Tashkent");
    private static LocalDateTime start;
    private static LocalDateTime start_today;

    private ContestsParse() {

    }

    public static Set<Contest> get(ContestList contestName) {
        ContestRepository repository = ContestRepository.getInstance();
        LocalDateTime current_time = LocalDateTime.now();
        boolean isEmpty = repository.isEmpty(contestName);
        if (start == null || isEmpty || start.plusMinutes(5).isAfter(current_time)) {
            addContest(repository, contestName);
        }
        return repository.get(contestName);

    }

    private static void addContest(ContestRepository repository, ContestList contestName) {
        start = LocalDateTime.now();
        repository.remove(contestName.getName());
        Optional<String> req = read(contestName);
        req.ifPresent(str -> {
            if (contestName == ALL) {
                new JSONArray(str).forEach(o -> {
                    Contest contest = getContest(valueOf(o), contestName);
                    if (contest.isIn_24_hours())
                        repository.add(contest);
                });
            } else {
                new JSONArray(str).forEach(o -> {
                    Contest contest = getContest(valueOf(o), contestName);
                    repository.add(contest);
                });
            }
        });
    }


    private static Contest getContest(String o, ContestList contestName) {
        JSONObject obj = new JSONObject(o);
        Contest contest = new Contest();
        contest.setName(obj.getString("name"));
        contest.setUrl(obj.getString("url"));
        contest.setEvent(contestName);
        contest.setIn_24_hours(obj.getString("in_24_hours").equals("Yes"));
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.from(dtformatter.parse(obj.getString("start_time").substring(0, 19).replace('T', ' '))), ZoneId.of("UTC+00:00")).toOffsetDateTime().atZoneSameInstant(tashkentId);
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.from(dtformatter.parse(obj.getString("end_time").substring(0, 19).replace('T', ' '))), ZoneId.of("UTC+00:00")).toOffsetDateTime().atZoneSameInstant(tashkentId);
        contest.setStart(start);
        contest.setEnd(end);
        return contest;
    }


    private static Optional<String> read(ContestList contestType) {
        try {
            String name = contestType.getName();
            URL url = new URL("https://kontests.net/api/v1/" + name);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String str = reader.lines().collect(Collectors.joining());
                return Optional.ofNullable(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
