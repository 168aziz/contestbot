package model;

public enum ContestList {

    HACKERRANK("hacker_rank"),
    CODEFORCES("codeforces"),
    CODECHEF("code_chef"),
    ATCODER("at_coder"),
    CS_ACADEMY("cs_academy"),
    HACKEREARTH("hacker_earth"),
    KICKSTART("kick_start"),
    LEETCODE("leet_code"),
    TOPCODER("top_coder"),
    ALL("all");
    private final String name;


    ContestList(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
