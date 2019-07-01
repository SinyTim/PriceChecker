package com.example.test;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JSONFunctions {
    /*public static File convertToJSON(InfoSend info, File directory)throws IOException{
        Gson gson = new Gson();
        //InfoSend info = new InfoSend(1000000000000L);
        //String fileName = "output.json";
        File file = File.createTempFile("send", ".json", directory);
        //File file = File.createTempFile(fileName, null, MainActivity.this.getCacheDir());

        //file.deleteOnExit();
        FileWriter fileWriter = new FileWriter(file);
        gson.toJson(info,fileWriter);
        fileWriter.close();

        //BufferedReader reader = new BufferedReader(new FileReader(file));
        //info = gson.fromJson(reader, InfoSend.class);
        //file.delete();
        return file;
    }*/
}
