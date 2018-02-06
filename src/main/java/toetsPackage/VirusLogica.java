package toetsPackage;

import javax.swing.*;
import java.util.ArrayList;
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
    private static ArrayList<String> inhoudBestand;
    private static VirusGUI virusgui;

    /**
     * Roept de bestand thread aan om een lokaal bestand te openen
     * @param gui Bevat de VirusGUI waarmee gewerkt wordt om later voor andere functies bij de getters en setters te komen van het object.
     */
    public static void openLokaalBestand(VirusGUI gui){
        virusgui = gui;
        Bestand bestand = new Bestand();                // Maak een bestands object aan zonder parameter om de lokale bestand route te kunnen nemen als de thread start
        bestand.start();
    }

    /**
     * Roept de bestand thread aan om een online bestand te openen
     * @param gui Bevat de VirusGUI waarmee gewerkt wordt om later voor andere functies bij de getters en setters te komen van het object.
     * @param website String met daarin de URL van een bestand
     */
    public static void openOnlineBestand(VirusGUI gui,String website){
        virusgui = gui;
        Bestand bestand = new Bestand(website);         // Maak een bestands object aan met een String object om zo de online bestand route te kunnen nemen als de thread start
        bestand.start();
    }

    /**
     * Deze methode wordt aangeroepen als de bestand en
     * virushostmapper threads klaar zijn om het resultaat in de gui te laten zien.
     */
    public static void releaseClassAndHost(){
        virusgui.fillHostBoxes(hostMap.keySet().toArray(new String[hostMap.size()]));
        virusgui.fillClassificationBox(classificaties);
        setIntersect();
        sortIntersection();
    }

    /**
     * Deze methode wordt aangeroepen als er een host of classificatie keuze is
     * gemaakt om de virussen die daarbij horen op te halen.
     * @param host String met de gekozen gastheer
     * @param model DefaultListModel<String> van de lijst die aangepast moet worden
     */
    public static void getVirusses(String host, DefaultListModel<String> model){
        String sorteerOp = virusgui.getSortBy();                                            // Haalt de geselecteerde sorteer optie op
        if(sorteerOp != null) {                                                             // Om mogelijke errors te voorkomen: controleer of de getter niet null heeft geretourneerd
            String classMap = virusgui.getClassification();                                 //Haal de geselecteerde classificatie op
            HashSet<String> virusSet = hostMap.get(host).getVirusSet();                     // Haal de virus id's op die bij de host horen
            String[] virusList = virusSet.toArray(new String[virusSet.size()]);             // Zet ze om naar een array. Het is een array omdat het alleen gebruikt wordt om er overheen te loopen in de sorter class

            Sorter sorter = new Sorter(virusMap, classMap, virusList, sorteerOp, model);    // Maak een nieuw sorter object aan met de parameters die het gebruikt om de viruslijst te sorteren op de gewenste volgorde.
            sorter.start();
        }
    }

    /**
     * Methode om de overlap lijst te sorteren
     */
    public static void sortIntersection(){
        DefaultListModel<String> model = virusgui.getModelIntersect();
        String sorteerOp = virusgui.getSortBy();
        if(sorteerOp != null) {
            String classMap = virusgui.getClassification();
            HashSet<String> virusSet = new HashSet<>();
            for (int index = 0; index < model.size(); index++) {
                virusSet.add(model.getElementAt(index));
            }
            if (virusSet.size() > 0) {
                String[] virusList = virusSet.toArray(new String[virusSet.size()]);

                Sorter sorter = new Sorter(virusMap, classMap, virusList, sorteerOp, model);
                sorter.start();
            }
        }
    }

    /**
     * Methode die wordt aangeroepen door de sorter thread om de resultaten te plaatsen in de lijst
     * @param virusList ArrayList met de gesorteerde lijst.
     * @param model DefaultListModel om de virussen in de gui te plaatsten
     */
    public static void showVirusses(ArrayList<String> virusList,DefaultListModel<String> model){
        model.removeAllElements();
        if(virusList.size() == 0) {
            model.addElement("NO MATCH FOUND!");
        } else {
            for (String virus : virusList) {
                model.addElement(virus);
            }
        }
        if(model != virusgui.getModelIntersect()) {
            setIntersect();
            sortIntersection();
        }
    }


    /**
     * Methode om de overlap te bepalen en te plaatsen in de gui
     */
    private static void setIntersect(){
        HashSet<String> set1 = getModelElements(virusgui.getModel1()),
                        set2 = getModelElements(virusgui.getModel2());
        DefaultListModel<String> modelIntersect = virusgui.getModelIntersect();
        modelIntersect.removeAllElements();
        set1.retainAll(set2);
        for(String id : set1){
            modelIntersect.addElement(id);
        }
        if(modelIntersect.size() >= 1){
            if(modelIntersect.getElementAt(0).equals("NO MATCH FOUND!")) {
                modelIntersect.removeAllElements();
            }
        }


    }

    /**
     * Methode om de inhoud van een list te isoleren
     * @param model DefaultListModel die het mogelijk maakt om de lijst te bekijken
     * @return een HashSet<String> met de inhoud van de lijst
     */
    private static HashSet<String> getModelElements(DefaultListModel<String> model){
        HashSet<String> inhoud = new HashSet<>();
        for(int index = 0; index < model.size(); index++){
            try {
                inhoud.add(model.getElementAt(index));
            } catch (ArrayIndexOutOfBoundsException e){
                /* Tijdens het testen van mijn programma kwam ik op deze locatie deze error tegen.
                * De Error kwam uit het niks en toen ik excact dezelfde gemaakte stappen herhaalde
                * na het opnieuw opstarten van mijn programma bleef de error weg. Na een tijdje random knoppen in te drukken
                * lukte het me om weer deze error te krijgen en dit keer had ik een print statement er bij staan waarin
                * stond dat index = 0, model.size = 7 en inhoud.size = 0 dus ik zie niet de reden voor deze error.
                * Toen ik dezelfe stappen opnieuw probeerde bleef de error weer weg...
                * Aangezien de error random lijkt te zijn heb ik geprobeert dit op te lossen door de methode
                * opnieuw aan te roepen in de hoop dat de error niet meer verschijnt maar wel met het risico
                * op een oneindige loop maar aangezien ik de error niet meer kan krijgen kan ik dit niet uit testen...
                 */
                inhoud = getModelElements(model);
            }
        }
        return inhoud;
    }

    /**
     * Methode die door threads wordt aangeroepen om als een soort exception oproeper te dienen
     * @param functie Interger die staat voor de exception die moet worden aangeroepen
     */
    public static void fileExceptionThrower(int functie){
        virusgui.fileExceptionCatcher(functie);
    }

    public static HashMap<String, Host> getHostMap() {return hostMap;}
    public static HashMap<String,HashMap<String,Virus>> getVirusMap() {return virusMap;}
    public static ArrayList<String> getInhoudBestand(){return inhoudBestand;}
    public static HashSet<String> getClassificaties(){return classificaties;}

    public static void setInhoudBestand(ArrayList<String> inhoud){ inhoudBestand = inhoud;}

}



