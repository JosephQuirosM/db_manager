package com.universidad;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OracleMapping
{
    //atributes
    private Connection connectDB;
    private int connectionType;

    //Constructor
    OracleMapping(String url, String user, String password)
    {
        try
        {
            connectDB = DriverManager.getConnection(url, user, password);
        }
        catch (SQLException e)
        {
            System.out.println("Error al conectar con la base de datos oracle " + url);
            System.out.println("Usuario: " + user);
            System.out.println("password: " + password);
            e.printStackTrace();
            return;
        }

        if(url.contains("oracle"))
        {
            connectionType = 1;
            return;
        }

        if(url.contains("mysql"))
        {
            connectionType = 2;
            return;
        }
    }

    //Public methods
    public void insertObjectInTable(Object obj)
    {
        Class<?> auxClass = obj.getClass();
        String className = auxClass.getSimpleName().toUpperCase();
        findTable(className, auxClass);
        insertAttributes(auxClass, className, obj);
    }
    
    public void printTable(Class<?> pClass)
    {
        String className = pClass.getSimpleName().toUpperCase();

        if(isContainedTheTableOnDB(className)){
            printTable(className);
            return;
       } 
       System.out.println("No existe la tabla " + className);
    }

    public <T> List<T> selectAll(Class<T> pClass) {
        List<T> result = new ArrayList<>();
        String tableName = pClass.getSimpleName().toUpperCase();
        try {
            String query = "SELECT * FROM " + tableName;
            try (PreparedStatement statement = connectDB.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        T instance = createInstance(pClass, resultSet);
                        result.add(instance);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al recuperar objetos de la tabla: " + e.getMessage());
        }
        return result;
    }

    public <T> T selectObjectWithID(Class<T> pClass, Object id) {
        String tableName = pClass.getSimpleName().toUpperCase();
    
        String query = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement statement = connectDB.prepareStatement(query)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createInstance(pClass, resultSet);
                } else {
                    System.out.println("No se encontró ningún registro con ID " + id + " en la tabla " + tableName);
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al seleccionar el registro con ID " + id + ": " + e.getMessage());
            return null;
        }
    }

    public void deleteObjectFromTable(Object obj)
    {

        if(isContainedTheObjectOnDB(obj)){
            sendDeleteQuery(obj);
            return;
       }

       System.out.println("El Objeto no existe en la db");
    }

    public void updateObjectFromTable(Object obj)
    {
        if(isContainedTheObjectOnDB(obj)){
            sendUpdateQuery(obj);
            return;
       }

       System.out.println("El Objeto no existe en la db");
    }

    //Private methods
    private void findTable(String className, Class<?> class1)
    {
        if(!isContainedTheTableOnDB(className))
        {
            createTable(class1);
        }

    }

    private boolean isContainedTheTableOnDB(String className)
    {
        String query = "";

        if (connectionType == 1) //oracle
        {
            query = "SELECT count(*) FROM user_tables WHERE table_name = ?";
        }
        
        if (connectionType == 2) //MySQL
        {
            query =  "SELECT count(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";
        }

        try(PreparedStatement statement = connectDB.prepareStatement(query))
        {
            statement.setString(1, className);
            try(ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        catch (SQLException e)
        {
          System.out.println("Ocurrio un error en la conexion o al buscar la tabla en la db");
          e.printStackTrace();  
        }

        System.out.println("no existe la tabla " + className + "en la db");
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

    private boolean isContainedTheObjectOnDB(Object obj){
        Class<?> auxClass = obj.getClass();
        String className = auxClass.getSimpleName().toUpperCase();

        if(isContainedTheTableOnDB(className)){
            Field[] attributes = auxClass.getDeclaredFields();
            StringBuilder query = new StringBuilder("SELECT * FROM ").append(className).append(" WHERE ");
            StringBuilder values = new StringBuilder();
            try
            {
              for(Field attribute : attributes)
              {
                attribute.setAccessible(true);
                String attributeType = attribute.getName();

                if(attributeType.equals("id"))
                {
                  Object attributeValue = attribute.get(obj);
                  values = new StringBuilder(attributeType).append(" = ").append(attributeValue);  
                }
                
              }

              query.append(values);
            }
            catch(IllegalAccessException e)
            {
                System.out.println("Error al obtener los valores del objeto " + values);
                return false;
            }
            
            try
            {
                PreparedStatement statement = connectDB.prepareStatement(query.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next())
                {
                    int count = resultSet.getRow();
                    return count > 0;
                }
            }
            catch(SQLException e)
            {
                e.printStackTrace();
                System.out.println("Error al buscar el objeto "+ values +" en la db");
                return false;
            }
        }

        return false;
    }
    
    private void sendDeleteQuery(Object obj)
    {
        Class<?> auxClass = obj.getClass();
        String className = auxClass.getSimpleName().toUpperCase();

        Field[] attributes = auxClass.getDeclaredFields();
        StringBuilder query = new StringBuilder("Delete FROM ").append(className).append(" WHERE ");
        StringBuilder values = new StringBuilder();
        try
        {
            for(Field attribute : attributes)
            {
            attribute.setAccessible(true);
            String attributeType = attribute.getName();

            if(attributeType.equals("id"))
            {
                Object attributeValue = attribute.get(obj);
                values = new StringBuilder(attributeType).append(" = ").append(attributeValue);  
            }
                
            }

            query.append(values);
        }
        catch(IllegalAccessException e)
        {
            System.out.println("No se pudo eliminar el objeto debido a que no se pudo acceder a los datos" + values);
                return;
        }
            
        try
        {
            PreparedStatement statement = connectDB.prepareStatement(query.toString());
            statement.executeUpdate();
            System.out.println("Se elimino correctamente el objeto " + values + " de la tabla " + className);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.out.println("No se pudo ejecutar la siguiente instruccion: " + query);
            return;
        }
    }

    private void sendUpdateQuery(Object obj)
    {
        Class<?> auxClass = obj.getClass();
        String className = auxClass.getSimpleName().toUpperCase();

        Field[] attributes = auxClass.getDeclaredFields();
        StringBuilder query = new StringBuilder("Update ").append(className).append(" SET ");
        StringBuilder where_id = new StringBuilder(" WHERE ");
        StringBuilder values = new StringBuilder();
        try
        {
            for(Field attribute : attributes)
            {
            attribute.setAccessible(true);
            String attributeType = attribute.getName();
            Object attributeValue = attribute.get(obj);
            Class<?> typeClass = attribute.getType();

            if(attributeType.equals("id"))
            {
                where_id.append(attributeType).append(" = ").append(attributeValue);
            }
            else
            {
                
                if(typeClass == String.class || typeClass == char.class)
                {
                    values.append(attributeType).append(" = ").append("'").append(attributeValue).append("', ");
                }
                else
                {
                    values.append(attributeType).append(" = ").append(attributeValue).append(", ");
                }
            }
            }
            values.delete(values.length() - 2, values.length());
            query.append(values).append(where_id);
        }
        catch(IllegalAccessException e)
        {
            System.out.println("No se pudo eliminar el objeto debido a que no se pudo acceder a los datos" + values);
                return;
        }
        
        try
        {
            PreparedStatement statement = connectDB.prepareStatement(query.toString());
            statement.executeUpdate();
            System.out.println("Se actualizo correctamente el objeto " + values + " de la tabla " + className);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.out.println("No se pudo ejecutar la siguiente instruccion: " + query);
            return;
        }
    }

    private void printTable(String tableName)
    {
        String query = "SELECT * FROM " + tableName;

        try(PreparedStatement statement = connectDB.prepareStatement(query))
        {
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columns = metaData.getColumnCount();
            String columnName;
            String columnValue;
            String object = "";

            System.out.println("------- " + tableName + " -------");

            while(resultSet.next())
            {
                for(int column = 1; column <= columns; column++ )
                {
                    columnName = metaData.getColumnName(column);
                    columnValue = resultSet.getString(column);
                    object += columnName + ": " + columnValue + " | ";
                }
                System.out.println(object);
                object = "";
            }
            System.out.println(" ");
        }
        catch(SQLException e)
        {
            System.out.println("Error al obtener la tabla " + tableName);
            e.printStackTrace();
            return;
        }

        
    }
    
    private <T> T createInstance(Class<T> pClass, ResultSet resultSet) throws SQLException {
        try {
            Constructor<T> constructor = pClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();

            Field[] fields =pClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = resultSet.getObject(fieldName);

                if (fieldValue != null) {
                    if (field.getType() == int.class || field.getType() == Integer.class) {
                        fieldValue = ((Number) fieldValue).intValue();
                    } else if (field.getType() == double.class || field.getType() == Double.class) {
                        fieldValue = ((Number) fieldValue).doubleValue();
                    } else if (field.getType() == float.class || field.getType() == Float.class) {
                        fieldValue = ((Number) fieldValue).floatValue();
                    } else if (field.getType() == long.class || field.getType() == Long.class) {
                        fieldValue = ((Number) fieldValue).longValue();
                    } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        fieldValue = (fieldValue instanceof Number) ? ((Number) fieldValue).intValue() != 0 : Boolean.parseBoolean(fieldValue.toString());
                    }

                    field.set(instance, fieldValue);
                }
            }

            return instance;
        } catch (NoSuchMethodException e) {
            throw new SQLException("No se pudo encontrar el constructor predeterminado para la clase " + pClass.getSimpleName() + ": " + e.getMessage(), e);
        } catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            throw new SQLException("Error al instanciar la clase " + pClass.getSimpleName() + ": " + e.getMessage(), e);
        }
    }
}