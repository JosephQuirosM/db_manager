package com.universidad;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Mapping
{
    //atributes
    Connection connectDB;

    //Constructor
    Mapping(String url, String user, String password)
    {
        try
        {
            connectDB = DriverManager.getConnection(url, user, password);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
    }

    //Public methods
    public void convertObjectToTable(Object obj)
    {
        System.out.println("const: "+ connectDB);
        Class<?> auxClass = obj.getClass();
        String className = auxClass.getSimpleName().toUpperCase();
        findTable(className, auxClass);
        insertAttributes(auxClass, className, obj);
    }

    //Private methods
    private void findTable(String className, Class<?> class1)
    {
        if(!isContainedTheTable(className))
        {
            System.out.println("La Tabla " + className + " no se encontro en la db");
            createTable(class1);
            return;
        }

        System.out.println("si existe la tabla " + className + " en la db");
    }

    private boolean isContainedTheTable(String className)
    {
        String query = "SELECT count(*) FROM user_tables WHERE table_name = ?";
        try(PreparedStatement statement = connectDB.prepareStatement(query))
        {
            statement.setString(1, className);
            try(ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    int count = resultSet.getInt(1);
                    System.out.println("tablas con el nombre del objeto: " + count);
                    return count > 0;
                }
            }
        }
        catch (SQLException e)
        {
          System.out.println("Ocurrio un error en la conexion o al buscar la tabla en la db");
          e.printStackTrace();  
        }

        return false;
    }

    private void createTable(Class<?> class1)
    {
        String className = class1.getSimpleName().toUpperCase();
        StringBuilder query = new StringBuilder("CREATE TABLE ");
        query.append(className);
        query.append(" (");

        Field[] attributes = class1.getDeclaredFields();
        Boolean havePrimaryKey = false;
        String columnName;
        String columnType;

        for(Field attribute : attributes)
        {
            attribute.setAccessible(true);
            columnName = attribute.getName();
            columnType = getType(attribute.getType());
            query.append(columnName.toUpperCase()).append(" ").append(columnType);
            if(columnName.equals("id"))
            {
                query.append(" PRIMARY KEY");
                havePrimaryKey = true;
            }
            query.append(", ");
        }

        query.delete(query.length() - 2, query.length());
        query.append(")");

        if (!havePrimaryKey)
        {
            System.out.println("La tabla " + className + " no tiene un campo clave primaria.");;
        }

        try(PreparedStatement statement = connectDB.prepareStatement(query.toString()))
        {
            statement.executeUpdate();
            System.out.println("La Tabla " + className + " se creo con exito");
        }
        catch (SQLException e)
        {
            System.out.println("No se creo la tabla " + className);
            e.printStackTrace();
        }
    }

    private String getType(Class<?> type)
    {
        if(type == String.class)
            return "VARCHAR(255)";

        if(type == int.class || type == Integer.class)
            return "INT";
        
        if (type == double.class || type == Double.class)
            return "DOUBLE";

        if (type == float.class || type == Float.class)
            return "FLOAT";
        
        if (type == boolean.class || type == Boolean.class)
            return "BOOLEAN";
        
        return "VARCHAR(255)";
    }

    private void insertAttributes(Class<?> class1, String className, Object obj)
    {
        StringBuilder query = new StringBuilder("INSERT INTO ").append(className).append(" (");
        StringBuilder values = new StringBuilder("VALUES (");

        Field[] attributes = class1.getDeclaredFields();
        try
        {
            for (Field attribute : attributes)
            {
                attribute.setAccessible(true);
                String attributeType = attribute.getName();
                Object attributeValue = attribute.get(obj);
                query.append(attributeType).append(", ");
                values.append("'").append(attributeValue).append("', ");
            }
        }
        catch (IllegalAccessException e)
        {
            System.out.println("Error al intentar conseguir los atributos del objeto para insertarlos en la tabla " + className);
            return;
        }

        query.delete(query.length() - 2, query.length());
        values.delete(values.length() - 2, values.length());
        query.append(") ");
        values.append(")");
        String completeQuery = query.toString() + values.toString();
        System.out.println(completeQuery);

        try (PreparedStatement statement = connectDB.prepareStatement(completeQuery))
        {
            statement.executeUpdate();
            System.out.println("Datos insertados en la tabla " + className + " correctamente.");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println("Error al insertar el objeto a la tabla "+ className);
        }
    }
}
