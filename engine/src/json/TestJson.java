package json;

import auxiliary.Message;
import com.google.gson.Gson;
import jaxb.generated.CTEEnigma;
import machine.Engine;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;


public class TestJson {

    private static FileWriter writer;
    public static void main(String[] args) throws FileNotFoundException {
        Engine engine = new Engine();
        File file  = new File("C:\\Users\\omerg\\IdeaProjects\\Enigma\\uboat\\src\\resources\\enigma-xml\\ex3-large.xml");
        InputStream targetStream = null;
        Gson gson = new Gson();


        try {
            targetStream = Files.newInputStream(file.toPath());
            CTEEnigma cteEnigma = engine.loadMachineFromInputStream(targetStream);
            String json = gson.toJson(cteEnigma);
            writer = new FileWriter("C:\\Users\\omerg\\IdeaProjects\\Enigma\\engine\\src\\resources\\ex3-large.json");
            writer.write(json);
            engine.loadFromJson(json);
            engine.setupMachineAtRandom();
            Message message = (Message) engine.processText("OMER GABAY").getData();
            engine.resetMachine();
            System.out.println(message.getProcessed());
            Message decrypted = (Message) engine.processText(message.getProcessed()).getData();
            System.out.println(decrypted.getProcessed());
        } catch (IOException | JAXBException e) {
            throw new RuntimeException(e);
        }finally {
            try{
                writer.flush();
                writer.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }


    }


}
