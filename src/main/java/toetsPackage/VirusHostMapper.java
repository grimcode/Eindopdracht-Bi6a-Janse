package toetsPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Klas die het inhoud van een bestand omzet in twee HashMappen
 */
public class VirusHostMapper extends Thread{

    private HashMap<String,HashMap<String,Virus>> virusMap; // Zie virusLogica waarom er voor hashmap is gekozen
    private HashMap<String, Host> hostMap;
    private ArrayList<String> inhoudBestand;
    private HashSet<String> classificatiesSet;
    private final String[] hoofdClassificaties = {"dsDNA","ssDNA","dsRNA","ssRNA","Satellites","virophage","Retro","Viroids"}; // De classificaties die gezocht worden in de lineages om de virus in de juiste hashmap te plaatsen


    /**
     * Constructor die de variabelen vult die nodig zijn om de hashMaps te vullen
     * @param inhoudBestand
     */
    public VirusHostMapper(ArrayList<String> inhoudBestand){
        this.virusMap = VirusLogica.getVirusMap();
        this.hostMap = VirusLogica.getHostMap();
        this.inhoudBestand = inhoudBestand;
        this.classificatiesSet = VirusLogica.getClassificaties();
    }

    /**
     *  Methode die aangeroepen wordt als de thread wordt gestart
     */
    public void run(){
        int counter = 0;
        for(String lijn : inhoudBestand){
            if(counter>0){
                isolate(lijn);
            }
            counter++;
        }
        VirusLogica.releaseClassAndHost();              // Methode wordt aangeroepen om de classificaties en de hosts weer te geven in de gui
    }

    /**
     * Methode om de verschillende onderdelen die we uit de regel willen te isoleren
     * @param lijn een String met een regel uit het bestand
     */
    private void isolate(String lijn){
        String virusTaxID = "N.A.", virusName = "N.A.",
                classification = "N.A.", virusLineage = "N.A.",
                keggGenome = "N.A.", disease = "N.A.",
                hostTaxID = "N.A.", hostName = "N.A.", hostLineage = "N.A.",hostMapKey = "N.A.";
        String[] refseqID = null, keggDisease = null, pmidID = null, onderdelen = lijn.split("\t");
        int counter = 0;

        for(String onderdeel : onderdelen){                         // Ik gebruik loops omdat niet alle regels even lang zijn en index dus niet mogelijk is tenzij je veel if else statements gaat maken
            onderdeel = onderdeel.trim();
            switch (counter){

                case 0:
                    virusTaxID = nullFiller(onderdeel);
                    break;

                case 1:
                    virusName = nullFiller(onderdeel);
                    break;

                case 2:
                    virusLineage = nullFiller(onderdeel);
                    String[] lineageOnderdelen = virusLineage.split(";");
                    if(lineageOnderdelen[0].trim().equals("Viruses")){
                        classification = lineageOnderdelen[1].trim();
                    } else {
                        classification = lineageOnderdelen[0].trim();
                    }
                    classification = getHoofdClass(classification);
                    classificatiesSet.add(classification); // Wordt gebruikt omlater de classificatie combobox te vullen in de gui
                    break;

                case 3:
                    refseqID = onderdeel.split(";");
                    break;

                case 4:
                    keggGenome = nullFiller(onderdeel);
                    break;

                case 5:
                    if(onderdeel.split(";").length > 0) {
                        keggDisease = onderdeel.split(";");
                    }
                    break;

                case 6:
                    disease = nullFiller(onderdeel);
                    break;

                case 7:
                    hostTaxID = nullFiller(onderdeel);
                    break;

                case 8:
                    hostName = nullFiller(onderdeel);
                    hostMapKey = hostName+" ("+hostTaxID+")";
                    break;

                case 9:
                    hostLineage = nullFiller(onderdeel);
                    break;

                case 10:
                    if(onderdeel.split(";").length > 0) {
                        pmidID = onderdeel.split(";");
                    }
                    break;

                default:
                    break;
            }
            counter++;
        }
        virusMapFiller( virusTaxID, virusName, virusLineage,
                classification, keggGenome, disease,
                hostMapKey,  refseqID,  keggDisease);
        hostMapFiller( hostMapKey, virusTaxID,
                hostTaxID, hostName,
                hostLineage,pmidID);
    }

    /**
     * Methode om lege objecten te vullen met N.A.
     * @param s De string die gecontroleerd moet worden
     * @return De string zonder aanpassingen of een string met N.A,
     */
    private String nullFiller(String s){
        if(s.equals("")){
            return "N.A.";
        } else {
            return s;
        }
    }

    /**
     * Methode om de variabelen in de virus hashmap te plaatsen
     * @param virusTaxID String met de taxatie id
     * @param virusName String met de virus naam
     * @param virusLineage String met de virus afstamming
     * @param classification String met de classificatie
     * @param keggGenome String met de KEGG genome ID
     * @param disease String met de ziekte die het virus veroorzaakt
     * @param hostMapKey String met de host id's
     * @param refseqID String array met de refseq id's
     * @param keggDisease String array met de KEGG disease ID's
     */
    private void virusMapFiller(String virusTaxID,String virusName,String virusLineage,
                                String classification,String keggGenome,String disease,
                                String hostMapKey, String[] refseqID, String[] keggDisease){

        if(!virusMap.containsKey(classification)){
            virusMap.put(classification,new HashMap<>());
        }

        HashMap<String, Virus> classMap = virusMap.get(classification);
        if(classMap.containsKey(virusTaxID)){
            updateVirus(virusTaxID,refseqID,keggDisease,hostMapKey,classMap);
        } else if(!virusTaxID.equals("N.A.")){
            classMap.put(virusTaxID,new Virus(virusTaxID,virusName,virusLineage,classification,keggGenome,disease));
            updateVirus(virusTaxID,refseqID,keggDisease,hostMapKey,classMap);
        }
    }

    /**
     * Methode om de bestaande virus hashmaps aan te vullen
     * @param virusTaxID
     * @param refseqID
     * @param keggDisease
     * @param hostMapKey
     * @param classMap
     */
    private void updateVirus(String virusTaxID, String[] refseqID,
                             String[] keggDisease, String hostMapKey,
                             HashMap<String,Virus> classMap){
        Virus firstVirus = classMap.get(virusTaxID);
        if(refseqID != null){
            for (String id : refseqID) {
                firstVirus.addToRefseqID(id);
            }
        }
        if (keggDisease != null && !keggDisease[0].equals("")){
            for (String id : keggDisease){
                firstVirus.addToKeggDisease(id);
            }
        } else {
            firstVirus.addToKeggDisease("N.A.");
        }
        if (!hostMapKey.equals("N.A.")){
            firstVirus.addToHostSet(hostMapKey);
        }
    }

    /**
     * Methode om de host hashmap te vullen
     * @param hostMapKey String met de hashmap sleutel
     * @param virusTaxID String met de virus taxatie id
     * @param hostTaxID String met de host taxatie id
     * @param hostName String met de host naam
     * @param hostLineage String met host afstamming
     * @param pmidID String array met de pubmed ID
     */
    private void hostMapFiller(String hostMapKey,String virusTaxID,
                               String hostTaxID,String hostName,
                               String hostLineage,String[] pmidID){
        if(hostMap.containsKey(hostMapKey)){
            hostMap.get(hostMapKey).addToVirusSet(virusTaxID);
        } else if(!hostMapKey.equals("N.A.")){
            hostMap.put(hostMapKey,new Host(hostTaxID,hostName,hostLineage));
            hostMap.get(hostMapKey).addToVirusSet(virusTaxID);
            if (pmidID != null) {
                for (String id : pmidID) {
                    hostMap.get(hostMapKey).addToPmidID(id);
                }
            }
        }
    }

    /**
     * Methode om de de classificaties om te zetten de default classificatie
     * @param classification String met de aan te passen classificaties
     * @return String met de aangepaste classificaties
     */
    private String getHoofdClass(String classification){
        for(String hoofd : hoofdClassificaties){
            for(String onderdeel : classification.split(" ")){
                if(onderdeel.contains(hoofd)){
                    if(hoofd.equals("Retro")){
                        return "Retrovirusses";
                    } else if(hoofd.equals("Satellites") || hoofd.equals("virophage")){
                        return "Satellite virus and Virophage";
                    }else {
                        return hoofd;
                    }
                }
            }
        }
        return "Others";
    }
}
