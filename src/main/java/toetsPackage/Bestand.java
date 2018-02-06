package toetsPackage;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static javax.swing.JFileChooser.APPROVE_OPTION;

/**
 * Class om bestanden te openen, op te slaan en te lezen.
 * @author Alex Janse
 * @since 29-01-2018
 * @version 2.00
 */
public class Bestand extends Thread{

    private File bestand;
    private String naamBestand, pathBestand;
    private BufferedReader inBstand;
    private JFileChooser fileChooser = new JFileChooser();
    private boolean online = false;
    private String website;
    private ArrayList<String> inhoudBestand; //ArrayList omdat ik het wil kunnen aanvullen en bij een array kan dat niet

    /**
     * Constructor die aangeroepen wordt als er een lokaal bestand geopend moet worden
     */
    public Bestand(){}

    /**
     * Constructor die aangeroepen wordt als er een online bestand geopend moet worden
     * @param url String met daarin de URL van het bestand
     */
    public Bestand(String url){
        this.online = true;
        this.website = url;
    }

    /**
     * Methode die wordt aangeroepen als de thread wordt gestart
     */
    public void run(){
        try {
            if (online) {
                inhoudBestand = bestandVanUrl();                        // Bestand openen en de inhoud in een ArrayList<String> plaatsem
            } else {
                kiesBestand();
                inhoudBestand = leesBestand();
            }

            if (checkBestand()) {                                       // Alleen doorgaan als het bestand klopt
                VirusHostMapper mapper = new VirusHostMapper(inhoudBestand);    // mapper object aanmaken en inhoud van het bestand meesturen
                mapper.start();
                VirusLogica.fileExceptionThrower(0);             // Niet echt een exception maar laat weten dat het gelukt is aan de gebruiker
            } else {
                VirusLogica.fileExceptionThrower(2);             // Als het bestand niet klopt zorg er dan voor dat er een Error bij de gebruiker komt
            }
        } catch (NoFileInObject e){                                     // Als er exceptions zijn opgetreden dan dit melden aan de gebruiker
            VirusLogica.fileExceptionThrower(1);
        } catch (IOException e){
            VirusLogica.fileExceptionThrower(3);
        }
    }

    /**
     * Bij het aanroepen van deze methode zal er een openDialog van JFileChooser verschijnen waarin de gebruiker een bestand kan kiezen.
     */
    public void kiesBestand(){
        int reply;
        reply = fileChooser.showOpenDialog(fileChooser); // retourneert een 0 als de gebruiker een bestand heeft gekozen
        if (reply == APPROVE_OPTION) {
            bestand = fileChooser.getSelectedFile();
            naamBestand = bestand.getName();
            pathBestand = bestand.getAbsolutePath();
        }
    }

    /**
     * Methode om het bestand in te lezen en de inhoud te retourneren
     * @return String met de inhoud van het bestand. Hierbij zijn de regels gescheden met een \n.
     * @throws NoFileInObject Wordt aangeroepen als de functie wordt aangeroepen als er nog geen bestand in het object staat.
     * @throws FileNotFoundException Wordt aangeroepen als het bestand in het object niet gevonden kan worden.
     * @throws IOException Wordt aangeroepen als het inhoud van het bestand niet gelezen kan worden.
     */
    public ArrayList<String> leesBestand() throws NoFileInObject, FileNotFoundException, IOException{
        if (bestand == null){
            throw new NoFileInObject();
        } else {
            ArrayList<String> inhoud = new ArrayList<>();
            String regel;
            inBstand = new BufferedReader(new FileReader(bestand));
            regel = inBstand.readLine();
            while (regel != null){
                inhoud.add(regel);
                regel = inBstand.readLine();
            }
            return inhoud;
        }
    }

    /**
     * Methode om een online bestand op te halen en in te lezen
     * @return Een ArrayList<String> met daarin de inhouden met elke object een regel van het bestand
     * @throws MalformedURLException
     * @throws IOException
     */
    public ArrayList<String> bestandVanUrl() throws MalformedURLException, IOException{
        URL url = new URL(website);
        Scanner s = new Scanner(url.openStream());
        ArrayList<String> inhoud = new ArrayList<>();
        String lijn = s.nextLine();
        while (s.hasNext()){
            inhoud.add(lijn);
            lijn = s.nextLine();
        }
        return inhoud;
    }

    /**
     * Controleer het bestand of het met de huidige instellingen gebruikt kan worden om te mappen
     * @return een boolean met true als het bestand gebruikt kan worden en false als het niet zo is
     */
    private boolean checkBestand(){
        if(inhoudBestand.get(0).split("\t").length != 12){
            return false;
        } else {
            return true;
        }
    }

    public String getNaamBestand() {return naamBestand;}
    public File getBestand(){ return bestand; }
    public String getPathBestand(){return pathBestand;}

}

/**
 * Exception voorals er een methode wordt aangeroepen terwijl er geen bestand aanwezig is om te gebruiken.
 */
class NoFileInObject extends Exception{

    public NoFileInObject(){

        super("Error: There is no file to use in called method.");
    }

}

