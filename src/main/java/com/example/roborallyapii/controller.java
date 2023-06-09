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

        String fileName = "src/main/resources/templates/" + id ;

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



    /***
     * Getting a list of all the files in the resource folder
     * Based on a solution found on Stackoverflow (https://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder)
     * used together with a solution from  Baeldung (https://www.baeldung.com/java-filename-without-extension)
     */
    @GetMapping("/sendList/{folder}")
    public ResponseEntity<String> sendList(@PathVariable String folder) {

        List<String> gameFiles = new ArrayList<>();

        File resources = new File("src/main/resources/"+folder);
        File[] listOfFiles = resources.listFiles();
        for(int i = 0 ; i < listOfFiles.length ; i++) {
            if (listOfFiles[i].isFile()) {
                String filename = listOfFiles[i].getName();
                gameFiles.add(filename);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(String.join(",", gameFiles));
    }




    // initialize game
    @GetMapping("/new/{players}/{boardNum}")
    public ResponseEntity<String> newGame (@PathVariable int players, @PathVariable String boardNum) {
        System.out.println(players + "  players" + " , board  " + boardNum);
        game = new Game(players, boardNum);
        String filePath = "src/main/resources/boardOptions/"+ boardNum;
        try {
            String fileContent = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.OK).body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //add players and board number into game to create a game

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
    }

    // send gameID
    @GetMapping("/gameID")
    public ResponseEntity<String> gameID() {
        try {
            int GameId = game.getGameId();
            return (ResponseEntity.status(HttpStatus.OK).body(String.valueOf(GameId)));
        } catch (NullPointerException e) // if game is null
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game is null");
        }

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