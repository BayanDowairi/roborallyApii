package com.example.roborallyapii;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
public class controller {

    private int players;
    private String board;

    public int newGame;

    public int join;
    public Game game;

    // load game
    @GetMapping("/loadGame/{id}")
    public ResponseEntity<String> loadGame(@PathVariable String id) {

        String fileName = "src/main/resources/templates/" + id+ ".json" ;

        try {
            String fileContent = Files.readString(Paths.get(fileName), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.OK).body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {

        return ResponseEntity.status(HttpStatus.OK).body("serverisup");
    }

    //join game




    @GetMapping("/sendList")
    public ResponseEntity<String> sendList() {

        List<String> gameFiles = new ArrayList<>();

        File resources = new File("src/main/resources/templates/");
        File[] listOfFiles = resources.listFiles();
        for(int i = 0 ; i < listOfFiles.length ; i++) {
            if (listOfFiles[i].isFile()) {
                //String filename = Files.readString(listOfFiles[i].getName());
                String filename = listOfFiles[i].getName();
                gameFiles.add(filename);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(String.join(",", gameFiles));
    }




    // initialize game
    @PostMapping("/new/{players}/{board}")
    public ResponseEntity<String> newGame (@PathVariable String players, @PathVariable String board) {

        String fileName = "src/main/resources/boardOptions/"+board;

        try {
            String fileContent = Files.readString(Paths.get(fileName), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.OK).body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.players = Integer.parseInt(players);
        this.board = board;

        System.out.println(players + "  players" + " , board  " + board);

        //add players and board number into game to create a game
        Game Game = new Game(Integer.parseInt(players), Integer.parseInt(board));

        return ResponseEntity.status(HttpStatus.OK).body(board);
    }

    // send gameID
    @GetMapping("/gameID")

    public ResponseEntity<String> gameID() {
        Game game = new Game(6,8);
        int GameId = game.getGameId();
        return (ResponseEntity.status(HttpStatus.OK).body(String.valueOf(GameId)));
    }



    // save game
    @PostMapping("/saveGame/{id}")
    public void saveGame(@RequestBody String game, @PathVariable String id) {

        String filename = "src/main/resources/templates/"+ id;

        FileWriter fileWriter = null;
        try{
            fileWriter = new FileWriter(filename);
            fileWriter.write(game);
        } catch (IOException e1) {
            System.out.println(e1);
        }

        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e2) {}
        }

        System.out.println(game + id);
    }

}
