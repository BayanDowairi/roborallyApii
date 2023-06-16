package com.example.roborallyapii;

import java.util.Random;

public class Game {
    int joinedPlayers = 1;
    int programmedPlayers = 0;
    int executedPlayers = 0;

    int numPlayers;
    String boardOption;
    int gameId;

    private boolean gameFull = false;

    private boolean loadExisting;

    public Game(int numPlayers, String boardOption, boolean loadExisting) {
        this.numPlayers = numPlayers;
        this.boardOption = boardOption;
        this.loadExisting = loadExisting;
        Random random = new Random();

        gameId = random.nextInt(200);

        System.out.println(numPlayers + " players" + "," + " board " + boardOption + "," + " GameID " + gameId);
    }

    public int getGameId() {
        return gameId;
    }

    public int joinCounter() {
        if(joinedPlayers == numPlayers-1)
            gameFull = true;
        if (joinedPlayers > numPlayers){
            return (-1);
        }
        joinedPlayers++;
        System.out.println("joinedPlayers in joinCounter: " + joinedPlayers);
        return joinedPlayers;
    }

    public void programmedCounter(){
        programmedPlayers++;
    }

    public void executedCounter(){
        executedPlayers++;
    }

    public int getJoinedPlayers() {
        return joinedPlayers;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public String getBoardOption() {
        return boardOption;
    }


    public boolean gameIsFull(){
        return gameFull;
    }

    public boolean allProgrammed(){
        return programmedPlayers==numPlayers;
    }

    public int getExecuted(){
        return executedPlayers;
    }

    public boolean getLoadExisting() {
        return loadExisting;
    }
}
