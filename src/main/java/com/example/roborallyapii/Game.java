package com.example.roborallyapii;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Random;

public class Game {
    int joinedPlayers = 1;
    int numPlayers;
    String boardOption;
    int gameId;

    private boolean gameFull = false;

    public Game(int numPlayers, String boardOption) {
        this.numPlayers = numPlayers;
        this.boardOption = boardOption;

        Random random = new Random();

        gameId = random.nextInt(200);

        System.out.println(numPlayers + " players" + "," + " board " + boardOption + "," + " GameID " + gameId);
    }

    public int getGameId() {
        return gameId;
    }

    public int joinCounter() {
        if (joinedPlayers>=numPlayers){
            gameFull = true;
            return (-1);
        }
        return ++joinedPlayers;

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





}