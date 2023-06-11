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

import static java.lang.Integer.parseInt;

@RestController
public class controller {

    private int players;
    private String board;

    public int newGame;

    public int join;

    int j = 0;
    public Game game;
    List<Game> availableGames =new ArrayList<Game>();
    List<Game> gamesInProgress = new ArrayList<>();

    // load game
  /*  @GetMapping("/loadGame/{id}")
    public ResponseEntity<String> loadGame(@PathVariable String id) {

        String fileName = "src/main/resources/templates/" + id ;

        try {
            String fileContent = Files.readString(Paths.get(fileName), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.OK).body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
    }*/

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
        Game game = new Game(players, boardNum);
        availableGames.add(game);
        int gameId = game.getGameId();
        return ResponseEntity.status(HttpStatus.OK).body(String.valueOf(gameId));

    }

    // initialize game
    @GetMapping("/sendBoard/{gameId}/{folder}")
    public ResponseEntity<String> sendBoard (@PathVariable String gameId, @PathVariable String folder) {
        Game game = findGame(parseInt(gameId));
        String filePath = "src/main/resources/" + folder + "/" + game.boardOption + ".json";
        try {
            String fileContent = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.OK).body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
    }

    private Game findGame(int gameId){
        for(Game g : gamesInProgress){
            if(gameId == g.gameId){
                return g;
            }
        }
        System.out.println("Game not found.");
        return null;
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

    @GetMapping("/availableGames")
    public ResponseEntity<String> availablegames(){
        List<String> ids = new ArrayList();
        for (int i = 0; i < availableGames.size(); i++) {
            Game game = availableGames.get(i);
            ids.add(Integer.toString(game.gameId));
        }
        return ResponseEntity.status(HttpStatus.OK).body(String.join(",",ids));
    }
    @GetMapping("/gameFull/{gameId}")
    public ResponseEntity<String> gameFull(@PathVariable int gameId){
        String gameIsFull = "false";
        for(Game game : gamesInProgress){
            if (gameId == game.gameId){
                if(game.gameIsFull())
                    gameIsFull = "true";
            }
        }  return (ResponseEntity.status(HttpStatus.OK).body(gameIsFull));
    }

    @GetMapping("/join/{gameId}")
    public ResponseEntity<String> joinGame(@PathVariable int gameId) {
        int playerNum;
        if (availableGames != null) {
            for (int i = 0; i < availableGames.size(); i++) {
                Game game = availableGames.get(i);
                if (gameId == game.gameId) {
                    playerNum = game.joinCounter();
                    if (playerNum == game.numPlayers) {
                        availableGames.remove(game);
                        gamesInProgress.add(game);
                    }
                    if(playerNum != -1){
                        System.out.println(playerNum + "," + game.numPlayers);
                        String bodyString = playerNum + "," + game.numPlayers;
                        return (ResponseEntity.status(HttpStatus.OK).body(bodyString));
                    }
                }

                // )

            }
        }
        System.out.println("Program goes to bad request return statement.");
        return (ResponseEntity.status(HttpStatus.BAD_REQUEST).body("join response -1"));
    }

    @GetMapping("/playerCount/{gameId}")
    public ResponseEntity<String> playersCount(@PathVariable int gameId){

        if (availableGames != null) {
            for (int i = 0; i < availableGames.size(); i++) {
                Game game = availableGames.get(i);
                if (gameId == game.gameId) {
                    int plyerCount = game.getJoinedPlayers();
                    return (ResponseEntity.status(HttpStatus.OK).body(String.valueOf(plyerCount)));
                }
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(("Players must be joined."));
    }

}