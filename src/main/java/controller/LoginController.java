package controller;

import config.MysqlConfig;
import model.UserModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "loginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Tao object cookie
//        Cookie cookie = new Cookie("username", "nguyenvana");
//        //Yeu cau tao cookie tren may
//        resp.addCookie(cookie);

        Cookie[] cookies = req.getCookies();
        for (Cookie item : cookies) {
            if (item.getName().equals("username")){
                System.out.println("Kiem tra: " + item.getValue());
            }
        }
        req.getRequestDispatcher("login.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Bước 1: Lấy tham số username và password người dùng nhập
        String email= req.getParameter("username");
        String password= req.getParameter("password");
        Connection connection = null;
        //Bước 2: Viết câu query
        //? : tham số sẽ được truyền ở JDBC
        String sql= "SELECT *FROM users u where u.email = ? AND u.password = ?;";

        //Bước 3: Đưa câu query vào Statement để chuẩn bị thực thi
        try {
            connection = MysqlConfig.getConnection();
            PreparedStatement statement = MysqlConfig.getConnection().prepareStatement(sql);
            //Truyền tham số cho dấu chấm hỏi trong câu query
            statement.setString(1,email);
            statement.setString(2,password);

            //Bước 4: Thực thi câu query
            //statement có 2 loại thực thi
            //excuteQuery  : select
            //excuteUpdate : insert, delete, update...
            ResultSet resultSet = statement.executeQuery();
            List<UserModel> list = new ArrayList<>();

            //Bước 5: duyệt dữ liệu trong ResultSet và lưu vào trong UserModel
            while (resultSet.next()){
                //Duyệt qua từng dòng dữ liệu
                UserModel userModel = new UserModel();
                //Lấy giá trị của cột chỉ định
                userModel.setId(resultSet.getInt("id"));
                userModel.setEmail(resultSet.getString("email"));
                userModel.setFullname(resultSet.getString("fullname"));
                userModel.setRole_id(resultSet.getInt("role_id"));

                list.add(userModel);
            }
            boolean isSuccess = list.size()>0;
            PrintWriter writer = resp.getWriter();
            writer.println(isSuccess ? "Login success!" : "Login Fail");
            writer.close();
            System.out.println("Kiem tra" + list.size());

        } catch (SQLException e) {
            System.out.println("Lỗi thực thi login");
        } finally {
            if(connection!=null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Lỗi đóng kết nối login" + e.getMessage());;
                }
            }
        }
    }
}