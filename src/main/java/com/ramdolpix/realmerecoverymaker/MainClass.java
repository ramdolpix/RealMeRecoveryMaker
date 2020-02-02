package com.ramdolpix.realmerecoverymaker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

    public class MainClass extends Application {
        @Override
        public void start(Stage primaryStage) throws IOException {
            Parent root = FXMLLoader.load(getClass().getResource("/mainWindow.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setResizable(false);
            primaryStage.setTitle("RealMeRecoveryMaker");
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setOnHiding(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                ActionsClass.deleteFolderAndItsContent(Controller.tempFolder);
                                System.out.println(Controller.tempFolder + " folder was deleted!");

                            } catch (IOException e) {
                                System.out.println(Controller.tempFolder + " already deleted or can't delete folder.Delete yourself :D");
                            }

                        }
                    });
                }
            });
        }
        public static void main(String[] args) {
            launch(args);
        }
    }

