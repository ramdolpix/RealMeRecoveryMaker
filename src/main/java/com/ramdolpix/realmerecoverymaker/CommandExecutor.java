package com.ramdolpix.realmerecoverymaker;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandExecutor extends Task<Void> {
    private final String commandString;
    private final TextArea outputArea;
    private final ProcessBuilder processBuilder;

    public CommandExecutor(
            String commandString,
            TextArea outputArea
    ) {
        this.commandString = commandString;
        this.outputArea = outputArea;

        processBuilder = new ProcessBuilder(
                commandString.trim().split("\\s+")
        )
                .redirectErrorStream(true);

        exceptionProperty().addListener((observable, oldException, newException) -> {
            if (newException != null) {
                newException.printStackTrace();
            }
        });
    }

    @Override
    protected Void call() throws IOException, InterruptedException {
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
        {
            String line;
            while ((line = reader.readLine()) != null) {
                final String nextLine = line;
                Platform.runLater(() -> outputArea.appendText(nextLine + "\n"));
//                process.waitFor();
            }
        }

        return null;
    }
}
