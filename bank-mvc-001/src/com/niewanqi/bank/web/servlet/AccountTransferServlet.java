package com.niewanqi.bank.web.servlet;

import com.niewanqi.bank.exceptions.MoneyNotEnoughException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * 在不使用mvc架构的前提下，完成银行账户转账
 * 分析这个程序存在哪些问题？
 *
 *  @author niewanqi
 *  @version 1.0
 *  @since 1.0
 */
@WebServlet("/transfer")
public class AccountTransferServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //获取响应流对象
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        //获取转账的相关的信息
        String fromActno = request.getParameter("fromActno");
        String toActno = request.getParameter("toActno");
        double money = Double.parseDouble(request.getParameter("money"));
        //编辑转账的业务逻辑代码，连接数据库，进行转账操作
        //1、转账之前要判断转出的账户余额是否充足
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            //注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //获取连接
            String url = "jdbc:mysql://localhost:3306/mvc";
            String user = "root";
            String password = "123456";
            conn = DriverManager.getConnection(url,user,password);
            //获取预编译的数据库操作对象
            String sql1= "select balance from t_act where actno = ?";
            ps = conn.prepareStatement(sql1);
            ps.setString(1,fromActno);
            rs = ps.executeQuery();
            //处理结果集
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance < money){
                    //余额不足(使用异常处理机制)
                    throw new MoneyNotEnoughException("对不起，余额不足");
                }
                //程序如果能够进行到这里一定证明余额是充足的
                //开始转账

            }
        } catch (Exception e) {
            //发生异常之后你准备怎么做
            //e.printStackTrace();
            out.print(e.getMessage());
        }finally {
            //释放资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }
}
