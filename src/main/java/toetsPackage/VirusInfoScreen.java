package toetsPackage;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Klas om de informatie van de virussen en gastheren te laten zien
 * @author Alex Janse
 * @since 03-02-2018
 * @version 1.00
 */
public class VirusInfoScreen extends JFrame {

    private Virus virus;
    private JTextArea infoArea;
    private HashMap<String, Host> hostMap;
    private int appWidth = 700, appHeight = 300;

    /**
     * Roept de methodes aan om de frame aan te maken en te laten zien
     * @param virusID een String met daarin de virus waarvan informatie gezocht moet worden
     * @param classMap een String met de key van de HashMap waarin de virus in bevind
     */
    public VirusInfoScreen(String virusID, String classMap){
        this.virus = Sorter.vindVirus(virusID,classMap);                // Methode aan roepen om het bijbehoorende virus object op te halen
        this.hostMap = VirusLogica.getHostMap();
        this.setSize(appWidth,appHeight);
        this.setResizable(false);
        this.setTitle("Information about: "+virusID);
        this.createInfoScreen();
        this.setVisible(true);
    }

    /**
     * Methode om de informatie scherm op te bouwen
     */
    private void createInfoScreen(){
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Container window = getContentPane();
        window.setLayout(new GridLayout(1,1));

        infoArea = new JTextArea();
        infoArea.setFont(new Font("Ariel",Font.BOLD,14));
        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setPreferredSize(new Dimension(appWidth-20,appHeight-20));
        getInfo();
        infoArea.setCaretPosition(0);
        window.add(scrollPane);

        // Dit zorgt ervoor dat de applicatie in het midden van het scherm opent.
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }

    /**
     * Methode om de informatie te plaatsen in de textarea
     */
    private void getInfo(){
        infoArea.setText("Information about: "+virus);
        infoArea.append("\nNaam:\t\t"+virus.getName());
        infoArea.append("\nTax ID:\t\t"+virus.getTaxID());
        infoArea.append("\nClassificatie:\t\t"+virus.getClassification());
        infoArea.append("\nLineage:\t\t"+virus.getLineage());
        infoArea.append("\nRefSeq ID:\t\t");
        for (String id : virus.getRefseqID()){
            infoArea.append(id+" ");
        }
        infoArea.append("\nKEGG Genome:\t"+virus.getKeggGenome());
        infoArea.append("\nKEGG Disease:\t");
        for (String id : virus.getKeggDisease()){
            infoArea.append(id+" ");
        }
        infoArea.append("\nDiseases:\t\t"+virus.getDisease());
        infoArea.append("\n\nKnown host:");

        for(String hostID : virus.getHostSet()){
            Host host = hostMap.get(hostID);
            infoArea.append("\nName:\t\t"+host.getName());
            infoArea.append("\nTax ID:\t\t"+host.getTaxID());
            infoArea.append("\nLineage:\t\t"+host.getLineage());
            infoArea.append("\nPMID ID:\t\t");
            for(String id : host.getPmidID()){
                infoArea.append(id+" ");
            }
            infoArea.append("\n\n");
        }
    }
}
