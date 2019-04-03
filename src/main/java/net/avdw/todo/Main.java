package net.avdw.todo;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args){
        CommandLine.run(new Todo(), args);
    }
}
