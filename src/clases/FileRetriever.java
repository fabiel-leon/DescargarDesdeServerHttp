/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fabiel
 */
public class FileRetriever implements Runnable {

    private final URL url;

//    private final int i;
    private final LinkedBlockingDeque<URL> urlDown;
    private final File file;
    private final byte[] b = new byte[102400];

    public FileRetriever(URL u, LinkedBlockingDeque<URL> urlDown, File file) {
        this.url = u;
//        this.i = i;
        this.urlDown = urlDown;
        this.file = file;
    }

    @Override
    public void run() {
        try {
//                URL url = urls.take();
            String path = url.getPath();
            URLConnection urlc = url.openConnection();
//            urlc.addRequestProperty("Connection", "close");
            try (InputStream openStream = urlc.getInputStream()) {
//                    String string = openStream.getClass().getCanonicalName();
//                    System.out.println(string);
                File carpeta = new File(file, path.substring(0, path.lastIndexOf("/")));
                carpeta.mkdirs();
                File archivo = new File(carpeta, path.substring(path.lastIndexOf("/") + 1));
                BufferedInputStream bis = new BufferedInputStream(openStream);
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(archivo))) {
                    int nL;
                    try {
                        while ((nL = bis.read(b)) != -1) {
                            bos.write(b, 0, nL);
                        }
                    } catch (IOException ex) {
                        String headerField = urlc.getHeaderField(null);
                        if (!headerField.contains("206")) {
                            System.out.print("\r  code 206");
//                            urls.addLast(url);
                        }
                        Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, url.toString() + " " + headerField, ex);
                    }
                }
                try {
                    bis.close();
                } catch (IOException ex) {
                    Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, url.toString(), ex);
                }
            }
            urlDown.add(url);
            String toString = url.toString();
            String fileName = toString.substring(toString.lastIndexOf("/") + 1);
            int length = fileName.length();
            int maxlenght = 30;
            if (length > maxlenght) {
                fileName = fileName.substring(0, maxlenght);
            }
            if (length < maxlenght) {
                int faltan = maxlenght - length;
                StringBuilder sb = new StringBuilder();
                sb.append(fileName);
                for (int i = 0; i < faltan; i++) {
                    sb.append(" ");
                }
                fileName = sb.toString();
            }
            System.out.print("\rdescargue " + fileName);
        } catch (IOException ex) {
            Logger.getLogger(FileRetriever.class.getName()).log(Level.SEVERE, null, ex);
        }
//        System.out.println("Thread " + i + " termine.");
    }

}
