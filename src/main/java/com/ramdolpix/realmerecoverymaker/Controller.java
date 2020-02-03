package com.ramdolpix.realmerecoverymaker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Controller {

    @FXML Button chooseOZIPButton;
    @FXML TextField pathToOZIP;
    @FXML TextArea logField;

    String path2OZipFile; // path to oZIP file
    String pathZipFile;   //path to ZIP file

    static Path tempFolder;      //tempFolder
    String unpackedFilesFolder;
    String unpackedSystemFolder;
    {
        try {
            ActionsClass action = new ActionsClass();
            tempFolder = Files.createTempDirectory("ozip"); //create tempFolder
            System.out.println("Temp folder created: " + tempFolder.toString());
            System.out.println("Unpacking tools...");
            action.getFileFromResource("tools/python/ozipdecrypt.py",tempFolder.toString() + "/ozipdecrypt.py");
            action.getFileFromResource("tools/python/ofp_libextract.py",tempFolder.toString() + "/ofp_libextract.py");
            action.getFileFromResource("tools/python/sdat2img.py",tempFolder.toString() + "/sdat2img.py");

            action.getFileFromResource("tools/ImgExtractor.exe",tempFolder.toString() + "/ImgExtractor.exe");
            action.getFileFromResource("tools/bsdiff.exe",tempFolder.toString() + "/bsdiff.exe");
            action.getFileFromResource("tools/bspatch.exe",tempFolder.toString() + "/bspatch.exe");
            action.getFileFromResource("tools/brotli.exe",tempFolder.toString() + "/brotli.exe");
            File folderWithFiles = new File(tempFolder.toString() + "/unpackedFiles");
            File unpackedSystem = new File(tempFolder.toString() + "/unpackedSystem");
            folderWithFiles.mkdir();
            unpackedSystem.mkdir();
            System.out.println("Tools was unpacked!");
            unpackedFilesFolder = folderWithFiles.toString();
            unpackedSystemFolder = unpackedSystem.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //create folder

    public void onClickChooseOZIP(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("oZIP Firmware Archive", "*.ozip"); //set file filter(shows only .ozip archives)
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(new Stage());    //create dialog with window,where you can choose ur file

        ActionsClass action = new ActionsClass();

        path2OZipFile = file.toString(); //copy path to ozip file to separate string
        pathZipFile = path2OZipFile.replace(".ozip", ".zip"); //here is changing .ozip extension to .zip extension in string

        pathToOZIP.setText(path2OZipFile);

        new Thread(){
            @Override
            public void run() {
                try {
                    logField.clear();
                    logField.appendText("Converting oZip to Zip..." + "\n");
                    logField.appendText(action.runCommand("python " + tempFolder.toString() + "/ozipdecrypt.py " + path2OZipFile));
                    logField.appendText("Moving .zip to Temp folder..." + "\n");
                    action.moveFile(pathZipFile,tempFolder.toString() + "/firm.zip");
                    logField.appendText("Done!" + "\n");
                    logField.appendText("Extracting system and boot from .zip...");
                    new ZipFile(tempFolder.toString() + "/firm.zip").extractFile("boot.img", unpackedFilesFolder);
                    new ZipFile(tempFolder.toString() + "/firm.zip").extractFile("system.new.dat.br", unpackedFilesFolder);
                    new ZipFile(tempFolder.toString() + "/firm.zip").extractFile("system.transfer.list", unpackedFilesFolder);
                    logField.appendText("Done!" + "\n");
                    logField.appendText("Converting dat.br to dat...");
                    logField.appendText(action.runCommand(tempFolder.toString() + "/brotli.exe --decompress --in "  + unpackedFilesFolder + "/system.new.dat.br" + " --out "+ unpackedFilesFolder +"/system.new.dat"));
                    logField.appendText("Done!" + "\n");
                    logField.appendText("Converting .dat to .img...");
                    logField.appendText(action.runCommand("python " + tempFolder.toString() + "/sdat2img.py " + unpackedFilesFolder + "/system.transfer.list " + unpackedFilesFolder + "/system.new.dat " + unpackedFilesFolder + "/system.img"));
                    logField.appendText("Unpacking system.img...");
                    logField.appendText(action.runCommand(tempFolder.toString() + "/ImgExtractor.exe " + unpackedFilesFolder + "/system.img "  + unpackedSystemFolder));
                    logField.appendText("Done!" + "\n");
                    logField.appendText("Moving recovery-from-boot.p...");
                    action.moveFile(unpackedSystemFolder + "/system/recovery-from-boot.p",unpackedFilesFolder + "/recovery-from-boot.p");
                    logField.appendText("Done!" + "\n");
                    logField.appendText("Starting making recovery..." + "\n");
                    logField.appendText(action.runCommand(tempFolder.toString() + "/bspatch.exe " + unpackedFilesFolder + "/boot.img "  + unpackedFilesFolder +"/recovery.img " + unpackedFilesFolder + "/recovery-from-boot.p"));
                    logField.appendText("Complete!" + "\n");
                    action.moveFile(unpackedFilesFolder + "/recovery.img",System.getProperty("user.home") + "/recovery.img");
                    logField.appendText("You can find your file here: " + System.getProperty("user.home"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //unpacking .ozip with python script(u must have installed python on ur computer and installed pycryptodome by pip3)(thanks to bkerler for script)


            }
        }.start();
    }
}


