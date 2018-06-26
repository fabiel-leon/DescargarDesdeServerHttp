/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fabiel
 */
public class ParsearHTML {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String azul = "\u001B[34m", rojo = "\u001B[31m", verde = "\u001B[32m", reset = "\u001B[0m";
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            azul = rojo = verde = reset = "";
        }
        System.out.print(azul + "Autor:");
        System.out.println(rojo + "Fabiel Leon Oliva");
        System.out.print(azul + "Telf:");
        System.out.println(rojo + " 5354260597");
        System.out.print(azul + "Correo:");
        System.out.println(rojo + " mailto:fleon@estudiantes.uci.cu");
        System.out.print(azul + "Jabber:");
        System.out.println(rojo + " fleon@jabber.uci.cu " + reset);
        if (args.length == 0) {
            System.out.println(verde + "error de uso");
            System.out.println("primer parametro debe ser una url http o https");
            System.out.println("el segundo parametro debe ser la carpeta donde descargar (opcional)");
            System.out.println("ejemplo: http://kavupdates.uci.cu /home/admin/Updates");
            System.out.println("si un parametro tiene espacio intermedio encierrelo entre comillas \"\" ");
            System.out.println("ejemplo:   \"/home/admin/Mi Carpeta\"" + reset);
            System.exit(-1);
        }
        try {
            Handler[] handlers = Logger.getLogger("").getHandlers();
            for (Handler handler : handlers) {
                handler.setLevel(Level.OFF);
                Logger.getLogger("").removeHandler(handler);
                handler.close();
            }
            FileHandler fh = new FileHandler("log.log", true);
            Logger.getLogger("").addHandler(fh);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
        }
//        final File file = new File(System.getProperty("user.dir") + File.separator + "Updates");

//        final File file = new File(System.getProperty("user.dir") + " " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
//        final File file = new File(args[1] + " " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        try {
            URL url = new URL(args[0]);
            final File file;
            if (args.length == 2) {
                file = new File(args[1]);
            } else {
                file = new File(System.getProperty("user.dir") + File.separator + url.getHost());
            }
            System.out.println("Se esta descargando en: "+file);
            final LinkedBlockingDeque<URL> urlDown = new LinkedBlockingDeque<>();
            HTMLRetriever tt;
            tt = new HTMLRetriever(url, urlDown, file);
            tt.start();
//            url = new URL(args[0]);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ParsearHTML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
