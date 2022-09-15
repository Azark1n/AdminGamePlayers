package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository repository;

    public PlayerService(PlayerRepository playerRepository) {
        this.repository = playerRepository;
    }

    public Optional<Player> findById(Long id) {
        return repository.findById(id);
    }

    public List<Player> findAll(PlayerCriteria criteria) {
        return repository.findAll((Specification<Player>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            processCriteriaSpecifications(root, criteriaBuilder, predicates, criteria);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, paging(criteria)).getContent();
    }

    private Pageable paging(PlayerCriteria criteria) {

        Pageable paging = Pageable.unpaged();

        if (!((criteria.getPageNumber() == null || criteria.getPageSize() == null) || (criteria.getPageNumber() < 0 || criteria.getPageSize() <= 0))) {
            if (criteria.getOrder() != null) {
                paging = PageRequest.of(criteria.getPageNumber(), criteria.getPageSize(), Sort.by(criteria.getOrder().getFieldName()));
            } else {
                paging = PageRequest.of(criteria.getPageNumber(), criteria.getPageSize());
            }
        }

        return paging;
    }

    private void processCriteriaSpecifications(Root<Player> root, CriteriaBuilder cb, List<Predicate> predicates, PlayerCriteria criteria) {

        if (!StringUtils.isEmpty(criteria.getName())) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.getName() + "%"));
        }

        if (!StringUtils.isEmpty(criteria.getTitle())) {
            predicates.add(cb.like(cb.lower(root.get("title")), "%" + criteria.getTitle() + "%"));
        }

        if (criteria.getRace() != null) {
            predicates.add(cb.equal(root.get("race"), criteria.getRace()));
        }

        if (criteria.getProfession() != null) {
            predicates.add(cb.equal(root.get("profession"), criteria.getProfession()));
        }

        if (criteria.getAfter() != null || criteria.getBefore() != null) {
            if (criteria.getAfter() != null && criteria.getBefore() != null) {
                predicates.add(cb.between(root.get("birthday"), new Date(criteria.getAfter()), new Date(criteria.getBefore())));
            } else if (criteria.getAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("birthday"), new Date(criteria.getAfter())));
            } else {
                predicates.add(cb.lessThanOrEqualTo(root.get("birthday"), new Date(criteria.getBefore())));
            }
        }

        if (criteria.getBanned() != null) {
            predicates.add(cb.equal(root.get("banned"), criteria.getBanned()));
        }

        if (criteria.getMinExperience() != null || criteria.getMaxExperience() != null) {
            if (criteria.getMinExperience() != null && criteria.getMaxExperience() != null) {
                predicates.add(cb.between(root.get("experience"), criteria.getMinExperience(), criteria.getMaxExperience()));
            } else if (criteria.getMinExperience() != null) {
                predicates.add(cb.ge(root.get("experience"), criteria.getMinExperience()));
            } else {
                predicates.add(cb.le(root.get("experience"), criteria.getMaxExperience()));
            }
        }

        if (criteria.getMinLevel() != null || criteria.getMaxLevel() != null) {
            if (criteria.getMinLevel() != null && criteria.getMaxLevel() != null) {
                predicates.add(cb.between(root.get("level"), criteria.getMinLevel(), criteria.getMaxLevel()));
            } else if (criteria.getMinLevel() != null) {
                predicates.add(cb.ge(root.get("level"), criteria.getMinLevel()));
            } else {
                predicates.add(cb.le(root.get("level"), criteria.getMaxLevel()));
            }
        }
    }

    public Player save(Player player){
        player.setLevel((int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100));
        player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());
        return repository.save(player);
    }

    public void delete(Player player){
        repository.delete(player);
    }
}
