package toetsPackage;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Klas die gebruikt wordt om virus lijsten te gebruiken om de bijbehorende virus object op te halen en te sorteren
 */
public class Sorter extends Thread {

    private static HashMap<String,HashMap<String,Virus>> virusMap = new HashMap<>();
    private String classification, sortBy;
    private String[] findThese;
    private DefaultListModel<String> model; // Puur alleen zodat viruslogica weet waar de ID's uiteindelijk in moet worden opgeslagen

    /**
     * Constructor die gebruikt wordt om het object te maken met bijbehorende parameters
     * @param map HashMap met de classificatie hashmaps
     * @param classification String met de classificatie key voor de juiste HashMap uit de map te halen
     * @param findThese een array met de virussen due gevonden en gesorteerd moeten worden
     * @param sortBy Een String waarin staat waarop gesorteerd moet worden
     * @param model Model die verwijst naar de lijst waarin de virus gezet moet worden
     */
    public Sorter(HashMap<String,HashMap<String,Virus>> map,
                  String classification,String[] findThese,
                  String sortBy, DefaultListModel model){
        virusMap = map;
        this.classification = classification;
        this.findThese = findThese;
        this.sortBy = sortBy;
        this.model = model;
    }

    @Override
    /**
     * Methode die wordt aangeroepen als de thread wordt gestart
     */
    public void run(){
        ArrayList<Virus> virusList = vindVirussen();
        ArrayList<Virus> gesorteerd = sorteer(virusList);
        ArrayList<String> idList = extractID(gesorteerd);
        VirusLogica.showVirusses(idList, model);

    }

    /**
     * Methode om de Virus object op te halen die overeenkomen met de ID's uit de viruslijst
     * @return Een ArrayList met de gevonden Virus objecten
     */
    private ArrayList<Virus> vindVirussen(){
        ArrayList<Virus> gevondenVirussen = new ArrayList<>();
        if (classification.equals("Alles")){
            for(String mainKey : virusMap.keySet()){
                HashMap<String, Virus> classMap = virusMap.get(mainKey);
                for(String virus : findThese) {
                    if (classMap.containsKey(virus)){
                        gevondenVirussen.add(classMap.get(virus));
                    }
                }
            }
        } else {
            HashMap<String, Virus> classMap = virusMap.get(classification);
            for(String virus : findThese) {
                if (classMap.containsKey(virus)){
                    gevondenVirussen.add(classMap.get(virus));
                }
            }
        }
        return gevondenVirussen;
    }

    /**
     * Methode die de Virus objecten gaat sorteren
     * @param sortThis ArrayList met de ongesorteerde virusobjecten
     * @return ArrayList met de gesorteerde virus objecten
     */
    private ArrayList<Virus> sorteer(ArrayList<Virus> sortThis){
        if(sortBy != null){
            Virus.setCompareOn(sortBy);             // Laat de virus object weten waarop er gesorteerd moet worden
        }
        Collections.sort(sortThis);                 // Collection laat mij de ArrayList sorteren aan de hand van de compareTo methode
        return sortThis;

    }

    /**
     * Methode om de virus tax ID's te verzamelen om ze weer in de gui lijst te plaatsen
     * @param virusList De ArrayList met de Virus objecten waarvan de ID's gehaald moeten worden
     * @return ArrayList met de ID's van de virus objecten
     */
    private ArrayList<String> extractID(ArrayList<Virus> virusList){
        ArrayList<String> virusIDs = new ArrayList<>();
        for(Virus virus : virusList){
            virusIDs.add(virus.getTaxID());
        }
        return virusIDs;
    }

    /**
     * Methode voor de VirusInfoScreen om de virus object op te halen
     * @param virus String met het virus id van de gezochte Virus object
     * @param classification String met de virusMap key van de juiste classificatie
     * @return Het gevonden Virus object of null als het niks vind
     */
    public static Virus vindVirus(String virus, String classification){
        if (classification.equals("Alles")){
            for(String mainKey : virusMap.keySet()){
                HashMap<String, Virus> classMap = virusMap.get(mainKey);
                if (classMap.containsKey(virus)){
                    return classMap.get(virus);
                }
            }
        } else {
            HashMap<String, Virus> classMap = virusMap.get(classification);
            if (classMap.containsKey(virus)){
                return classMap.get(virus);
            }
        }
        return null;
    }
}
