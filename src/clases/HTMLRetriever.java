/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author fabiel
 */
public class HTMLRetriever extends Thread {

    private final ThreadPoolExecutor executor;
    private final LinkedBlockingDeque<URL> urlsHTML;
//    private final LinkedBlockingDeque<URL> urls;
    private final LinkedBlockingDeque<URL> urlDown;
    private boolean finish = false;
    private final File file;

    public HTMLRetriever(URL url, LinkedBlockingDeque<URL> urlDown, File file) {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

        this.urlsHTML = new LinkedBlockingDeque<>();
//        this.urls = urls;
        this.urlDown = urlDown;
        urlsHTML.add(url);
        this.file = file;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    @Override
    public void run() {
        while (!urlsHTML.isEmpty()) {
            try {
//                System.out.print("\rurlsHTML = " + empty);
                URL currentURL = urlsHTML.take();
                URLConnection urlc = currentURL.openConnection();
                String contentType = urlc.getContentType();
                if (contentType != null && contentType.contains("text/html")) {
                    ParserGetter kit;
                    kit = new ParserGetter();
                    HTMLEditorKit.Parser parser = kit.getParser();
                    HTMLEditorKit.ParserCallback callback;
                    callback = new HTMLLinkScanner(currentURL, urlDown, urlsHTML, executor, file);
                    try (InputStream in = new BufferedInputStream(currentURL.openStream())) {
                        InputStreamReader r = new InputStreamReader(in);
                        parser.parse(r, callback, true);
                    }
                }
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(HTMLRetriever.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        setFinish(true);
        System.out.print("\rTermine de parsear HTML");
        executor.shutdown();
    }

}
