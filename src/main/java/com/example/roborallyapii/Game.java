package com.example.roborallyapii;

import java.util.Random;

public class Game {

    int numPlayers;
    String boardOption;
    int gameId;

    public Game(int numPlayers, String boardOption) {
        this.numPlayers = numPlayers;
        this.boardOption = boardOption;

        Random random = new Random();

        gameId = random.nextInt(200);

        System.out.println(numPlayers + " players" + "," + " board " + boardOption + "," + " GameID " + gameId);
    }

    public int  getGameId() {
        return gameId;
    }




}