package com.rerx.alexey.audiocontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by mihail on 04.02.16.
 */
public class FilesControl {

    private Context context;
    private MainActivity mainActivity;
    private Spinner savedTabChooser;

    private static final String FILE_FOLDER = "AudioControl";
    private static final String PATH = android.os.Environment
            .getExternalStorageDirectory()
            + java.io.File.separator
            + FILE_FOLDER + java.io.File.separator;

    // TODO: 05.02.16 проверить перевод
    private String TAB_EXTENSION = ".tab";


    FilesControl(Context context) {
        this.context = context;
        mainActivity = (MainActivity) context;
        checkFolder();
    }

    public void checkFolder() {
        File dir = new File(PATH);
        if (!(dir.isDirectory() && dir.exists())) {
            Toast.makeText(context, mainActivity.getString(R.string.folder_not_found),
                    Toast.LENGTH_SHORT).show();
            if (!dir.mkdirs()) {
                // TODO: 05.02.16 обработать случай ошибки создания папки
                new AlertDialog.Builder(context)
                        .setTitle(mainActivity.getString(R.string.unexpected_err))
                        .setPositiveButton(mainActivity.getString(R.string.exit), (dialog, which) -> {
                            System.exit(0);
                        })
                        .create()
                        .show();
            }
        }
    }

    public boolean saveTab(String tabName, Tablature tab) throws IOException {
        File file = new File(PATH + tabName + TAB_EXTENSION);
        if (!file.createNewFile()) {
            return false;
        } else {
            PrintWriter writer = new PrintWriter(file);

            for (int i = 0; i < 6; i++) {
                String storedData = "";
                for (String s : tab.getStringsList().get(i)) {
                    storedData = storedData.concat(s);
                }
                writer.println(storedData);
            }
            writer.close();

        }
        return true;
    }

    public Tablature openTab(String name) throws IOException {
        name = PATH + name + TAB_EXTENSION;
        Tablature tab = new Tablature();
//        File file   = new File(name);
        BufferedReader reader = openFile(name);
        boolean initializated = false;
        ArrayList<ArrayList<Integer[]>> list = new ArrayList<>();
        for (int string = 1; string <= 6; string++) {
            String line = reader.readLine();
//            while((line.charAt(j)!='\n')){
//                if((line.charAt(j)==c)){
//
//                }
//            }
            String[] bar = line.split("|");

            if (!initializated) {
                initializated = true;
                for (String aBar : bar) {
                    list.add(new ArrayList<>());
                }
            }


            int i = 1;
            while (i < bar[0].length()) {
                if (bar[0].charAt(i) != '-') {
                    String num_s = "";
                    while (bar[0].charAt(i) != '-') {
                        num_s += bar[0].charAt(i++);
                    }
                    Integer note[] = {string, Integer.valueOf(num_s)};
                    list.get(i).add(note);
                }
                i++;
            }

            for (int bar_i = 1; bar_i < bar.length; bar_i++) {
                i = 0;
                while (i < bar[bar_i].length()) {
                    if (bar[bar_i].charAt(i) != '-') {
                        String num_s = "";
                        while (bar[bar_i].charAt(i) != '-') {
                            num_s += bar[bar_i].charAt(i++);
                        }
                        tab.addNote(string, Integer.valueOf(num_s));
                    }
                    i++;
                }
            }
        }




        return tab;
    }

    public Spinner getSavedTabsChooser() {
        if (savedTabChooser != null) {
            return savedTabChooser;
        } else {
            savedTabChooser = new Spinner(context);
            // TODO: 05.02.16 доделать вывод списка табов
            ArrayList<String> list = new ArrayList<>();
            list.add("DEMO");
            savedTabChooser.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list));

            return savedTabChooser;
        }
    }

    public void saveFile(String name, ArrayList<Object> list) throws FileNotFoundException {
        File file = new File(PATH + name);
        PrintWriter writer = new PrintWriter(file);
        for (Object s : list) {
            writer.println(s);
        }
        writer.close();

    }


    public BufferedReader openFile(String fileName) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(PATH + fileName));
        Log.i("FilesControl", "File{" + fileName + "} is loaded");
        return br;
    }

}
