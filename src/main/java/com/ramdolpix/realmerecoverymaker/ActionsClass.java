package com.ramdolpix.realmerecoverymaker;

import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

public class ActionsClass {

    public static void deleteFolderAndItsContent(final Path folder) throws IOException {
        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static File takeFileFromResourceToTmpFile(String resourcePath) {
        try {
            InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                return null;
            }

            File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".file");
            tempFile.deleteOnExit();


            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                //copy stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void getFileFromResource(String nameFile,String pathTo){
        File file = new File(String.valueOf(takeFileFromResourceToTmpFile(nameFile)));
        file.renameTo(new File(pathTo));
        file.delete();
    }

    public static String runCommand(String cmd) throws IOException {
        Process proc = Runtime.getRuntime().exec(cmd);
        InputStream is = proc.getInputStream();
        Scanner s = new Scanner(is).useDelimiter("\\A");
        String val = "";
        if (s.hasNext()) {
            val = s.next();
        } else {
            val = "";
        }
        return val;
    }

    public static void moveFile(String from,String to){
        try{
            File justFile=new File(from);
            if(justFile.renameTo(new File(to))){
                System.out.println("Successful!");
            }else{
                System.out.println("Failed!");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
