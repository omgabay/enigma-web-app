package servlets.testing;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.User;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

public class GetUserServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter(Constants.USERNAME);
        if(username == null || username.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UserManager users = ServletUtils.getUserManager(getServletContext());
        User user = users.getUser(username);
        String json = Constants.GSON_INSTANCE.toJson(user);
        try(PrintWriter out = response.getWriter()){
            out.print(json);
            out.flush();
        }



    }


}
