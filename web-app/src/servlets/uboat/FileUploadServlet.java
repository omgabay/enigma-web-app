package servlets.uboat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jaxb.generated.CTEEnigma;
import machine.Engine;
import utils.Constants;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.Scanner;


@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileUploadServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        Part part = request.getParts().iterator().next();

        InputStream is = new DataInputStream(part.getInputStream());
        Engine engine = new Engine();
        try {
            CTEEnigma cteEnigma  = engine.loadMachineFromInputStream(is);
            String json = Constants.GSON_INSTANCE.toJson(cteEnigma);
            out.print(json);
            response.setStatus(HttpServletResponse.SC_OK);


        } catch (JAXBException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getOutputStream().println("JAXB FILE ERROR");
        }






    }


    private void printPart(Part part, PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("Parameter Name: ").append(part.getName()).append("\n")
                .append("Content Type (of the file): ").append(part.getContentType()).append("\n")
                .append("Size (of the file): ").append(part.getSize()).append("\n")
                .append("Part Headers:").append("\n");

        for (String header : part.getHeaderNames()) {
            sb.append(header).append(" : ").append(part.getHeader(header)).append("\n");
        }

        out.println(sb.toString());
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    private void printFileContent(String content, PrintWriter out) {
        out.println("File content:");
        out.println(content);
    }


}





