import auxiliary.Alphabet;
import auxiliary.EngineResponse;
import auxiliary.MachineSettings;
import auxiliary.RomanNumeral;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EnigmaConsole {


    private static IEngine engine;
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
                   printMachineSetup();
                   break;
               case '3':
                   readInputForSetup();
                   break;
               case '4':
                   createRandomSetup();
                   break;
               case '5':
                   String word = reader.next();
                   for(char letter : word.toCharArray())
                       processText(letter);
                   break;
               case '6':
                   break;
               case '7':
                   break;
               case '8': case 'q':
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
            try {
                Thread.sleep(1000);
                pressEnterToContinue();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while(loop);
        System.out.println("Goodbye!");
        System.out.print("Going to sleep zzz");


    }

    private static void printMachineSetup() {
        if(!engine.isLoaded()){
            System.out.println("Please load machine configuration file before setup phase");
            return;
        }
        engine.displayMachine();
    }

    public static void loadXML(){
        System.out.println("In order to start encrypting messages please give me the full path to the machine config file");
        System.out.println("The configuration file should be in .xml format");

        try{
            File xml = new File("C:\\Users\\omerg\\IdeaProjects\\Enigma\\console_ui\\src\\resources\\ex1-sanity-small.xml");
            settings = (MachineSettings) engine.loadMachineFromXml(xml).getData();
            System.out.printf("Machine loaded successfully from %s", xml.getName());
        }catch(FileNotFoundException e){
            System.out.println("Configuration file was not found, make sure you gave the right path!");
        }catch (JAXBException e) {
            e.printStackTrace();
            System.out.println("XML parsing error. Please close xml file if open and try again");
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
        //HashMap<Character,Character> pb = readPlugboard();
        HashMap<Character, Character> pb = new HashMap<>();
        pb.put('C', 'B');
        pb.put('B', 'C');
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
        //System.out.print("Plugboard chosen " + pb.entrySet().stream().filter(entry -> entry.getKey() <= entry.getValue()).toString());

    }

    private static HashMap<Character, Character> readPlugboard() {
        HashMap<Character, Character> pb = new HashMap<>();
        Alphabet abc = settings.getAlphabet();
        System.out.println("Please Input any plugs you want in the machine. Notice that this step is optional");
        System.out.println("Write in a single line pairs of letters from your alphabet without spaces in between, if you want you can add '=' sign between the letters i.e A=B");
        Scanner line = new Scanner(reader.nextLine());

        while(line.hasNext()){
            String pair = reader.next();
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



    private static void processText(char c){
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        System.out.println("original: " + c);
        System.out.println("processed: " + engine.processText(sb.toString()));

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
        System.out.println("8. Exit\n");
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
        System.out.println("\n\tPress Enter to continue..");
        try{
            //System.in.read();
            reader.nextLine();

        }catch(Exception e){}
    }
}
