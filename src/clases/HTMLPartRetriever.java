/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author fleon
 */
class HTMLPartRetriever extends HTMLEditorKit.ParserCallback {

    private final URL base;
    private boolean script;
    private final LinkedBlockingDeque<URL> urlDown;
    private final LinkedBlockingDeque<URL> urls;

    HTMLPartRetriever(URL base, LinkedBlockingDeque<URL> urls, LinkedBlockingDeque<URL> urlDown) {
        this.base = base;
        this.urls = urls;
        this.urlDown = urlDown;
    }

    @Override
    public void handleText(char[] text, int pos) {
        //  String.
//            try {
//                out.write(text);
//                out.flush();
//            } catch (IOException e) {
//                System.err.println(e);
//            }
    }

    @Override
    public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int pos) {
//            try {
//                out.write("<" + tag);
        this.writeAttributes(attributes);
//                out.write(">");
//                out.flush();
//            } catch (IOException ex) {
//                Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
//            }
    }

    @Override
    public void handleEndTag(HTML.Tag tag, int position) {
//            try {
//                out.write("</" + tag + ">");
//                out.flush();
//            } catch (IOException ex) {
//                Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
//            }
    }

    @Override
    public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
//            try {
//                out.write("<" + tag);
        this.writeAttributes(attributes);
//                out.write("/>");
//            } catch (IOException ex) {
//                Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
//            }
    }

    @Override
    public void handleComment(char[] data, int pos) {
    }

    private void writeAttributes(MutableAttributeSet attributes) {
        Enumeration e = attributes.getAttributeNames();
        while (e.hasMoreElements()) {
            Object name = e.nextElement();
            String value = (String) attributes.getAttribute(name);
            if (name == HTML.Attribute.HREF || name == HTML.Attribute.SRC || name == HTML.Attribute.LOWSRC || name == HTML.Attribute.CODEBASE) {
                if (value.charAt(0) != '#' && !value.startsWith("../") && !value.startsWith("javascript")) {
                    try {
//                            System.out.println("name = " + name);
//                            System.out.println("value = " + value);
                        URL u = new URL(base, value);
                        if (!"mailto".equals(u.getProtocol())
                                && base.getHost().equals(u.getHost())
                                && !urls.contains(u)
                                && !urlDown.contains(u)) {
                            try {
//                                    System.out.println("u = " + u);
                                urls.put(u);

                            } catch (InterruptedException ex) {
                                Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
