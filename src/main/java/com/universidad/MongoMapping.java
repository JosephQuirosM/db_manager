package com.universidad;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class MongoMapping {
    private MongoDatabase connectDB;

    public MongoMapping(String url, String Mongodb_Name) {
        try
        {
            ConnectionString connectionString = new ConnectionString(url);
            MongoClient mongoClient = MongoClients.create(connectionString);
            connectDB = mongoClient.getDatabase(Mongodb_Name);
        }
        catch(Exception e)
        {
            System.out.println("Error al conectar con la db Mongo " + Mongodb_Name + " url:" + url);
        }
    }
    

    public void insertObjectOnCollection(Object obj) {
        try
        {
            Class<?> auxClass = obj.getClass();
            String className = auxClass.getSimpleName().toLowerCase();
            MongoCollection<Document> collection = connectDB.getCollection(className);

            Document doc = convertToJson(obj, auxClass);

            collection.insertOne(doc);
            System.out.println("Objeto insertado en la coleccion " + className);
        }
        catch (Exception e)
        {
            System.err.println("Error al insertar el objeto en MongoDB: ");
            e.printStackTrace();
        }
    }

    public <T> ArrayList<T> getCollectionFromDB(Class<T> pClass) {
        ArrayList<T> objectList = new ArrayList<>();
        String className = pClass.getSimpleName().toLowerCase();
        MongoCollection<Document> collection = connectDB.getCollection(className);

        try (MongoCursor<Document> cursor = collection.find().iterator())
        {
            while (cursor.hasNext())
            {
                Document doc = cursor.next();
                T instance = createObject(pClass, doc);
                objectList.add(instance);
            }
        } catch (Exception e) {
            System.err.println("Error al recuperar los objetos de la coleccion " + className);
        }
        return objectList;
    }

    public <T> T getObjectFromDB(Class<T> pClass, Object idOfObject) {
        String className = pClass.getSimpleName().toLowerCase();
        MongoCollection<Document> collection = connectDB.getCollection(className);
        Document query = new Document("_id", idOfObject);

        try {
            Document doc = collection.find(query).first();
            if (doc != null)
            {
                return createObject(pClass, doc);
            }
        }
        catch (Exception e)
        {
            System.err.println("Error al recuperar el objeto de la colección: " + className);
            e.printStackTrace();
        }
        return null;
    }

    public <T> void deleteObjectFromCollection(Class<T> pClass, Object obj) {
        String className = pClass.getSimpleName().toLowerCase();

        try
        {
            
            MongoCollection<Document> collection = connectDB.getCollection(className);
            Document query = new Document("_id", obj);
    
            collection.deleteOne(query);
            System.out.println("Objeto eliminado de la colección " + className + " correctamente.");
        } 
        catch (Exception e)
        {
            System.err.println("Error al eliminar el objeto de la coleccion " + className);
            e.printStackTrace();
        }
    }

    public void updateObjectFromCollection(Object obj) {
        Class<?> auxClass = obj.getClass();
        String className = auxClass.getSimpleName().toLowerCase();

        try
        {
            MongoCollection<Document> collection = connectDB.getCollection(className);

            Document doc = convertToJson(obj, auxClass);
            Field idField = auxClass.getDeclaredField("id");
            idField.setAccessible(true);
            Object id = idField.get(obj);

            if (id == null)
            {
                throw new IllegalArgumentException("El campo ID no puede ser nulo");
            }

            collection.replaceOne(Filters.eq("_id", id), doc);
            System.out.println("Documento actualizado en la colección " + className + " correctamente.");
        }
        catch (Exception e)
        {
            System.err.println("Error al actualizar el objeto en la coleccion " + className);
        }
    }

    private Document convertToJson(Object obj, Class<?> pClass)
    {
        try
        {
            Document doc = new Document();
            Field[] fields = pClass.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(obj);

                if (fieldName.equals("id"))
                {
                    doc.append("_id", fieldValue);
                } 
                else 
                {
                    doc.append(fieldName, fieldValue);
                }
            }
            return doc;
        }
        catch (IllegalAccessException e)
        {
            System.out.println("error al convertir en Json");
            e.printStackTrace();
            return null;
        }
        
    }

    private <T> T createObject(Class<T> pClass, Document doc)
    {
        try
        {
            Constructor<T> constructor = pClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();
            Field[] fields = pClass.getDeclaredFields();

            for (Field field : fields)
            {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = doc.get(fieldName);

                if (fieldValue == null && fieldName.equals("id")) {
                    fieldValue = doc.get("_id");
                }

                field.set(instance, fieldValue);
            }

            return instance;
        }
        catch (Exception e)
        {
            System.out.println("error al obtener el objeto y crearlo");
        }
        return null;
    }

}
