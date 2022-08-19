import auxiliary.*;
import machine.MachineInfo;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;

public class EnigmaConsole {


    private static IEngine engine;
    private static String configFileName;
    static Scanner reader = new Scanner(System.in);
    private static MachineSettings settings;

    public static void main(String[] args) {
        engine = new Engine();

        System.out.println("\nWelcome to the Enigma Console by Omer Gabay :)");

        boolean loop = true;
        char choice;
        do{
           printMenu();
           try {
               String input = reader.nextLine();
               choice = Character.toLowerCase(input.charAt(0));

           }catch(RuntimeException re){
               choice = 'q';
           }

           switch(choice){
               case '1':
                   loadXML();
                   break;
               case '2':
                   printMachineSpecs();
                   break;
               case '3':
                   readInputForSetup();
                   break;
               case '4':
                   createRandomSetup();
                   break;
               case '5':
                   processText();
                   break;
               case '6':
                   resetMachine();
                   break;
               case '7':
                   printHistory();
                   break;
               case '8':
                   backupBonusMenu();
                   break;
               case '9': case 'q':
                   System.out.println("Are you sure you want to exit? (y/n)");
                   char c = reader.nextLine().charAt(0);
                   if(Character.toLowerCase(c) == 'y') {
                       loop = false;
                       continue;
                   }else{
                       System.out.println("exit was declined by the user");
                   }
                   break;

               default:
                   System.out.println("Please make sure to select a number from the options");
                   System.out.println("to exit you can also press q");
                   break;
           }
//            try {
//                Thread.sleep(1000);
                pressEnterToContinue();

//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }while(loop);
        System.out.println("Goodbye!");
        System.out.print("Going to sleep zzz");


    }

    private static void backupBonusMenu() {
        System.out.println("Please choose one of the 2 options:");
        System.out.println("1. Load Machine Backup File");
        System.out.println("2. Create Machine Backup and Save it");
        char choice = reader.next().trim().charAt(0);
        String path;
        switch(choice) {
            case '1':
                System.out.println("Please give the full path to Enigma backup file");
                path = reader.next().trim();
                try {
                    FileInputStream fileIn = new FileInputStream(path);
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    engine = (IEngine) in.readObject();
                    settings = (MachineSettings) engine.getEnigmaSettings().getData();
                    in.close();
                    fileIn.close();

                }catch(FileNotFoundException e){
                    System.out.println("Enigma Backup File was not Found, Check Path");
                    return;
                }
                catch (ClassNotFoundException e) {
                    System.out.println("Error in Deserializing Enigma Object");
                    return;
                }
                catch (IOException i) {
                    i.printStackTrace();
                    return;
                }
                break;

            case '2':
                if (!engine.isLoaded()) {
                    System.out.println("You do not have a machine to save\nPlease go through step 1 first");
                    return;
                }
                System.out.println("Please input the full path where you want to save your backup");
                path = reader.next().trim();
                System.out.println("How should I name your backup? You can skip this choice by pressing Enter");
                String filename = reader.next().trim();
                if (filename.isEmpty())
                    filename = configFileName;
                filename = filename + ".buk";


                try {
                    FileOutputStream fileOut = new FileOutputStream(path + "\\" + filename);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(engine);
                    out.close();
                    fileOut.close();
                    System.out.println("Successfully Created Enigma backup - " + filename);
                    System.out.println("Full Path:" + path+"\\"+filename);

                } catch (RuntimeException | IOException re) {
                    System.out.println("Serialization Error");
                }
                break;



            default:
                System.out.println("I did not understand your choice, please select an option from the menu");
        }
    }

    private static void printHistory() {
        if(!engine.isLoaded()){
            System.out.println("Enigma was not loaded! Please go through step 1");
            return;
        }
        if(!engine.isMachinePresent()){
            System.out.println("No History, Please go through machine setup - step 3 or 4");
            return;
        }
        List<?> history = (List<?>) engine.showHistory().getData();

        if(history.isEmpty()){
            System.out.println("Please make sure to load xml config file and choose setup for the machine");
        }
        history.forEach(System.out::println);
    }

    private static void printMachineSpecs() {
        if(!engine.isLoaded()){
            System.out.println("Enigma Machine was not loaded - please go through step 1 first");
            return;
        }
        if(!engine.isMachinePresent()){
            System.out.println("Please make sure to setup the machine first");
            System.out.println("Go to step 3 if you want to choose manually your Enigma setup");
            System.out.println("Or go to step 4 to start your Enigma with random setup");
            return;
        }
        MachineInfo machine =(MachineInfo) engine.displayMachine().getData();
        System.out.println("Printing your Enigma...\n");
        System.out.println("Your Enigma is using " + machine.getNumOfRotors() +"/" + engine.getNumOfRotors() + " rotors.");
        System.out.println("Number of Available Reflectors - " + engine.getNumOfReflectors());
        System.out.println("Your Enigma processed " + machine.getMessageCount() + " messages");
        System.out.println("Enigma Initial Setup " + machine.toString());
        System.out.println("Enigma current configuration - " + machine.getCurrentConfiguration());

    }

    public static void loadXML(){
        System.out.println("To start encrypting messages please give me the path to the machine configuration file");
        System.out.println("The file should be in .xml format");
        String folder = "C:\\Users\\omerg\\IdeaProjects\\Enigma\\console_ui\\src\\resources\\";
        String fileName = reader.nextLine().trim();

        try{
            File xml = new File(folder+fileName);
            settings = (MachineSettings) engine.loadMachineFromXml(xml).getData();
            System.out.printf("Machine loaded successfully from %s", xml.getName());
            // get file name without extension
            int pos = xml.getName().lastIndexOf('.');
            configFileName = xml.getName().substring(0,pos);
        }catch(FileNotFoundException e){
            System.out.println("Configuration file was not found, make sure you gave the right path!");
        }catch (JAXBException e) {
            e.printStackTrace();
            System.out.println("XML parsing error. Please check that your xml is in the right format and close the file if it's open");
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    private static void readInputForSetup(){
        if(!engine.isLoaded()){
            System.out.println("Please load machine configuration file before setup phase");
            return;
        }
        List<Integer> rotorIDs = readRotorsList();
        List<Character> positions = readRotorsPositions();
        RomanNumeral reflectorID = readReflectorChoice();
        HashMap<Character,Character> pb = readPlugboard();

        try {
            engine.setupMachine(rotorIDs, positions, reflectorID, pb);
        }catch(RuntimeException re){
            System.out.print("Error in Setup - " + re.getMessage());
            return;
        }

        System.out.println("Printing your choices");
        System.out.println("Rotors - " + rotorIDs);
        System.out.println("Rotors Positions - " + positions);
        System.out.println("Reflector used " + reflectorID);
        System.out.print("Plugboard chosen " + pb);

    }

    private static HashMap<Character, Character> readPlugboard() {
        HashMap<Character, Character> pb = new HashMap<>();
        Alphabet abc = settings.getAlphabet();
        System.out.println("Please Input any plugs you want in the machine. Notice that this step is optional");
        System.out.println("Write in a single line pairs of letters from your alphabet without spaces in between, if you want you can add '=' sign between the letters i.e A=B");
        StringTokenizer st = new StringTokenizer(reader.nextLine().trim());

        while(st.hasMoreTokens()){
            String pair = st.nextToken();
            char c1, c2;
            if(pair.length() == 2){
                c1 = pair.charAt(0);
                c2 = pair.charAt(1);
            } else if (pair.length() == 3) {
                c1 = pair.charAt(0);
                c2 = pair.charAt(2);
                if(pair.charAt(1) != '='){
                    System.out.println("Invalid input missing '='");
                    continue;
                }

            }else{
                System.out.println("Invalid input - please make sure to input the pairs correctly");
                continue;
            }
            if(!abc.isLetter(c1) || !abc.isLetter(c2)){
                System.out.println("Invalid! One of your letters is not in the alphabet. Check that you use the right case (" + c1+c2+ ")");
                continue;
            }
            if(pb.containsKey(c1)){
               System.out.println("Invalid input - " + c1 + " was already paired!");
               continue;
            }
            if(pb.containsKey(c2)){
                System.out.println("Invalid input - " + c2 + " was already paired!");
                continue;
            }
            pb.put(c1,c2);
            pb.put(c2,c1);
        }

        return pb;
    }

    private static RomanNumeral readReflectorChoice() {
        RomanNumeral max = settings.getMaximalReflector();
        boolean failed = false;
        RomanNumeral response;
        do{
            System.out.println("Please choose your reflector from " + RomanNumeral.I+ " to "+ max);
            String input = reader.nextLine().trim().toUpperCase();
            try {
                response = RomanNumeral.valueOf(input);
                failed = false;
            }catch (Exception e){
                response = RomanNumeral.Undefined;
            }
            if(response == RomanNumeral.Undefined || response.getValue() > max.getValue()){
                failed = true;
                System.out.println("Invalid Roman Number - please make sure your input is a roman in range\n\n");
            }
        }while(failed);
        return response;
    }


    private static void createRandomSetup(){
        if(!engine.isLoaded()){
            System.out.println("Please load machine configuration file before setup phase");
            return;
        }
        EngineResponse<?> response = engine.setupMachineAtRandom();
        System.out.println(response.getData());
    }



    private static void processText(){
        if(!engine.isLoaded()){
            System.out.println("Enigma config file was not loaded! Please go through step 1 first.");
            return;
        }
        if(!engine.isMachinePresent()){
            System.out.println("Enigma machine was not setup. Please go through steps 3 or 4 to choose your Enigma setup");
            return;
        }
        System.out.println("Input your text to Enigma...");
        String text = reader.nextLine().trim();
        Alphabet abc = settings.getAlphabet();
        StringBuilder sb = new StringBuilder();
        boolean errMessage = false;
        for(char letter : text.toCharArray()){
            if(!abc.isLetter(letter)){
                errMessage = true;
                sb.append('^');
            }else{
                sb.append(' ');
            }
        }
        if(errMessage){
            System.err.println("Invalid Message! Your message contains letters not in the Alphabet");
            System.err.println("Message: " + text);
            System.err.println("         " + sb.toString());
            System.err.println("Machine Alphabet: " + abc);
            return;
        }

        System.out.println("From Console original: " + text);
        Message m = (Message) engine.processText(text).getData();
        System.out.println("processed: " + m.getProcessed());

    }
    private static void resetMachine(){
        if(!engine.isLoaded()){
            System.out.println("Enigma Machine doesn't exist! please load .xml config file");
            return;
        }
        if(!engine.isMachinePresent()) {
            System.out.println("Please setup your machine beforehand, using options 3 or 4 from the menu");
            return;
        }
        System.out.println("Are you sure you want to reset the Enigma Machine? (y/n)");
        char c = reader.nextLine().charAt(0);
        if(Character.toLowerCase(c) == 'y') {
            engine.resetMachine();
           System.out.println("Reset to Enigma was finished successfully");
        }else{
            System.out.println("Reset was declined by the user");
        }

    }

    private static void printMenu(){
        System.out.println("\n\t\t\tENIGMA MENU");
        System.out.println("1. Load machine configuration from XML file");
        System.out.println("2. Print machine specification - rotors, reflector, statistics");
        System.out.println("3. Choose Setup for machine");
        System.out.println("4. Choose Random Setup for machine - good for newbies ;)");
        System.out.println("5. Input text for Encryption / Decryption");
        System.out.println("6. Reset machine to initial setup");
        System.out.println("7. Print History and Statistics");
        System.out.println("8. Create and Load Backups - BONUS");
        System.out.println("9. Exit");
    }


    private static List<Integer> readRotorsList(){
        int rotorCount = settings.getRotorsCount();
        int maximalRotor = settings.getNumOfRotors();
        Set<Integer> rotors = new HashSet<>();
        List<Integer> rotorsList = new ArrayList<>();

        System.out.println("Step 1: Please choose " + rotorCount +" rotors");
        System.out.println("\trotors should be numbers in range (1," + maximalRotor + ")");
        int i = 0;
        while(i < rotorCount){
            try {
                int id = reader.nextInt();
                if(id < 1 || id > maximalRotor){
                    System.out.printf("\nrotor %d is out of range\n", id);
                }
                if(rotors.contains(id)){
                    System.out.printf("rotor %d was already chosen!", id);
                    continue;
                }
                rotors.add(id);
                rotorsList.add(id);
                i++;
            }catch(InputMismatchException e){
                System.out.println("please make sure to enter numbers");
            }
            catch (NoSuchElementException e){
                System.out.printf("you need to choose %d more rotors\n", rotorCount-i);
            }
        }
    return rotorsList;
    }

    private static List<Character> readRotorsPositions() {
        int rotorCount = settings.getRotorsCount();
        Alphabet abc = settings.getAlphabet();
        List<Character> positions = new ArrayList<>();
        System.out.println("Do you want to choose rotors positions? press Enter to continue or 'no' for default setup");
        char choice = reader.next().charAt(0);
        if (choice == 'n' || choice == 'N') {
            return rotorsDefaultSetup();
        }
        System.out.println("Pick a letter in the alphabet for each rotor to set position, you can separate the letters by space or comas your choice :)");
        System.out.printf("Please input %d letters for %d rotors\n", rotorCount, rotorCount);
        int i = 0;
        do {
            for (char c : reader.nextLine().trim().toCharArray()) {
                if (i == rotorCount)
                    break;
                if (abc.isLetter(c)) {
                    positions.add(c);
                    i++;
                } else if (c != ',') {
                    System.out.print("letter " + c + " is not in alphabet!");
                }
            }
        } while (i < rotorCount);
        return positions;
    }


    /**
     * @return list of rotor positions all initialized to the first position meaning 1st letter in the alphabet
     */
    private static List<Character> rotorsDefaultSetup() {
        char firstLetter = settings.getAlphabet().getLetter(0);
        return Collections.nCopies(settings.getRotorsCount(), firstLetter);
    }


    private static void pressEnterToContinue(){
        System.out.println("\n\n\tPress Enter to continue..");
        reader.nextLine();
//        try{
//            //System.in.read();
//            reader.nextLine();
//
//        }catch(Exception e){}
    }
}
