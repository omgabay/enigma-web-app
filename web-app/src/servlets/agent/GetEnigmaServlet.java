package servlets.agent;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UBoat;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

public class GetEnigmaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uboatFromParam = request.getParameter(Constants.UBOAT);
        UserManager users = ServletUtils.getUserManager(getServletContext());
        UBoat uboat = users.getUboat(uboatFromParam);
        PrintWriter out = response.getWriter();
        if(uboat == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("UBoat "+ uboatFromParam + " was not found!");
            return;
        }

        response.setContentType("application/json");
        String json =  Constants.GSON_INSTANCE.toJson(uboat.getMachine());
        out.print(json);
        response.setStatus(HttpServletResponse.SC_OK);

    }


}
