package repository;

import model.Contest;
import model.ContestList;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ContestRepository {
    //TODO разные лист в мапе
    // TODO исправить время
    private static ContestRepository contestRepository;

    private ZonedDateTime dateTime;
    private Map<String, Set<Contest>> contestMap;

    private ContestRepository() {
        contestMap = new ConcurrentHashMap<>();
    }

    public synchronized static ContestRepository getInstance() {
        if (contestRepository == null) {
            contestRepository = new ContestRepository();
        }
        return contestRepository;
    }


    public void add(Contest contest) {
        Set<Contest> contestSet = contestMap.get(contest.getEvent().getName());
        if (contestSet == null) contestSet = new CopyOnWriteArraySet<>();
        contestSet.add(contest);
        contestMap.put(contest.getEvent().getName(), contestSet);
    }

    public Set<Contest> get(ContestList contest) {
        String name = contest.getName();
        Set<Contest> contestSet = contestMap.get(name);
        if (contestSet == null) contestMap.put(name, new CopyOnWriteArraySet<>());
        return contestMap.get(name);
    }

    public boolean isEmpty(ContestList contest) {
        String name = contest.getName();
        Set<Contest> contestSet = contestMap.get(name);
        if (contestSet == null) contestMap.put(name, new CopyOnWriteArraySet<>());
        return contestMap.get(name).isEmpty();
    }

    public void remove(String contest) {
        contestMap.remove(contest);
    }


}
