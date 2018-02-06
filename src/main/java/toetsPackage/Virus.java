package toetsPackage;

import java.util.HashSet;

/**
 * Klas waarin virus objecten worden gemaakt en die bijbehorende informatie in wordt opgeslagen
 * @author Alex Janse
 * @since 03-02-2018
 * @version 1.00
 */
public class Virus implements Comparable{

    private String taxID, name, lineage, classification, keggGenome, disease;
    private HashSet<String> refseqID = new HashSet<>(),
                            keggDisease = new HashSet<>(),
                            hostSet = new HashSet<>();
    private static String compareOn = "classification";

    /**
     * Constructor waarin het object wordt aangemaakt en gevuld
     * @param taxID
     * @param name
     * @param lineage
     * @param classification
     * @param keggGenome
     * @param disease
     */
    public Virus(String taxID,String name,String lineage, String classification,String keggGenome,String disease){
        this.taxID = taxID;
        this.name = name;
        this.lineage = lineage;
        this.classification = classification;
        this.keggGenome = keggGenome;
        this.disease = disease;
    }

    public void addToRefseqID(String ID){ refseqID.add(ID);}
    public void addToKeggDisease(String ID){ keggDisease.add(ID);}
    public void addToHostSet(String ID){ hostSet.add(ID);}

    public static void setCompareOn(String option){ compareOn = option;}

    public HashSet<String> getHostSet() {return hostSet;}
    public HashSet<String> getKeggDisease() {return keggDisease;}
    public HashSet<String> getRefseqID() {return refseqID;}
    public String getClassification() {return classification;}
    public String getDisease() {return disease;}
    public String getKeggGenome() {return keggGenome;}
    public String getName() {return name;}
    public String getTaxID() {return taxID;}
    public String getLineage() {return lineage;}
    public Integer getSizeHost(){return hostSet.size();}

    @Override
    /**
     * een override methode om er voor te zorgen dat zowel de naam als de taxID wordt geprint bij aanroepen van deze methode ipv objectverwijzing
     */
    public String toString(){return name+" ("+taxID+")";}

    /**
     * Methode om de virus object te vergelijken.
     * @param o Object die vergeleken moet worden met het huidige Virus object
     * @return een Integer: 0 als de Virus objecten gelijk zijn, -1 als de vergelijkMet lager ligt en 1 als het hoger is dat de huidige Virus
     */
    public int compareTo(Object o){
        Virus vergelijkMet = (Virus)o;
        switch (compareOn.toLowerCase()){

            case "classification":
                return this.classification.compareTo(vergelijkMet.classification);

            case "id":
                Integer idThis = Integer.parseInt(this.taxID),
                        idThat = Integer.parseInt(vergelijkMet.taxID);
                return idThis.compareTo(idThat);

            case "aantal gastheren":
                return this.getSizeHost().compareTo(vergelijkMet.getSizeHost());

            default:
                return 0;
        }

    }
}
