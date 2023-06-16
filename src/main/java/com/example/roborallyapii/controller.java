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


    // CREATE A GAME OBJECT
    @GetMapping("/createGame/{players}/{boardName}/{loadExisting}")
    public ResponseEntity<String> newGame (@PathVariable int players, @PathVariable String boardName, @PathVariable boolean loadExisting) {
        System.out.println(players + "  players" + " , board  " + boardName);
        Game game = new Game(players, boardName, loadExisting);
        availableGames.add(game);
        int gameId = game.getGameId();
        return ResponseEntity.status(HttpStatus.OK).body(String.valueOf(gameId));

    }

    // SEND JSON STRING FOR A NEW BOARD
    @GetMapping("/newBoard/{gameId}")
    public ResponseEntity<String> newBoard (@PathVariable String gameId) {
        for(Game g : availableGames){
            System.out.println("AVailableGame: " + g.getGameId());
        }
        Game game = findGameInProgress(parseInt(gameId));
        String filePath = "src/main/resources/boardOptions/" + game.boardOption;
        return getFileContent(filePath);
    }

    private Game findAvailableGame(int gameId){
        for(Game g : availableGames){
            if(gameId == g.gameId){
                return g;
            }
        }
        System.out.println("Game not found.");
        return null;
    }

    private Game findGameInProgress(int gameId){
        for(Game g : gamesInProgress){
            if(gameId == g.gameId){
                return g;
            }
        }
        System.out.println("Game not found.");
        return null;
    }

    // SEND JSON STRING FOR AN EXISTING BOARD WITH PLAYERS AND GAME STATE
    @GetMapping("/existingBoard/{gameId}/{loadOrUpdate}")
    public ResponseEntity<String> existingBoard(@PathVariable int gameId, @PathVariable String loadOrUpdate){
        String filePath;
        if(loadOrUpdate.equals("load")){
            String boardName = findGame(gameId).getBoardOption();
            filePath = "src/main/resources/templates/" + boardName;
        } else {
            filePath = "src/main/resources/templates/" + gameId;
        }
        return getFileContent(filePath);
    }

    private ResponseEntity<String> getFileContent(String path){
        try {
            String fileContent = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.OK).body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @GetMapping("/availableGames")
    public ResponseEntity<String> availableGames(){
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

    @GetMapping("/getLoadExisting/{gameId}")
        public ResponseEntity<String> getLoadExisting(@PathVariable int gameId){
            Game game = findAvailableGame(gameId);
            String isExisting;
            if(game.getLoadExisting())
                isExisting = "true";
            else
                isExisting = "false";
            return (ResponseEntity.status(HttpStatus.OK).body(isExisting));
        }




    @GetMapping("/playerCount/{gameId}")
    public ResponseEntity<String> playersCount(@PathVariable int gameId){

        for (int i = 0; i < availableGames.size(); i++) {
            Game game = availableGames.get(i);
            if (gameId == game.gameId) {
                int plyerCount = game.getJoinedPlayers();
                return (ResponseEntity.status(HttpStatus.OK).body(String.valueOf(plyerCount)));
            }
        }

        for (int i = 0; i < gamesInProgress.size(); i++) {
            Game game = gamesInProgress.get(i);
            if (gameId == game.gameId) {
                int plyerCount = game.getJoinedPlayers();
                return (ResponseEntity.status(HttpStatus.OK).body(String.valueOf(plyerCount)));
            }
        }



        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(("Players must be joined."));
    }

    private Game findGame(int gameId){
        Game game;
        for (int i = 0; i < availableGames.size(); i++) {
            game = availableGames.get(i);
            if (gameId == game.gameId) {
                return game;
            }
        }
        for (int i = 0; i < gamesInProgress.size(); i++) {
            game = gamesInProgress.get(i);
            if (gameId == game.gameId) {
                return game;
            }
        }
        return null;
    }
    @DeleteMapping("/deleteGame/{gameId}")
    public ResponseEntity<String> deleteGame(@PathVariable int gameId) {

        String filePath = "src/main/resources/templates/" + gameId;
        System.out.println("Delete file path : " + filePath);
        File file = new File(filePath);

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File has been deleted byyyye.");
                return (ResponseEntity.status(HttpStatus.OK).body("File deleted."));
            } else {
                System.out.println("File failed to delete");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File failed to delete");
            }
        } else {
            System.out.println("File not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        }
    }


    @PostMapping("/programmingPhaseComplete/{gameId}")
    public ResponseEntity<String> programmingPhaseComplete(@PathVariable int gameId){
        for (int i = 0; i < gamesInProgress.size(); i++) {
            Game game = gamesInProgress.get(i);
            if (gameId == game.gameId) {
                game.programmedCounter();
                if (game.programmedPlayers == game.numPlayers)
                    game.executedPlayers = 0;
                return (ResponseEntity.status(HttpStatus.OK).body(String.valueOf("ok")));
            }
        }
        return (ResponseEntity.status(HttpStatus.BAD_REQUEST).body(""));
    }

    @GetMapping("/allReady/{gameId}")
    public ResponseEntity<String> allReady(@PathVariable int gameId){
        for (int i = 0; i < gamesInProgress.size(); i++) {
            Game game = gamesInProgress.get(i);
            if (gameId == game.gameId) {
                boolean allProgrammed = game.allProgrammed();
                if (allProgrammed)
                    return (ResponseEntity.status(HttpStatus.OK).body(String.valueOf("true")));
                else
                    return (ResponseEntity.status(HttpStatus.OK).body(String.valueOf("false")));
            }
        }
        return (ResponseEntity.status(HttpStatus.BAD_REQUEST).body(""));
    }
    @GetMapping("/getTurn/{gameId}")
    public ResponseEntity<String> getTurn(@PathVariable int gameId){
        for (int i = 0; i < gamesInProgress.size(); i++) {
            Game game = gamesInProgress.get(i);
            if (gameId == game.gameId) {
                int executed = game.getExecuted();
                return (ResponseEntity.status(HttpStatus.OK).body(String.valueOf(executed)));
            }
        }
        return (ResponseEntity.status(HttpStatus.BAD_REQUEST).body(""));
    }

    @PostMapping("/executed/{gameId}")
    public ResponseEntity<String> execute(@PathVariable int gameId){
        for (int i = 0; i < gamesInProgress.size(); i++) {
            Game game = gamesInProgress.get(i);
            if (gameId == game.gameId) {
                game.executedCounter();
                if (game.executedPlayers == game.numPlayers) {
                    game.programmedPlayers = 0;
                }
                return (ResponseEntity.status(HttpStatus.OK).body(String.valueOf("ok")));
            }
        }
        return (ResponseEntity.status(HttpStatus.BAD_REQUEST).body(""));
    }
}