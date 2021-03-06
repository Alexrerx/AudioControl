package com.rerx.alexey.audiocontrol;

import android.app.AlertDialog;
import android.app.TaskStackBuilder;
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

    public static final String FILE_FOLDER = "AudioControl";
    public static final String PATH = android.os.Environment
            .getExternalStorageDirectory()
            + java.io.File.separator
            + FILE_FOLDER + java.io.File.separator;

    public String TAB_EXTENSION = ".tab";


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

    private void printData(File file, Tablature tab) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(file);


        writer.println(tab.getBPM());

        for (int i = 0; i < 6; i++) {
            String storedData = "";
            for (String s : tab.getStringsList().get(i)) {
                storedData = storedData.concat(s);
            }
            writer.println(storedData);
        }
        writer.close();
    }

    public void saveRecord(String recordName){
//        mainActivity.record.finish(PATH+recordName+Record.FILE_EXTENSION);
//        for (String file:mainActivity.record.getTmpFiles()){
//            new File(file).delete();
//        }

    }

    public boolean saveTab(String tabName, Tablature tab, boolean overwrite) throws IOException {
        saveRecord(tabName);
        File file = new File(PATH + tabName + TAB_EXTENSION);
        if (overwrite) {
            boolean res = file.delete();
            res |= file.createNewFile();
            if (!res) {
                throw new IOException("File overwriting error");
            }
            printData(file, tab);
        } else {
            if (!file.createNewFile()) {
                if (file.exists()) {
                    return false;
                } else {
                    throw new IOException("File creating error");
                }
            } else {
                printData(file, tab);
            }
        }
        return true;
    }

    public Tablature openTab(String name) throws Exception {
        Tablature tab = new Tablature(context, name);
        name = name + TAB_EXTENSION;
        tab.clearTab();
        BufferedReader reader = openFile(name);

        String s1 = reader.readLine();
        tab.setBPM(Integer.valueOf(s1));

        ArrayList<String> l = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            l.add(reader.readLine());
        }

        for (int bar = 1; bar < l.get(0).length(); bar++) {
            if (l.get(0).charAt(bar) == '|') {
                tab.addBar();
            } else {
                for (int string = 0; string < 6; string++) {
                    String s = String.valueOf(l.get(string).charAt(bar)) + String.valueOf(l.get(string).charAt(bar + 1));
                    if (!s.equals("--") && !s.equals("-|")) {
                        try {
                            tab.addNoteNoTime(string + 1, Integer.valueOf(s));
                        } catch (NumberFormatException e) {
                            try {
                                tab.addNoteNoTime(string + 1, Integer.valueOf(String.valueOf(s.charAt(0))));
                            } catch (NumberFormatException ee) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                }
                bar++;
            }
        }

        return tab;
    }

    public Spinner getSavedTabsChooser() {
        Spinner savedTabChooser = new Spinner(context);
        File path = new File(PATH);
        File[] files = path.listFiles();
        ArrayList<String> fileslist = new ArrayList<String>();
        for (File f : files) {
            String s = String.valueOf(f.getName());
            if (s.contains(".tab")) {
                fileslist.add(s.substring(0, s.length() - 4));
            }
        }
        savedTabChooser.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, fileslist));
        return savedTabChooser;
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
