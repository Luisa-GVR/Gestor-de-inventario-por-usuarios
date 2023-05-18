package com.ISI.Desarrollo.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Methods {

    final MyService myService;

    public Methods(MyService myService) {
        this.myService = myService;
    }

    public String showDatabase() throws SQLException {

        StringBuilder table = new StringBuilder();
        table.append("+------------+------------------+------------+---------------------+------------+\n");
        table.append("|   Código   |      Producto    |   Cantidad |      Descripción    |    Precio  |\n");
        table.append("+------------+------------------+------------+---------------------+------------+\n");

        try (Connection conn = myService.getConnection()) {
            String sql = "SELECT * FROM inventario";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int column1 = rs.getInt("Codigo");
                String column2 = rs.getString("Producto");
                int column3 = rs.getInt("Cantidad");
                String column4 = rs.getString("Descripcion");
                int column5 = rs.getInt("Precio");

                table.append(String.format("| %-10s | %16s | %-10s | %19s | %-10s |\n", column1, column2, column3, column4, column5));
            }
        }

        table.append("+------------+------------------+------------+---------------------+------------+\n");

        return table.toString();
    }

    public List<String> productoNames() throws SQLException {
        List<String> nombres = new ArrayList<>();

        try(Connection conn = myService.getConnection()){
            String sql = "SELECT * FROM inventario";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                nombres.add(rs.getString("Producto"));
            }

        }
        return nombres;
    }

    public void addAttribute(long codigo, String producto, long cantidad, String descripcion, long precio) {

        int codigoFinal, cantidadFinal, precioFinal;
        String productoFinal = producto;
        String descripcionFinal = descripcion;

        //codigo length
        String subCodigo = String.valueOf(codigo);
        if (subCodigo.length()>8){
            codigoFinal = Integer.parseInt(subCodigo.substring(0,7));
        } else {
            codigoFinal = Integer.parseInt(subCodigo);
        }
        //producto length
        if(producto.length()>15){
            productoFinal = producto.substring(0,14);
        }
        //cantidad length
        String subCantidad= String.valueOf(cantidad);
        if (subCantidad.length()>8){
            cantidadFinal = Integer.parseInt(subCantidad.substring(0,7));
        } else {
            cantidadFinal = Integer.parseInt(subCantidad);
        }
        //descripcion length
        if(descripcion.length()>20){
            descripcionFinal = descripcion.substring(0,19);
        }
        //precio length
        String subPrecio= String.valueOf(precio);
        if (subPrecio.length()>8){
            precioFinal = Integer.parseInt(subPrecio.substring(0,7));
        } else {
            precioFinal = Integer.parseInt(subPrecio);
        }

        try (Connection conn = myService.getConnection()) {
            String sql = "INSERT INTO inventario (Codigo, Producto, Cantidad, Descripcion, Precio) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = null;
            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, codigoFinal);
            preparedStatement.setString(2, productoFinal);
            preparedStatement.setInt(3, cantidadFinal);
            preparedStatement.setString(4, descripcionFinal);
            preparedStatement.setInt(5, precioFinal);

            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAttribute(int codigo){

        try (Connection conn = myService.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM inventario WHERE codigo = ?");

            pstmt.setInt(1, codigo);
            pstmt.executeUpdate();

    } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int[] valores(String producto){
        int[] valores = new int[2];
        try (Connection conn = myService.getConnection()) {
            String sql = "SELECT * FROM inventario WHERE producto = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, producto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                valores[0] = rs.getInt("Cantidad");
                valores[1] = rs.getInt("Precio");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return valores;
    }

    public void editAttribute(int cantidad, int precio, String producto){
        try (Connection conn = myService.getConnection()) {
        PreparedStatement preparedStatement = null;

            String sql = "UPDATE inventario SET Cantidad = ?, Precio = ? WHERE Producto = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, cantidad);
            preparedStatement.setInt(2, precio);
            preparedStatement.setString(3, producto);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
