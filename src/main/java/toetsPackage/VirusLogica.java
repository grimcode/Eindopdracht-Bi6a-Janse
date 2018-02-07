package toetsPackage;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Klas die de logica achter de VirusGUI bevat. Regelt het verkeer tussen de GUI en de verschillende threads.
 * Ik heb gekozen om de gehele klas statisch te houding om zo constant heen en weer sturen van het logica object te omzeilen
 * @author Alex Janse
 * @since 29-01-2018
 * @version 1.00
 */
public class VirusLogica{

    /*
     * Er is gekozen om de data op te slaan in hashmaps
     * omdat dit de snelste datastructuur is om onderdelen toe te voegen, op te halen en te vinden met O(1).
     * Aangezien volgorde niet uit maakt is HashMap volgens mij de beste keuze die er was.
     * Ik heb daarnaast gekozen om de virus objecten onder te verdelen in verschillende hashmap waarbij ze
     * geclusterd zijn met virussen van de zelfde classificatie om ze zo makkelijker te vinden.
     */
    private static HashMap<String,HashMap<String,Virus>> virusMap = new HashMap<>();
    private static HashMap<String, Host> hostMap = new HashMap<>();
    private static HashSet<String> classificaties = new HashSet<>();
    private static ArrayList<String> inhoudBestand, virus1inhoud = new ArrayList<>(),
            virus2inhoud = new ArrayList<>(), intersectinhoud = new ArrayList<>();
    private static VirusGUI virusgui;


    public static void setVirusgui(VirusGUI gui){
        virusgui = gui;
    }
    /**
     * Roept de bestand thread aan om een lokaal bestand te openen
     */
    public static void openLokaalBestand(){
        Bestand bestand = new Bestand();                // Maak een bestands object aan zonder parameter om de lokale bestand route te kunnen nemen als de thread start
        bestand.start();
    }

    /**
     * Roept de bestand thread aan om een online bestand te openen
     * @param website String met daarin de URL van een bestand
     */
    public static void openOnlineBestand(String website){
        Bestand bestand = new Bestand(website);         // Maak een bestands object aan met een String object om zo de online bestand route te kunnen nemen als de thread start
        bestand.start();
    }

    /**
     * Deze methode wordt aangeroepen als de bestand en
     * virushostmapper threads klaar zijn om het resultaat in de gui te laten zien.
     */
    public static void releaseClassAndHost(){
        fillHostBoxes(hostMap.keySet().toArray(new String[hostMap.size()]));
        fillClassificationBox(classificaties);
    }

    /**
     * Zorgt er voor dat de comboboxen van de gastheren gevuld kunnen worden.
     * @param hostLijst Een String[] die de gastheer namen en ID'S BEVAT
     */
    public static void fillHostBoxes(String[] hostLijst){
        Arrays.sort(hostLijst);
        for(String host : hostLijst){
            virusgui.getHost1Box().addItem(host);
            virusgui.getHost2Box().addItem(host);
        }
        virusgui.getHost1Box().setEnabled(true);
        virusgui.getHost2Box().setEnabled(true);
    }

    /**
     * Functie om de de combobox van classificaties in te vullen vanuit de VirusLogica class.
     * Aangezien dit een visuele functie is staat het in de GUI class i.p.v. de logica class.
     * @param lijst Een HashSet met de classificaties
     */
    public static void fillClassificationBox(HashSet<String> lijst){
        JTextArea statusArea = virusgui.getStatusArea(),
                  emotieArea = virusgui.getEmotieArea();
        JComboBox<String> classificationBox = virusgui.getClassificationBox();
        statusArea.append("\n###############\n");
        statusArea.append("Classificatie Lijst is geupdated!\nKies een classificatie om uw gewenste virus groep te zien.");
        Emotie.getGelukt(emotieArea,statusArea);

        lijst.add("Alles");
        String[] sorteerdeLijst = lijst.toArray(new String[lijst.size()]);
        Arrays.sort(sorteerdeLijst);
        for(String classOnderdeel : sorteerdeLijst){
            classificationBox.addItem(classOnderdeel);
        }
        classificationBox.setSelectedIndex(0);
        classificationBox.setEnabled(true);
    }

    /**
     * Deze methode wordt aangeroepen als er een host of classificatie keuze is
     * gemaakt om de virussen die daarbij horen op te halen.
     * @param host String met de gekozen gastheer
     * @param lijstnr bevat de lijst nummer waarin de gegevens in moeten worden opgeslagen
     */
    public static void getVirusses(String host, int lijstnr){
        String sorteerOp = virusgui.getSortBy();                                            // Haalt de geselecteerde sorteer optie op
        if(sorteerOp != null) {                                                             // Om mogelijke errors te voorkomen: controleer of de getter niet null heeft geretourneerd
            String classMap = virusgui.getClassification();                                 //Haal de geselecteerde classificatie op
            HashSet<String> virusSet = hostMap.get(host).getVirusSet();                     // Haal de virus id's op die bij de host horen
            String[] virusList = virusSet.toArray(new String[virusSet.size()]);             // Zet ze om naar een array. Het is een array omdat het alleen gebruikt wordt om er overheen te loopen in de sorter class

            Sorter sorter = new Sorter(virusMap, classMap, virusList, sorteerOp, lijstnr);    // Maak een nieuw sorter object aan met de parameters die het gebruikt om de viruslijst te sorteren op de gewenste volgorde.
            sorter.start();
        }
    }

    /**
     * Methode om de overlap lijst te sorteren
     */
    public static void sortIntersection(){
        String sorteerOp = virusgui.getSortBy();
        if(sorteerOp != null) {
            String classMap = virusgui.getClassification();
            HashSet<String> virusSet = new HashSet<>();
            for(String virus : intersectinhoud){
                virusSet.add(virus);
            }

            if (virusSet.size() > 0) {
                String[] virusList = virusSet.toArray(new String[virusSet.size()]);
                emptyList(3);
                Sorter sorter = new Sorter(virusMap, classMap, virusList, sorteerOp, 3);
                sorter.start();
            } else {
                emptyList(3);
                fillList(3);
            }

        }
    }

    /**
     * Methode die wordt aangeroepen door de sorter thread om de resultaten te plaatsen in de lijst
     * @param virusList ArrayList met de gesorteerde lijst.
     * @param lijstnr bevat de lijst nummer waarin de gegevens in moeten worden opgeslagen
     */
    public static void showVirusses(ArrayList<String> virusList,int lijstnr){
        emptyList(lijstnr);
        if(virusList.size() == 0) {
            addToList("NO MATCH FOUND!",lijstnr);
        } else {
            for (String virus : virusList) {
                addToList(virus,lijstnr);
            }
        }
        fillList(lijstnr);
        if(lijstnr == 2) {
            setIntersect();
            sortIntersection();
        }
    }


    /**
     * Methode om de overlap te bepalen en te plaatsen in de gui
     */
    private static void setIntersect(){
        HashSet<String> set1 = new HashSet<>(virus1inhoud),
                        set2 = new HashSet<>(virus2inhoud);
        emptyList(3);
        set1.retainAll(set2);
        for(String id : set1){
            addToList(id,3);
        }
        if(intersectinhoud.size() >= 1){
            if(intersectinhoud.get(0).equals("NO MATCH FOUND!")) {
                emptyList(3);
            }
        }

    }

    /**
     * Aangezien het niet mogelijk is om exceptions tijdens het runnen van threads
     * optegooien is deze methode gemaakt om dat toch een gelijke effect te creeëren.
     * Ook zorgt de methode voor het uit- en aanzetten van de bestandknoppen.
     * @param functie Een integer waarmee de methode weet welke exception die moet regeren
     */
    public static void fileExceptionThrower(int functie) {
        JButton openURLButton = virusgui.getOpenURLButton(),
                chooseBestandButton = virusgui.getChooseBestandButton(),
                onlineBestandButton = virusgui.getOnlineBestandButton();
        JTextArea emotieArea = virusgui.getEmotieArea(),
                  statusArea = virusgui.getStatusArea();
        switch (functie) {
            case -1:
                // Bestand knoppen tijdelijk uitschakelen om te voorkomen dat het programma overbelast wordt als de gebruiker blijft uploaden
                openURLButton.setEnabled(false);
                chooseBestandButton.setEnabled(false);
                onlineBestandButton.setEnabled(false);
                Emotie.getLoading(emotieArea, statusArea); // melden dat het programma bezig is
                break;

            case 0:
                // Melden dat het is gelukt
                statusArea.append("\n###############\n");
                statusArea.append("Het bestand is geladen en verwerkt!");
                Emotie.getGelukt(emotieArea, statusArea);
                break;

            case 1:
                // NoFileInObject exception is opgetreden in de Bestand thread. Treed alleen op als filechooser geannuleerd is.
                statusArea.append("\n###############\n");
                statusArea.append("Lokale bestand kiezen is geannuleerd.");
                Emotie.getGelukt(emotieArea, statusArea);
                break;

            case 2:
                // WrongDocumentException is opgetreden bij het controleren van de inhoud
                statusArea.append("\n###############\n");
                statusArea.append("Error: Er is iets mis met uw opgegeven bestand."+
                        " Controleer uw URL of bestandsindeling en "+
                        "vergelijk deze met het online bestand op "+
                        "ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv");
                Emotie.getError(emotieArea, statusArea);
                break;

            case 3:
                // IOException is opgetreden tijdens het openen of lezen van het bestand
                statusArea.append("\n###############\n");
                statusArea.append("Error: Uw bestand kan niet geopend of gelezen worden. " +
                        "Controleer uw opgegeven URL en probeer het nog eens.");
                Emotie.getError(emotieArea, statusArea);

            default:
                break;
        }
        if (functie != -1) {
            // Altijd weer knoppen vrijgeven tenzij de methode is aangeroepen om ze uit te doen
            chooseBestandButton.setEnabled(true);
            onlineBestandButton.setEnabled(true);
            openURLButton.setEnabled(true);
        }
    }

    /**
     * Vult de JList aan van de gui
     * @param lijstnr de lijst nummer waar in de tekst moet koemn
     */
    public static void fillList(int lijstnr){
        switch (lijstnr){
            case 1:
                virusgui.getAantalVirussen1().setText("("+virus1inhoud.size()+")");
                virusgui.getVirus1List().setListData(virus1inhoud.toArray(new String[virus1inhoud.size()]));
                break;

            case 2:
                virusgui.getAantalVirussen2().setText("("+virus2inhoud.size()+")");
                virusgui.getVirus2List().setListData(virus2inhoud.toArray(new String[virus2inhoud.size()]));
                break;

            case 3:
                virusgui.getIntersectList().setListData(intersectinhoud.toArray(new String[intersectinhoud.size()]));
                break;

            default:
                break;
        }
    }

    /**
     * Methode om string objecten toe te voegen aan de juiste arraylist
     * @param item String object dat toegevoegt moet worden
     * @param lijstnr int primitive met de lijst nummer waarin de item in moet komen te staan
     */
    public static void addToList(String item, int lijstnr){
        switch (lijstnr){
            case 1:
                virus1inhoud.add(item);
                break;

            case 2:
                virus2inhoud.add(item);
                break;

            case 3:
                intersectinhoud.add(item);
                break;

            default:
                break;
        }
    }

    /**
     * Maakt de juiste lijst leeg
     * @param lijstnr int van de lijst die leeg gemaakt moet worden
     */
    public static void emptyList(int lijstnr){
        switch (lijstnr){
            case 1:
                virus1inhoud.clear();
                break;

            case 2:
                virus2inhoud.clear();
                break;

            case 3:
                intersectinhoud.clear();
                break;

            default:
                break;
        }
    }

    public static HashMap<String, Host> getHostMap() {return hostMap;}
    public static HashMap<String,HashMap<String,Virus>> getVirusMap() {return virusMap;}
    public static ArrayList<String> getInhoudBestand(){return inhoudBestand;}
    public static HashSet<String> getClassificaties(){return classificaties;}
    public static ArrayList<String> getVirus1inhoud(){return virus1inhoud;}
    public static ArrayList<String> getVirus2inhoud(){ return virus2inhoud;}
    public static ArrayList<String> getIntersectinhoud(){ return intersectinhoud;}

    public static void setInhoudBestand(ArrayList<String> inhoud){ inhoudBestand = inhoud;}

}



