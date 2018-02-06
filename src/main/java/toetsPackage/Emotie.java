package toetsPackage;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class om emoties te retourneren om succes te vieren en de pijn van errors te verzachten.
 * @author Alex Janse
 * @since 03-12-2018
 * @version 1.00
 */
public class Emotie {

    private static final String GELUKT = "  (\\__/)\n"+
                                         "  (^__^)\n"+
                                         "┌-0----0-┐\n"+
                                         "| Gelukt!|\n"+
                                         "└--------┘",
                                 ERROR = "  (\\__/)\n"+
                                        "  (>__<)\n"+
                                        "┌-0----0-┐\n"+
                                        "| Error! |\n"+
                                        "└--------┘",
                                WELKOM = "  (\\__/)\n"+
                                        "  (^__^)\n"+
                                        "┌-0----0-┐\n"+
                                        "| Welkom!|\n"+
                                        "└--------┘",
                                LOADING = "  (\\__/)\n"+
                                        "  (u__u)\n"+
                                        "┌-0----0-┐\n"+
                                        "| Laden..|\n"+
                                        "└--------┘";
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");    // Hiermee kan de datum en tijd meegestuurd worden
    ;


    /**
     * Methode om succesen te vieren
     * @param emotieArea Hierin komt de succes emotie te staan
     * @param statusArea Hierin wordt de font kleur en achtergrond aangepast
     */
    public static void getGelukt(JTextArea emotieArea, JTextArea statusArea){
        Date date = new Date();                                                                     // De Date klass moet telkens opnieuw worden aangemaakt omdat het object alleen de datum van aanmaak op slaat
        emotieArea.setBackground(Color.black);
        emotieArea.setForeground(Color.green);
        statusArea.setForeground(Color.green);
        statusArea.append("\n"+dateFormat.format(date));
        statusArea.setCaretPosition(statusArea.getDocument().getLength());                          // Zorgt ervoor dat de statusarea helemaal naar beneden scrollt zodat de recente bericht in beeld komt te staan
        emotieArea.setText(GELUKT);
    }

    /**
     * Methode om Error's op te laten vallen
     * @param emotieArea Hierin komt de error emotie
     * @param statusArea Hierin wordt de kleur en achtergrond aangepast
     */
    public static void getError(JTextArea emotieArea, JTextArea statusArea){
        Date date = new Date();
        emotieArea.setBackground(Color.red);
        emotieArea.setForeground(Color.black);
        statusArea.setForeground(Color.red);
        statusArea.append("\n"+dateFormat.format(date));
        statusArea.setCaretPosition(statusArea.getDocument().getLength());
        emotieArea.setText(ERROR);
    }

    /**
     * Methode om de gebruiker welkom te heten
     * @param emotieArea
     * @param statusArea
     */
    public static void getWelkom(JTextArea emotieArea, JTextArea statusArea){
        emotieArea.setBackground(Color.black);
        emotieArea.setForeground(Color.cyan);
        statusArea.setForeground(Color.cyan);
        statusArea.setCaretPosition(statusArea.getDocument().getLength());
        emotieArea.setText(WELKOM);
    }

    /**
     * Methode om te ladten weten dat het programma bezig is
     * @param emotieArea
     * @param statusArea
     */
    public static void getLoading(JTextArea emotieArea, JTextArea statusArea){
        Date date = new Date();
        emotieArea.setBackground(Color.black);
        emotieArea.setForeground(Color.white);
        statusArea.setForeground(Color.white);
        statusArea.append("\n###############\n");
        statusArea.append("Loading...");
        statusArea.append("\n"+dateFormat.format(date));
        statusArea.setCaretPosition(statusArea.getDocument().getLength());
        emotieArea.setText(LOADING);
    }
}
