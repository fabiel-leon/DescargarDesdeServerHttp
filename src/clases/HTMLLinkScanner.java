/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author fabiel
 */
public class HTMLLinkScanner extends HTMLEditorKit.ParserCallback {

    private final URL base;
    private boolean script;
    private final LinkedBlockingDeque<URL> urlDown;
    private final LinkedBlockingDeque<URL> urlsHTML;
//    private final LinkedBlockingDeque<URL> urls;
    private final Executor executor;
    private final File file;

    public HTMLLinkScanner(URL base, LinkedBlockingDeque<URL> urlDown, LinkedBlockingDeque<URL> urlsHTML, Executor  executor,File file) {
        this.base = base;
        this.urlDown = urlDown;
        this.urlsHTML = urlsHTML;
//        this.urls = urls;
        this.executor = executor;
        this.file = file;
    }

    @Override
    public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int pos) {
        this.writeAttributes(attributes, tag);
    }

    @Override
    public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
        this.writeAttributes(attributes, tag);
    }

    private void writeAttributes(MutableAttributeSet attributes, Tag t) {
        Enumeration e = attributes.getAttributeNames();
        while (e.hasMoreElements()) {
            Object name = e.nextElement();
            Object attribute = attributes.getAttribute(name);
            if (attribute instanceof String) {
                String value = (String) attribute;
                if (name == HTML.Attribute.HREF || name == HTML.Attribute.SRC || name == HTML.Attribute.LOWSRC || name == HTML.Attribute.CODEBASE) {
                    if (value.charAt(0) != '#' && !value.startsWith("../") && !value.startsWith("javascript")) {
                        try {
//                            System.out.println("name = " + name);
//                            System.out.println("value = " + value);
                            URL u = new URL(base, value);
                            if (!"mailto".equals(u.getProtocol())
                                    && base.getHost().equals(u.getHost())
                                    && !urlsHTML.contains(u)
                                    && !urlDown.contains(u)) {
                                try {
                                    URLConnection urlc = null;
                                    try {
                                        urlc = u.openConnection();
                                    } catch (IOException ex) {
                                        Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    String contentType = urlc.getContentType();
                                    if (contentType != null && contentType.contains("text/html")) {
                                        urlsHTML.put(u);
                                    } else {
                                        executor.execute(new FileRetriever(u,urlDown,file));
//                                        urls.put(u);
                                    }
//                                    System.out.println("u = " + u);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } else {
                System.out.print("\rattributo no es un string");
                System.out.print("\rattribute = " + attribute.getClass());
                System.out.print("\rattribute = " + attribute);
                System.out.print("\rurl = " + base);
                System.out.print("\r<" + t + " " + name + "=" + attribute);
            }

        }
    }

    /*   public void run() {
    
     while (true) {
     //try {
     int empty = urls.size();
     System.out.println("urlsHTML = " + empty);
     URL url = null;
     try {
     url = urls.take();
     } catch (InterruptedException ex) {
     Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
     }
     URLConnection urlc = null;
     try {
     urlc = url.openConnection();
     } catch (IOException ex) {
     Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
     }
     String contentType = urlc.getContentType();
     System.out.println("contentType = " + contentType);
     if (contentType != null && contentType.contains("text/html")) {
     InputStream in = null;
     ParsearHTML.ParserGetter kit;
     kit = new ParsearHTML.ParserGetter();
     HTMLEditorKit.Parser parser = kit.getParser();
     HTMLEditorKit.ParserCallback callback;
     callback = new HTMLLinkScanner(url, urlDown, urls);
     try {
     in = new BufferedInputStream(url.openStream());
     } catch (IOException ex) {
     Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
     }
     InputStreamReader r = new InputStreamReader(in);
     try {
     parser.parse(r, callback, true);
     } catch (IOException ex) {
     Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
     }
     try {
     in.close();
     } catch (IOException ex) {
     Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     }
     }*/
}
