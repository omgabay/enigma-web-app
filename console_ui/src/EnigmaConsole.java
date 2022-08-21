import auxiliary.*;
import auxiliary.MachineInfo;
import exceptions.AbortSetupException;
import exceptions.EnigmaException;

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

        System.out.println("\nWelcome to the Enigma Console by Omer Gabay :)\n\n");
        System.out.println("\t\tGUIDELINES");
        System.out.println("To start Enigma choose option 1 to load machine configuration file");
        System.out.println("After enigma was loaded go to Setup - options 3 or 4");
        System.out.println("You can also select option 8, to load previously created enigma from Backup file");

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
                pressEnterToContinue();

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
                    File f = new File(path);
                    FileInputStream fileIn = new FileInputStream(path);
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    engine = (IEngine) in.readObject();
                    settings = (MachineSettings) engine.getEnigmaSettings().getData();
                    System.out.println("Enigma Backup File " + f.getName() + " was loaded successfully");
                    in.close();
                    fileIn.close();

                }catch(FileNotFoundException e){
                    System.out.println("Enigma Backup File was not found, check your path");
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
        System.out.println("Enigma Initial Setup " + machine);
        System.out.println("Enigma current configuration - " + machine.getCurrentConfiguration());

    }

    public static void loadXML(){
        System.out.println("To start encrypting messages please give the full path to Enigma configuration file");
        System.out.println("The file should be in .xml format");
        String fileName = reader.nextLine().trim();

        try{
            File xml = new File(fileName);
            if(!xml.exists()){
                throw new FileNotFoundException(fileName);
            }
            settings = (MachineSettings) engine.loadMachineFromXml(xml).getData();
            System.out.printf("Machine loaded successfully from %s", xml.getName());
            // get file name without extension
            int pos = xml.getName().lastIndexOf('.');
            configFileName = xml.getName().substring(0,pos);
        }
        catch(FileNotFoundException e){
            System.out.println("Loading operation failed");
            System.out.println("File '" + e.getMessage() + "' was not found, please check your path!");
        }catch (JAXBException e) {
            System.out.println("Loading operation failed");
            System.out.println("XML parsing error. Please check that your xml is in the right format and close the file if it's open");
        }
        catch (EnigmaException e){
            System.out.println("Loading operation failed");
            System.out.println(e.getMessage());

        }
    }

    private static void readInputForSetup(){
        if(!engine.isLoaded()){
            System.out.println("Please load machine configuration file before setup phase");
            return;
        }
        try {
            List<Integer> rotorIDs = readRotorsList();
            List<Character> positions = readRotorsPositions();
            RomanNumeral reflectorID = readReflectorChoice();
            HashMap<Character, Character> pb = readPlugboard2();
            System.out.println("Printing your choices");
            System.out.println("Rotors - " + rotorIDs);
            System.out.println("Rotors Positions - " + positions);
            System.out.println("Reflector used " + reflectorID);
            System.out.print("Plugboard chosen " + pb);
            Collections.reverse(rotorIDs);
            Collections.reverse(positions);
            engine.setupMachine(rotorIDs, positions, reflectorID, pb);
        }catch(AbortSetupException ase){
            System.err.println("Enigma Setup was terminated by the user");
        }
        catch(RuntimeException re){
            System.out.print("Error in Setup - " + re.getMessage());
        }
    }

    private static HashMap<Character, Character> readPlugboard2(){
        HashMap<Character, Character> pb;
        Alphabet abc = settings.getAlphabet();
        System.out.println("\nStep 4 - Do you want to add plugs to plugboard?");

        boolean waitingForInput = true;
        boolean error = false;
        do{
            pb = new HashMap<>();
            System.out.println("To add plugs input pairs of letters without spacing");
            System.out.println("You can press Enter to skip this step.");
            String input = reader.nextLine().trim().toUpperCase();
            if(input.isEmpty()){
                return pb;
            }
            if(input.length()%2 != 0){
                char c = input.charAt(input.length()-1);
                System.out.printf("Error - letter %c is missing another letter to plug with\ncheck that your input has even number of letters\n",c);
                error = true;
            }
            for(int i=0; i<input.length()-1 && !error; i+=2){
                char c1 = input.charAt(i);
                char c2 = input.charAt(i+1);
                if(!abc.isLetter(c1) || !abc.isLetter(c2)){
                    System.out.println("Error - One of your letters is not in the alphabet (" + c1+c2+ ")");
                    error = true;
                    break;
                }
                if(pb.containsKey(c1)){
                    System.out.println("Invalid input - " + c1 + " was already paired!");
                    error = true;
                    break;
                }
                if(pb.containsKey(c2)){
                    System.out.println("Invalid input - " + c2 + " was already paired!");
                    error = true;
                    break;
                }
                pb.put(c1,c2);
                pb.put(c2,c1);
            }
            if(error){
                System.out.println("Do you want to try again? Pressing 'n' will exit the setup phase, any other key to try again");
                char c = reader.nextLine().charAt(0);
                if (Character.toLowerCase(c) == 'n') {
                    throw new AbortSetupException();
                }
                error = false;
            }else{
                waitingForInput = false;
            }

        }while(waitingForInput);


        return pb;

    }
    @Deprecated
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
        System.out.println("\nStep 3 - choosing reflector");
        RomanNumeral max = settings.getMaximalReflector();
        if (max == RomanNumeral.I) {
            System.out.println("Your Enigma uses Reflector " + RomanNumeral.I + " the only reflector available");
            return RomanNumeral.I;
        }
        RomanNumeral input = RomanNumeral.Undefined;
        do {
            System.out.println("Please choose which reflector to work with, input a number from the options menu");
            for (int i = 1; i <= max.getValue(); i++) {
                System.out.println(i + ". " + RomanNumeral.getRomanFromInt(i));
            }

            String tmp = reader.nextLine().trim();
            char choice = Character.toLowerCase(tmp.charAt(0));
            if (choice-'0' > max.getValue() || !Character.isDigit(choice)) {
                choice = 0;
            }

            switch (choice) {
                case '1':
                    input = RomanNumeral.I;
                    break;
                case '2':
                    input = RomanNumeral.II;
                    break;
                case '3':
                    input =  RomanNumeral.III;
                    break;
                case '4':
                    input = RomanNumeral.IV;
                    break;
                case '5':
                    input =  RomanNumeral.V;
                    break;
                default:
                    System.out.println("No reflector matching your choice.");
                    System.out.println("Do you want to try again? Pressing 'n' will exit the setup phase, any other key to try again");
                    char c = reader.nextLine().charAt(0);
                    if (Character.toLowerCase(c) == 'n') {
                        throw new AbortSetupException();
                    }
            }
        }while(input == RomanNumeral.Undefined);
        return input;
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
        String text = reader.nextLine().trim().toUpperCase();
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
            System.err.println("         " + sb);
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
           System.out.println("Reset to Enigma finished successfully");
        }else{
            System.out.println("Reset was declined by the user");
        }

    }

    private static void printMenu(){
        System.out.println("\n\t\tENIGMA MENU");
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

        System.out.println("Step 1 - Please choose " + rotorCount +" rotors");
        System.out.println("rotors should be numbers in range 1-" + maximalRotor + "  (inclusive)");
        System.out.println("Separate the numbers with whitespace");
        int count = 0;
        String line = reader.nextLine();
        Scanner s = new Scanner(line);
        while(count < rotorCount){
            String input;
            try {
                if(!s.hasNext()){
                    System.out.printf("you need to choose %d more rotors\n", rotorCount-count);
                    System.out.println("if you don't want to continue with setup press q");
                    line = reader.nextLine();
                    if(line.startsWith("q") || line.startsWith("Q")){
                        throw new AbortSetupException();
                    }
                    s = new Scanner(line);
                }
                input = s.next();
            } catch (NoSuchElementException e){
                System.out.printf("you need to choose %d more rotors\n", rotorCount-count);
                continue;
            }
            for (String num : input.split(",")) {
                if(count == rotorCount){
                    break;
                }
                try {
                    int id = Integer.parseInt(num);
                    if (id < 1 || id > maximalRotor) {
                        System.out.printf("\nrotor %d is out of range\n", id);
                        continue;
                    }
                    if (rotors.contains(id)) {
                        System.out.printf("rotor %d was already chosen!\n", id);
                        continue;
                    }
                    rotors.add(id);
                    rotorsList.add(id);
                    count++;
                }catch(NumberFormatException ne){
                    System.out.println("Wrong input - rotor id should be a number\n");
                }
            }
        }
    System.out.println("Rotor IDs: " + rotorsList);

    return rotorsList;
    }

    private static List<Character> readRotorsPositions() {
        // Loading machine relevant settings
        int rotorCount = settings.getRotorsCount();
        Alphabet abc = settings.getAlphabet();
        List<Character> positions = new ArrayList<>();
        int mistakes = 0;

        System.out.println("\nStep 2 - Choosing Rotors Positions\nPick a letter in the alphabet for each rotor");
        System.out.println("You can input the letters with or without spacing");
        System.out.printf("Please input %d letters for %d rotors\n", rotorCount, rotorCount);
        int i = 0;
        do {
            for (char c : reader.nextLine().trim().toCharArray()) {
                c = Character.toUpperCase(c);
                if (i == rotorCount)
                    break;
                if (abc.isLetter(c)) {
                    positions.add(c);
                    i++;
                } else if (c != ',' && !Character.isWhitespace(c)) {
                    System.out.println("letter " + c + " is not in alphabet!");

                    if(++mistakes >= 3){
                        System.out.println("if you don't want to continue with setup press q");
                        String input = reader.nextLine().trim();
                        if(input.startsWith("q") || input.startsWith("Q")) {
                            throw new AbortSetupException();
                        }else{
                            mistakes = 0;
                        }
                    }
                }
            }
        } while (i < rotorCount);
        System.out.println("Rotors Positions: " + positions);
        return positions;
    }



    private static void pressEnterToContinue(){
        System.out.println("\n\n\tPress Enter to continue..");
        reader.nextLine();
    }
}
