package com.example.oneus.subClasses;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Path {
    public static String getPath(final Context context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                String[] path = id.split(":");
                return path[1];
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    //copy a binary files.
    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    //delete a folder
    public static void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] dirs = folder.listFiles();
            for (File dir: dirs) {
                deleteFolder(dir);
            }
        }
        folder.delete();
    }

    //create a folder in ONEUS
    public static boolean createSubsDirectory(String FolderName){
        File folder = new File(Environment.getExternalStorageDirectory() +"/ONEUS/" + FolderName);
        if (!folder.exists()){
            folder.mkdir();
            return true;
        }else{
            return false;
        }
    }

    public static List<String[]> readHistoryFolder(){
        List<String[]> history = new ArrayList<>();
        try {
            File txt = new File(Environment.getExternalStorageDirectory() +"/ONEUS/HistoryFolder.txt");
            Scanner scan = new Scanner(txt);
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                history.add(line.split("-"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return history;
    }

    public static void writeHistoryFolder(List<String[]> newFile){
        File txt = new File(Environment.getExternalStorageDirectory() +"/ONEUS/HistoryFolder.txt");
        if (txt.exists()){
            txt.mkdirs();
        }
        try {
            FileOutputStream writer = new FileOutputStream(txt);
            for(int i = 0; i < newFile.size(); i++){
                String info = newFile.get(i)[0]+"-"+newFile.get(i)[1]+"\n";
                writer.write(info.getBytes(StandardCharsets.UTF_8));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  String getExtension(File file){
        String filename = file.toString();
        int index = filename.lastIndexOf('.');
        return filename.substring(index+1);
    }

    public static int getMaxIndex(File fileDir) {
        if(fileDir.isDirectory()){
            if (fileDir.listFiles().length == 0)
                return 0;
            else{
                List listFile = Arrays.asList(fileDir.list());
                Collections.sort(listFile);
                Collections.sort(listFile,Collections.reverseOrder());
                String info = listFile.get(0).toString().split("_")[1];
                int maxInt = info.lastIndexOf('.');
                String tmp = info.substring(0, maxInt);
                return Integer.parseInt(tmp);
            }
        }
        return 0;
    }

}
