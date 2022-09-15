package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception_handling.IncorrectDataException;
import com.game.exception_handling.NotFoundException;
import com.game.service.PlayerCriteria;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<Player>> getPlayersList(
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "title") String title,
            @RequestParam(required = false, name = "race") Race race,
            @RequestParam(required = false, name = "profession") Profession profession,
            @RequestParam(required = false, name = "after") Long after,
            @RequestParam(required = false, name = "before") Long before,
            @RequestParam(required = false, name = "banned") Boolean banned,
            @RequestParam(required = false, name = "minExperience") Integer minExperience,
            @RequestParam(required = false, name = "maxExperience") Integer maxExperience,
            @RequestParam(required = false, name = "minLevel") Integer minLevel,
            @RequestParam(required = false, name = "maxLevel") Integer maxLevel,
            @RequestParam(required = false, name = "order") PlayerOrder order,
            @RequestParam(required = false, name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(required = false, name = "pageSize", defaultValue = "3") int pageSize
    ) {

        PlayerCriteria criteria = new PlayerCriteria(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize);

        return ResponseEntity.ok(playerService.findAll(criteria));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getPlayersCount(
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "title") String title,
            @RequestParam(required = false, name = "race") Race race,
            @RequestParam(required = false, name = "profession") Profession profession,
            @RequestParam(required = false, name = "after") Long after,
            @RequestParam(required = false, name = "before") Long before,
            @RequestParam(required = false, name = "banned") Boolean banned,
            @RequestParam(required = false, name = "minExperience") Integer minExperience,
            @RequestParam(required = false, name = "maxExperience") Integer maxExperience,
            @RequestParam(required = false, name = "minLevel") Integer minLevel,
            @RequestParam(required = false, name = "maxLevel") Integer maxLevel
    ) {

        PlayerCriteria criteria = new PlayerCriteria(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel);

        return ResponseEntity.ok(playerService.findAll(criteria).size());
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player){

        if (player.getName() == null ||
                player.getTitle() == null ||
                player.getRace() == null ||
                player.getProfession() == null ||
                player.getBirthday() == null ||
                player.getExperience() == null
        ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (player.getBanned() == null)
            player.setBanned(false);

        validatePlayerData(player);

        return ResponseEntity.ok(playerService.save(player));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable String id) {

        long numId = validateAndConvertId(id);

        Optional<Player> optionalPlayer = playerService.findById(numId);

        if (!optionalPlayer.isPresent())
            throw new NotFoundException("Player with id=" + id + " not found");

        return ResponseEntity.ok(optionalPlayer.get());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable String id, @RequestBody Player playerData){

        long numId = validateAndConvertId(id);

        Optional<Player> optionalPlayer = playerService.findById(numId);

        if (!optionalPlayer.isPresent())
            throw new NotFoundException("Player with id=" + id + " not found");

        Player player = optionalPlayer.get();

        if (playerData.getName() != null)
            player.setName(playerData.getName());

        if (playerData.getTitle() != null)
            player.setTitle(playerData.getTitle());

        if (playerData.getRace() != null)
            player.setRace(playerData.getRace());

        if (playerData.getProfession() != null)
            player.setProfession(playerData.getProfession());

        if (playerData.getBirthday() != null)
            player.setBirthday(playerData.getBirthday());

        if (playerData.getBanned() != null)
            player.setBanned(playerData.getBanned());

        if (playerData.getExperience() != null)
            player.setExperience(playerData.getExperience());

        validatePlayerData(player);

        return ResponseEntity.ok(playerService.save(player));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity.BodyBuilder deletePlayer(@PathVariable String id) {

        long numId = validateAndConvertId(id);

        Optional<Player> optionalPlayer = playerService.findById(numId);

        if (!optionalPlayer.isPresent())
            throw new NotFoundException("Player with id=" + id + " not found");

        playerService.delete(optionalPlayer.get());

        return ResponseEntity.ok();
    }

    private long validateAndConvertId(String id) throws IncorrectDataException{
        if (StringUtils.isEmpty(id))
            throw new IncorrectDataException("Id is empty");

        long numId;
        try {
            numId = Long.parseLong(id);
        } catch (NumberFormatException exception) {
            throw new IncorrectDataException("Id is not correct");
        }

        if (numId <= 0)
            throw new IncorrectDataException("Id <= 0");

        return numId;
    }

    private void validatePlayerData(Player player) {
        if (player.getName().length() > 12 ||
                player.getTitle().length() > 30 ||
                player.getName().isEmpty() ||
                player.getExperience() < 0 ||
                player.getExperience() > 10_000_000 ||
                player.getBirthday().getYear()+1900 < 2000 ||
                player.getBirthday().getYear()+1900 > 3000
        ) {
            throw new IncorrectDataException("Incorrect data");
        }
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(IncorrectDataException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(NotFoundException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

}
