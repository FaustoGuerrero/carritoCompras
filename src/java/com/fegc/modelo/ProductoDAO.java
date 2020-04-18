package com.fegc.modelo;

import com.fegc.config.Conexion;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author fausto
 */
public class ProductoDAO {

    private static final String SQL_SELECT = "select * from producto";
    private static final String SQL_SELECT_IMG_BY_ID = "select foto from producto where idProducto = ?";
    private static final String SQL_SELECT_BY_ID = "select * from producto where idProducto = ?";

//    private static final String SQL_INSERT = "INSERT INTO cliente(nombre, apellido, email, telefono, saldo) "
//            + " VALUES(?, ?, ?, ?, ?)";
//
//    private static final String SQL_UPDATE = "UPDATE cliente "
//            + " SET nombre=?, apellido=?, email=?, telefono=?, saldo=? WHERE id_cliente=?";
//
//    private static final String SQL_DELETE = "DELETE FROM cliente WHERE id_cliente = ?";
    public List<Producto> listar() {
        List<Producto> listaProductos = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = Conexion.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Producto producto = new Producto();
                producto.setId(resultSet.getInt(1));
                producto.setNombres(resultSet.getString(2));
                producto.setFoto(resultSet.getBinaryStream(3));
                producto.setDescripcion(resultSet.getString(4));
                producto.setPrecio(resultSet.getDouble(5));
                producto.setStock(resultSet.getInt(6));
                listaProductos.add(producto);
                System.out.println(producto);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(resultSet);
            Conexion.close(preparedStatement);
            Conexion.close(connection);
        }
        return listaProductos;
    }

    public Producto listarId(int id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Producto producto = new Producto();
        try {
            connection = Conexion.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_BY_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                producto.setId(resultSet.getInt(1));
                producto.setNombres(resultSet.getString(2));
                producto.setFoto(resultSet.getBinaryStream(3));
                producto.setDescripcion(resultSet.getString(4));
                producto.setPrecio(resultSet.getDouble(5));
                producto.setStock(resultSet.getInt(6));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(resultSet);
            Conexion.close(preparedStatement);
            Conexion.close(connection);
        }
        return producto;
    }

    public void listarImg(int id, HttpServletResponse response) throws IOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            outputStream = response.getOutputStream();
            connection = Conexion.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_IMG_BY_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Entramos");
                inputStream = resultSet.getBinaryStream("Foto");
            }
            bufferedInputStream = new BufferedInputStream(inputStream);
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            int i = 0;           
            while ((i = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(i);
            }
        } catch (SQLException | IOException ex) {
            System.out.println("Problemas con la consulta SQL o el Stream\n");
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(preparedStatement);
            Conexion.close(resultSet);
            Conexion.close(connection);
        }
    }

    public void listarImg2(int id, HttpServletResponse response) throws IOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        OutputStream oImage;
        try {
            connection = Conexion.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_IMG_BY_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                byte barray[] = resultSet.getBytes(1);
                response.setContentType("image/*");
                oImage = response.getOutputStream();
                oImage.write(barray);
                oImage.flush();
                oImage.close();
            }
        } catch (SQLException | IOException ex) {
            System.out.println("Problemas con la consulta SQL o el Stream\n");
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(preparedStatement);
            Conexion.close(resultSet);
            Conexion.close(connection);
        }
    }
}
