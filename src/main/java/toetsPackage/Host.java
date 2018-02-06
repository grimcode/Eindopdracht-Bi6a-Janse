package toetsPackage;

import java.util.HashSet;

/**
 * Klas om de host informatie in op te slaan
 */
public class Host {

    private String taxID, name, lineage;
    private HashSet<String> pmidID = new HashSet<>(), virusSet = new HashSet<>();

    /**
     * Constructor om het object te vullen en aan te maken
     * @param taxID
     * @param name
     * @param lineage
     */
    public Host(String taxID,String name,String lineage){
        this.name = name;
        this.taxID = taxID;
        this.lineage = lineage;
    }

    public void addToVirusSet(String ID){ virusSet.add(ID);}
    public void addToPmidID(String ID){ pmidID.add(ID);}

    public String getName() {return name;}
    public HashSet<String> getVirusSet() {return virusSet;}
    public String getLineage() {return lineage;}
    public String getTaxID() {return taxID;}
    public HashSet<String> getPmidID(){return pmidID;}

    @Override
    public String toString(){
        return name+" ("+taxID+")";
    }
}
