/*
#########################################################################
# Toetscode:     Praktische Opdracht kans 1 Bi6a 2017-2018              #
# Datum:         29 januari 2018                                        #
# Gemaakt door:  Alex Janse                                             #
# Studenten nr:  577754                                                 #
# Klas ID:       BIN-2A                                                 #
# School:        Hogeschool Arnhem en Nijmegen (HAN)                    #
# Opleiding:     Bio-Informatica                                        #
#-----------------------------------------------------------------------#
# Opmerking:    "Mocht mijn code lijken op die van andere dan           #
#               is dat te verklaren doordat ik veel heb samengewerkt    #
#               met andere studenten van leerjaar 2.                    #
#               Hierbij wil ik met name benadrukken dat ik dit          #
#               blok vooral Damian Bolwerk heb geholpen en met          #
#               hem de grootste kans op is dat de codes op elkaar       #
#               lijken."                                                #
# Opmerking2:   "In het programma heb ik op drie manieren commentaar    #
#               gegeven. 1: Boven elke methode voor de JavaDoc,         #
#               2: tussendoor met commentaar over opvolgende regels     #
#               en 3: Rechts naast sommige regels als de reden of       #
#               functie niet duidelijk genoegd is."                     #
#########################################################################
 */
package toetsPackage;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * Class om de applicatie te visualiseren en methodes aan te roepen.
 * @author Alex Janse
 * @since 29-01-2018
 * @version 1.00
 */
public class VirusGUI extends JFrame implements ActionListener, ItemListener, MouseListener,ListSelectionListener{

    private JTextArea emotieArea,statusArea;
    private JTextField bestandInput;
    private JComboBox<String> classificationBox, host1Box, host2Box;
    private JButton chooseBestandButton, onlineBestandButton,openURLButton;
    private ButtonGroup buttonGroup;
    private static int appWidth = 760, appHeight = 840, panelWidth = appWidth-10;
    private Font headerFont = new Font("Ariel",Font.BOLD,30),
            subTitlesFont = new Font("Ariel",Font.BOLD,12);
    private Color fontColor = Color.cyan, backgroundColor = Color.black, sortFontColor = Color.yellow;
    private String defaultURLMessage = "Voer hier uw URL in...";
    private JList<String> virus1List, virus2List, intersectList;
    private DefaultListModel<String> model1, model2, modelIntersect;


    /**
     * Roept de functies en methodes aan om de applicatie op te starten.
     */
    public static void main(String[] args) {
        VirusGUI frame = new VirusGUI();
        frame.setSize(appWidth, appHeight);
        frame.setResizable(false);
        frame.setTitle("ViHo App");
        frame.createGUI();
        frame.setVisible(true);
    }

    /**
     * Methode om de GUI op te bouwen.
     */
    private void createGUI(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);                        // Zorgt voor dat het programma echt afgesloten wordt als de gebruiker de app wegklikt

        Container window = getContentPane();
        window.setLayout(new FlowLayout());
        window.setBackground(backgroundColor);


        // Aanmaak van de ruimte om de titel en omschrijving van het programma in te plaatsten
        JPanel titelPanel = new JPanel();
        titelPanel.setPreferredSize(new Dimension(panelWidth,80));
        titelPanel.setBackground(backgroundColor);
        window.add(titelPanel);

        JLabel titelLabel = new JLabel("ViHo App");
        titelLabel.setFont(headerFont);
        titelLabel.setForeground(Color.green);
        titelPanel.setBackground(backgroundColor);
        titelPanel.add(titelLabel);

        JTextArea uitlegArea = new JTextArea("Met ViHo App kunt u virussen "+
                "van verschillende gastheren met elkaar vergelijken en "+
                "informatie opvragen. U kunt hiervoor een lokaal of "+
                "online een VirusHost.tsv bestand laden.");
        uitlegArea.setPreferredSize(new Dimension(panelWidth-20,30));
        uitlegArea.setEditable(false);                                                      // Voorkomt dat de uitlegtekst kan worden aangepast
        uitlegArea.setFont(subTitlesFont);
        uitlegArea.setForeground(fontColor);
        uitlegArea.setBackground(backgroundColor);
        uitlegArea.setLineWrap(true);                                                       /* Dit zorgt ervoor dat als de regel dat wordt ingevoerd
                                                                                            * langer is dan de grote van de textarea het dan doorgaat op de volgende regel */
        uitlegArea.setWrapStyleWord(true);
        titelPanel.add(uitlegArea);


        // Ruimte waarin de status van het programma wordt verteld
        JPanel statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(panelWidth,180));
        statusPanel.setBackground(backgroundColor);
        statusPanel.setBorder(BorderFactory.createTitledBorder(
                null,"Status", TitledBorder.CENTER,
                TitledBorder.TOP,new Font("Ariel",Font.BOLD,18),Color.white));     // Zorgt ervoor dat er een lijn rond de panel komt te staan met een titel
        window.add(statusPanel);

        emotieArea = new JTextArea();
        emotieArea.setFont(new Font("Courier new",Font.BOLD,15));
        statusPanel.add(emotieArea);

        statusArea = new JTextArea("Welkom "+System.getProperty("user.name")+"!"+
                "\nKies een bestand om te beginnen.");
        statusArea.setFont(new Font("Courier new",Font.BOLD,15));
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setBackground(backgroundColor);
        statusArea.setForeground(fontColor);

        Emotie.getWelkom(emotieArea, statusArea);                                               // Zorgt ervoor dat de gebruiker welkom wordt geheten met de juiste emotie en text

        JScrollPane statusScrollPane = new JScrollPane(statusArea);                             // Zorgt ervoor dat de statustekst scrollbaar wordt zodat je ook de geschiedenis ervan kan bekijken
        statusScrollPane.setPreferredSize(new Dimension(400,130));
        statusScrollPane.setBorder(null);                                                       // Verwijderd de rand om de scrollPane
        statusPanel.add(statusScrollPane);


        // Ruimte waarin de keuze wordt gemaakt om zelf een bestand te openen of de officele online bestand te gebruiken
        JPanel bestandPanel = new JPanel();
        bestandPanel.setLayout(new GridLayout(2,2));
        bestandPanel.setPreferredSize(new Dimension(panelWidth,80));
        bestandPanel.setBackground(backgroundColor);
        bestandPanel.setBorder(BorderFactory.createTitledBorder(null,"Kies uw bron:", TitledBorder.CENTER,
                TitledBorder.TOP,new Font("Ariel",Font.BOLD,18),Color.white));
        window.add(bestandPanel);

        bestandInput = new JTextField(defaultURLMessage);
        bestandInput.setBackground(Color.white);
        bestandInput.setForeground(Color.gray);
        bestandInput.setFont(new Font("Ariel",Font.ITALIC,16));
        bestandInput.addMouseListener(this);                                                    // Hierdoor kan de default tekst worden verwijderd wanneer de grbuiker op de textfield klikt om een URL in te vullen
        bestandPanel.add(bestandInput);

        openURLButton = new JButton("Open URL");
        openURLButton.setForeground(Color.white);
        openURLButton.setBackground(Color.blue);
        openURLButton.addActionListener(this);
        bestandPanel.add(openURLButton);

        chooseBestandButton = new JButton("Lokale bestand openen");
        chooseBestandButton.addActionListener(this);
        chooseBestandButton.setBackground(Color.yellow);
        bestandPanel.add(chooseBestandButton);

        onlineBestandButton = new JButton("Online default VirusHostdb.tsv bestand openen");
        onlineBestandButton.addActionListener(this);
        onlineBestandButton.setBackground(Color.green);
        bestandPanel.add(onlineBestandButton);

        // Ruimte om te kiezen welke virussen en op welke volgorde ze getoond moeten worden
        JPanel optionPanel =  new JPanel();
        optionPanel.setPreferredSize(new Dimension(panelWidth,170));
        optionPanel.setBackground(backgroundColor);
        optionPanel.setBorder(BorderFactory.createTitledBorder(null,"Opties", TitledBorder.CENTER,
                TitledBorder.TOP,new Font("Ariel",Font.BOLD,18),Color.white));
        window.add(optionPanel);

        JPanel sortPanel = new JPanel();
        sortPanel.setLayout(new GridLayout(3,1));
        sortPanel.setPreferredSize(new Dimension(160,130));
        sortPanel.setBackground(backgroundColor);
        sortPanel.setBorder(BorderFactory.createTitledBorder(null,"Sorteer op:", TitledBorder.CENTER,
                TitledBorder.TOP,new Font("Ariel",Font.BOLD,12),sortFontColor));
        optionPanel.add(sortPanel);

        JRadioButton idButton = new JRadioButton("ID");
        idButton.addItemListener(this);                         /* De reden waarom ik heb gekozen voor itemlistener ipv
                                                                   * actionlistener is omdat itemListener alleen een event opgeeft
                                                                   * als er iets veranderd is en actionListener altijd een event
                                                                   * opgeeft ookal was de radioButton al aangevinkt */
        idButton.setBackground(backgroundColor);
        idButton.setForeground(sortFontColor);
        sortPanel.add(idButton);

        JRadioButton classButton = new JRadioButton("Classificatie");
        classButton.setSelected(true);                                              // Deze zal hierdoor bij default aan staan
        classButton.addItemListener(this);
        classButton.setBackground(backgroundColor);
        classButton.setForeground(sortFontColor);
        sortPanel.add(classButton);

        JRadioButton hostButton = new JRadioButton("Aantal Gastheren");
        hostButton.addItemListener(this);
        hostButton.setBackground(backgroundColor);
        hostButton.setForeground(sortFontColor);
        sortPanel.add(hostButton);

        buttonGroup = new ButtonGroup();                                            // Zorgt ervoor dat er maar 1 radioknop kan worden geselecteerd
        buttonGroup.add(idButton);
        buttonGroup.add(classButton);
        buttonGroup.add(hostButton);

        JLabel classLabel = new JLabel("Kies classificatie:");
        classLabel.setFont(subTitlesFont);
        classLabel.setBackground(backgroundColor);
        classLabel.setForeground(fontColor);
        optionPanel.add(classLabel);

        classificationBox = new JComboBox<>();
        classificationBox.setPreferredSize(new Dimension(200,30));
        classificationBox.addActionListener(this);
        classificationBox.setEnabled(false);
        optionPanel.add(classificationBox);

        // Ruimte om voor 1 gastheer de virussen op te halen
        JPanel host1Panel =  new JPanel();
        host1Panel.setPreferredSize(new Dimension(panelWidth/3-5,260));
        host1Panel.setBackground(backgroundColor);
        host1Panel.setBorder(BorderFactory.createTitledBorder(null,"Gastheer 1", TitledBorder.CENTER,
                TitledBorder.TOP,new Font("Ariel",Font.BOLD,18),Color.white));
        window.add(host1Panel);

        JLabel host1Label = new JLabel("Gastheer 1 ID:");
        host1Label.setForeground(fontColor);
        host1Label.setFont(subTitlesFont);
        host1Label.setBackground(backgroundColor);
        host1Panel.add(host1Label);

        host1Box = new JComboBox<>();
        host1Box.setPreferredSize(new Dimension(220,30));
        host1Box.addActionListener(this);
        host1Box.setEnabled(false);
        host1Panel.add(host1Box);

        JLabel virus1Label = new JLabel("Viruslijst:");
        virus1Label.setBackground(backgroundColor);
        virus1Label.setFont(subTitlesFont);
        virus1Label.setForeground(fontColor);
        host1Panel.add(virus1Label);

        model1 = new DefaultListModel<>();                                                      // DefaulListModel zorgt ervoor dat ik de lijst kan aanvullen en aanpassen
        virus1List = new JList<>(model1);
        virus1List.setFont(new Font("Courier new",Font.BOLD,15));
        virus1List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);                       // Zorgt ervoor dat er maar één onderdeel kan worden geselecteerd
        virus1List.addListSelectionListener(this);                                              // Zorgt ervoor dat er een event ontstaat als er een onderdeel van de lijst wordt aangeklikt

        JScrollPane virus1ScrollPane = new JScrollPane(virus1List);
        virus1ScrollPane.setPreferredSize(new Dimension(220,130));
        virus1ScrollPane.setBorder(null);                                                       
        host1Panel.add(virus1ScrollPane);


        // Ruimte om de overeenkomst tussen gastheer 1 en gastheer 2 virussen te laten zien
        JPanel intersectPanel =  new JPanel();
        intersectPanel.setPreferredSize(new Dimension(panelWidth/3-5,260));
        intersectPanel.setBackground(backgroundColor);
        intersectPanel.setBorder(BorderFactory.createTitledBorder(null,"Overlap", TitledBorder.CENTER,
                TitledBorder.TOP,new Font("Ariel",Font.BOLD,18),Color.white));
        window.add(intersectPanel);

        modelIntersect = new DefaultListModel<>();
        intersectList = new JList<>(modelIntersect);
        intersectList.setFont(new Font("Courier new",Font.BOLD,15));
        intersectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        intersectList.addListSelectionListener(this);

        JScrollPane intersectScrollPane = new JScrollPane(intersectList);
        intersectScrollPane.setPreferredSize(new Dimension(220,220));
        intersectScrollPane.setBorder(null);
        intersectPanel.add(intersectScrollPane);


        // Ruimte om van de tweede gastheer virussen te laten zien
        JPanel host2Panel =  new JPanel();
        host2Panel.setPreferredSize(new Dimension(panelWidth/3-5,260));
        host2Panel.setBackground(backgroundColor);
        host2Panel.setBorder(BorderFactory.createTitledBorder(null,"Gastheer 2", TitledBorder.CENTER,
                TitledBorder.TOP,new Font("Ariel",Font.BOLD,18),Color.white));
        window.add(host2Panel);

        JLabel host2Label = new JLabel("Gastheer 2 ID:");
        host2Label.setForeground(fontColor);
        host2Label.setFont(subTitlesFont);
        host2Label.setBackground(backgroundColor);
        host2Panel.add(host2Label);

        host2Box = new JComboBox<>();
        host2Box.setPreferredSize(new Dimension(220,30));
        host2Box.addActionListener(this);
        host2Box.setEnabled(false);
        host2Panel.add(host2Box);

        JLabel virus2Label = new JLabel("Viruslijst:");
        virus2Label.setBackground(backgroundColor);
        virus2Label.setFont(subTitlesFont);
        virus2Label.setForeground(fontColor);
        host2Panel.add(virus2Label);

        model2 = new DefaultListModel<>();
        virus2List = new JList<>(model2);
        virus2List.setFont(new Font("Courier new",Font.BOLD,15));
        virus2List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        virus2List.addListSelectionListener(this);

        JScrollPane virus2ScrollPane = new JScrollPane(virus2List);
        virus2ScrollPane.setPreferredSize(new Dimension(220,130));
        virus2ScrollPane.setBorder(null);
        host2Panel.add(virus2ScrollPane);
        
        // Dit zorgt ervoor dat de applicatie in het midden van het scherm opent.
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

    }

    /**
     * Methode die na gebruik van een knop of keuzemenu de bijbehoorende methode(s) aanroept.
     * @param event Bevat de bron van de gebruikte knop of keuzemenu
     */
    @Override
    public void actionPerformed(ActionEvent event){
        if(event.getSource() == chooseBestandButton){
            fileExceptionCatcher(-1);                               // Zorgt ervoor dat de bestandsknopen uitgeschakeld worden en de gebruiker weet dat het programma bezig is
            VirusLogica.openLokaalBestand(this);
        } else if (event.getSource() == onlineBestandButton){
            fileExceptionCatcher(-1);
            VirusLogica.openOnlineBestand(this, "ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv");
        } else if(event.getSource() == openURLButton){
            String website = bestandInput.getText();
            if (!website.equals(defaultURLMessage)) {
                fileExceptionCatcher(-1);
                VirusLogica.openOnlineBestand(this, website);
            }
        }else if(classificationBox.getItemCount() !=0 &&
                host1Box.getItemCount() != 0 &&
                host2Box.getItemCount() != 0){                              //Hierdoor hoef ik niet telkens de status verandering per event te aan te roepen
            Emotie.getLoading(emotieArea, statusArea);
            if(event.getSource() == classificationBox || event.getSource() == host1Box || event.getSource() == host2Box){
                VirusLogica.getVirusses((String) host1Box.getSelectedItem(), model1);
                VirusLogica.getVirusses((String) host2Box.getSelectedItem(), model2);
            }
            statusArea.append("\n###############\n");
            statusArea.append("De virussen zijn gevonden en onderaan weergegeven!");
            Emotie.getGelukt(emotieArea, statusArea);
        }
    }

    /**
     * Methode die na gebruik van de radioknoppen de bijbehoorende methode aanroept.
     * @param event Bevat bron van de gekozen radioknop.
     */
    @Override
    public void itemStateChanged(ItemEvent event){
        if(model1.size() > 0
          || model2.size() > 0){
            Emotie.getLoading(emotieArea,statusArea);
            VirusLogica.getVirusses((String) host1Box.getSelectedItem(), model1);
            VirusLogica.getVirusses((String) host2Box.getSelectedItem(), model2);
            statusArea.append("\n###############\n");
            statusArea.append("De virussen zijn gesorteerd en onderaan weergegeven!");
            Emotie.getGelukt(emotieArea, statusArea);
        }
    }

    /**
     * Methode die na gebruik van de viruslijsten wordt aangeroepen om zo de bijbehoorende methodes aan te roepen.
     * @param event bevat bron van de gebruikte viruslijst
     */
    public void valueChanged(ListSelectionEvent event){
        if(event.getValueIsAdjusting()) {
            String virusID = "";
            if (event.getSource() == virus1List) {
                virusID = virus1List.getSelectedValue();
            } else if (event.getSource() == virus2List) {
                virusID = virus2List.getSelectedValue();
            } else {
                virusID = intersectList.getSelectedValue();
            }
            if(!virusID.equals("NO MATCH FOUND!")) {
                VirusInfoScreen infoScreen = new VirusInfoScreen(virusID, (String) classificationBox.getSelectedItem());
            }
        }
    }

    /**
     * Aangezien het niet mogelijk is om exceptions tijdens het runnen van threads
     * optegooien is deze methode gemaakt om dat toch een gelijke effect te creeëren.
     * Ook zorgt de methode voor het uit- en aanzetten van de bestandknoppen.
     * @param functie Een integer waarmee de methode weet welke exception die moet regeren
     */
    public void fileExceptionCatcher(int functie) {
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
     * Functie om de de combobox van classificaties in te vullen vanuit de VirusLogica class.
     * Aangezien dit een visuele functie is staat het in de GUI class i.p.v. de logica class.
     * @param lijst Een HashSet met de classificaties
     */
    public void fillClassificationBox(HashSet<String> lijst){
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
     * Zorgt er voor dat de comboboxen van de gastheren gevuld kunnen worden.
     * @param hostLijst Een String[] die de gastheer namen en ID'S BEVAT
     */
    public void fillHostBoxes(String[] hostLijst){
        Arrays.sort(hostLijst);
        for(String host : hostLijst){
            host1Box.addItem(host);
            host2Box.addItem(host);
        }
        host1Box.setEnabled(true);
        host2Box.setEnabled(true);
    }

    /**
     * Deze methode zorgt ervoor dat de huidige geselecteerde radiobutton kan worden opgehaald
     * @return String met daarin de huidige sorteerfunctie
     */
    public String getSortBy(){
        Enumeration<AbstractButton> buttons = buttonGroup.getElements();       // Hierin komen alle radiobuttons te staan
        while(buttons.hasMoreElements()){                                      // Ga door tot dat er geen radioknop meer over is
            AbstractButton button = buttons.nextElement();                     // Pak de eerst volgende knop
            if (button.isSelected()){
                return button.getText();                                       // Retourneer de tekst van de knop als die geselecteerd is
            }
        }
        return null;                                                            // Hoewel er altijd een radio knop geselecteerd is, is deze meer om foutmeldingen te voorkomen
    }

    public String getClassification(){
        return (String)classificationBox.getSelectedItem();
    }

    public DefaultListModel<String> getModel1(){return model1;}
    public DefaultListModel<String> getModel2() { return model2; }
    public DefaultListModel<String> getModelIntersect() { return modelIntersect;}

    /**
     * Functie om de default text van de url invoerveld te verwijderen en de font aan te passen
     * @param e Event die wordt opgegooid als iemand op de bestand input veld klikt.
     */
    public void mouseClicked(MouseEvent e){
        bestandInput.setText("");
        bestandInput.setBackground(Color.white);
        bestandInput.setForeground(Color.blue);
        bestandInput.setFont(new Font("Ariel",Font.BOLD,16));
    }

    //Deze methodes worden niet gebruikt maar moesten wel geimplementeerd worden voor de MouseActionListerner
    public void mouseReleased(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
}
