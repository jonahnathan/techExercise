import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SimpleFormSearch")
public class SimpleFormSearch extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public SimpleFormSearch() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String action = request.getParameter("action");
        
        if (action != null && action.equals("delete")) {
            String firstNameToDelete = request.getParameter("firstNameToDelete");
            deleteRecord(firstNameToDelete);
            // Return response as JSON indicating success
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": true}");
            out.flush();
            return;
        }

        search(keyword, response);
    }

    void search(String keyword, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String title = "Contact List";

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + title + "</title>");
        out.println("<style>");
        out.println("body {font-family: Arial, sans-serif;}");
        out.println("h1 {text-align: center;}");
        out.println("table {width: 100%; border-collapse: collapse;}");
        out.println("th, td {padding: 8px; text-align: left; border-bottom: 1px solid #ddd;}");
        out.println(".delete-button {background-color: #f44336; color: white; border: none; cursor: pointer; padding: 6px 12px; border-radius: 4px;}");
        out.println("</style>");
        out.println("<script src='https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js'></script>");
        out.println("<script>");
        out.println("function deleteRecord(firstName) {");
        out.println("  if (confirm('Are you sure you want to delete this record?')) {");
        out.println("    $.ajax({");
        out.println("      type: 'POST',");
        out.println("      url: '/webproject/SimpleFormSearch',");
        out.println("      data: {action: 'delete', firstNameToDelete: firstName},");
        out.println("      success: function(data) {");
        out.println("        if(data.success) {");
        out.println("          alert('Record deleted successfully');");
        out.println("          location.reload(); // Reload the page after successful deletion");
        out.println("        } else {");
        out.println("          alert('Failed to delete record');");
        out.println("        }");
        out.println("      },");
        out.println("      error: function() {");
        out.println("        alert('Error while deleting record');");
        out.println("      }");
        out.println("    });");
        out.println("  }");
        out.println("}");
        out.println("</script>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h1>" + title + "</h1>");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            DBConnection.getDBConnection();
            connection = DBConnection.connection;

            String selectSQL = "SELECT * FROM myTable";
            if (!keyword.isEmpty()) {
                selectSQL = "SELECT * FROM myTable WHERE FIRSTNAME LIKE ? ORDER BY FIRSTNAME ASC";
            }

            preparedStatement = connection.prepareStatement(selectSQL);
            if (!keyword.isEmpty()) {
                preparedStatement.setString(1, keyword + "%");
            }

            ResultSet rs = preparedStatement.executeQuery();

            out.println("<table>");
            out.println("<tr><th>First Name</th><th>Last Name</th><th>Address</th><th>Phone Number</th><th>Email</th><th>Action</th></tr>");

            while (rs.next()) {
                String firstName = rs.getString("firstname").trim();
                String lastName = rs.getString("lastname").trim();
                String address = rs.getString("address").trim();
                String phone = rs.getString("phone").trim();
                String email = rs.getString("email").trim();

                if (keyword.isEmpty() || firstName.contains(keyword)) {
                    out.println("<tr>");
                    out.println("<td>" + firstName + "</td>");
                    out.println("<td>" + lastName + "</td>");
                    out.println("<td>" + address + "</td>");
                    out.println("<td>" + phone + "</td>");
                    out.println("<td>" + email + "</td>");
                    out.println("<td><button class='delete-button' onclick='deleteRecord(\"" + firstName + "\")'>Delete</button></td>");
                    out.println("</tr>");
                }
            }
            out.println("</table>");

            out.println("<a href='/webproject/simpleFormSearch.html'>Go Back</a> <br>");
            out.println("</body></html>");
            rs.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    void deleteRecord(String firstName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            DBConnection.getDBConnection();
            connection = DBConnection.connection;

            String deleteSql = "DELETE FROM myTable WHERE FIRSTNAME = ?";
            preparedStatement = connection.prepareStatement(deleteSql);
            preparedStatement.setString(1, firstName);
            preparedStatement.executeUpdate();

            connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
