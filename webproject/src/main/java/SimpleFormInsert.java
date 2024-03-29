import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SimpleFormInsert")
public class SimpleFormInsert extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public SimpleFormInsert() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");

        Connection connection = null;
        String insertSql = " INSERT INTO myTable (id, FIRSTNAME, LASTNAME, ADDRESS, PHONE, EMAIL) values (default, ?, ?, ?, ?, ?)";

        try {
            DBConnection.getDBConnection();
            connection = DBConnection.connection;
            PreparedStatement preparedStmt = connection.prepareStatement(insertSql);
            preparedStmt.setString(1, firstname);
            preparedStmt.setString(2, lastname);
            preparedStmt.setString(3, address);
            preparedStmt.setString(4, phone);
            preparedStmt.setString(5, email);
            preparedStmt.execute();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set response content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String title = "Your contact has been added!";
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("<title>" + title + "</title>");
        out.println("<style>");
        out.println("body {font-family: Arial, sans-serif;}");
        out.println("h2 {text-align: center;}");
        out.println("ul {list-style-type: none; padding: 0;}");
        out.println("li {margin-bottom: 10px;}");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h2>" + title + "</h2>");
        out.println("<ul>");
        out.println("  <li><b>First Name</b>: " + firstname + "</li>");
        out.println("  <li><b>Last Name</b>: " + lastname + "</li>");
        out.println("  <li><b>Address</b>: " + address + "</li>");
        out.println("  <li><b>Phone</b>: " + phone + "</li>");
        out.println("  <li><b>Email</b>: " + email + "</li>");
        out.println("</ul>");

        out.println("<a href=\"/webproject/simpleFormSearch.html\">Go Back</a> <br>");
        out.println("</body>");
        out.println("</html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
