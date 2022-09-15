package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Profession;
import com.game.entity.Race;

public class PlayerCriteria {

    protected String name;
    protected String title;
    protected Race race;
    protected Profession profession;
    protected Long after;
    protected Long before;
    protected Boolean banned;
    protected Integer minExperience;
    protected Integer maxExperience;
    protected Integer minLevel;
    protected Integer maxLevel;

    protected Integer pageNumber;
    protected Integer pageSize;

    protected PlayerOrder order;

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public Race getRace() {
        return race;
    }

    public Profession getProfession() {
        return profession;
    }

    public Long getAfter() {
        return after;
    }

    public Long getBefore() {
        return before;
    }

    public Boolean getBanned() {
        return banned;
    }

    public Integer getMinExperience() {
        return minExperience;
    }

    public Integer getMaxExperience() {
        return maxExperience;
    }

    public Integer getMinLevel() {
        return minLevel;
    }

    public Integer getMaxLevel() {
        return maxLevel;
    }


    public PlayerOrder getOrder() {
        return order;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public PlayerCriteria(String name, String title, Race race, Profession profession, Long after, Long before,
                          Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                          Integer maxLevel) {
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.after = after;
        this.before = before;
        this.banned = banned;
        this.minExperience = minExperience;
        this.maxExperience = maxExperience;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;

    }

    public PlayerCriteria(String name, String title, Race race, Profession profession, Long after, Long before,
                          Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                          Integer maxLevel, PlayerOrder order, Integer pageNumber, Integer pageSize) {

        this(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel);

        this.order = order == null ? PlayerOrder.ID : order;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
}