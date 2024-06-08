# Gestor de bases de datos ORM y ODM para Java

### Descripcion
Este programa permite la comunicacion entre proyectos Java y Bases de Datos relacionales/no relacionales (sqlDev, MySQL y Mongo) haciendo el mapeo de la clase y enviando sus respectivas consultas a la base de datos deseada para realizar las operaciones CRUD basicas.  

### Dependencias del programa
- El proyecto fue construido con el JDK-21
- Implementacion de dependecias para el uso de sqlDev, MySQL y Mongo
```xml 
<!-- Dependencia Oracle -->
        <dependency>
            <groupId>com.oracle.database.jdbc</groupId>
            <artifactId>ojdbc8</artifactId>
            <version>23.2.0.0</version>
        </dependency>
        
        <!-- Dependencia MongoDB -->
        <dependency>
            <groupId>org.mongodb</groupId>
            <artifactId>mongodb-driver-sync</artifactId>
            <version>5.1.0</version>
        </dependency>

        <!-- Dependencia MySQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.28</version>
        </dependency>
```

## Guia de uso del proyecto

### sqlDev y MySQL
El proyecto ocupa la instanciacion de la clase para el mapeo de los objetos. La clase `OracleMapping.java` permite comunicarnos con la base de datos, para ello, llamamos el constructor de la clase, el cual consta de lo siguiente

```java
OracleMapping(String URL, String USER, String PASSWORD);
```

Este permite realizar las conexiones con cualquiera de las dos bases de datos y realizar sus respectivos CRUD's. ES NECESARIO la implementacion de un atributo llamado id en los objetos, ya que posteriormente sera tomado como el id del objeto en las tablas.
Para asegurar el correcto funcionamiento del proyecto, es necesario que las tablas se creen con id, para el uso de los metodos posteriores.

  #### Insertar
  - Para realizar la insercion de un objeto a la base de datos, utilizamos el metodo `insertObjectInTable(Object)` al cual, al pasarle por parametro el objeto, este realiza el mapeo correspondiente y lo registra en la base de datos. En cuanto al funcionamiento, si la tabla no existe, este creara la tabla para posteriormente hacer el registro del objeto en la misma.

  #### Seleccion
  - Para recuperar objetos de la base de datos, disponemos de los metodos como `selectObjectWithID(Class<T>, Object id)`, `selectAll(Class<T>)` y `printTable(Class<?>)` el cual permite la recuperacion unica de un objeto por medio de un ID, la recuperacion de todos los objetos de una Tabla (Si existe la misma) y el metodo de impresion de la tabla en la consola.

  #### Borrar
  - Para la eliminacion del objeto, disponemos del metodo `deleteObjectFromTable(Object)` el cual, al pasar el objeto a eliminar, este lo busca y lo elimina.

  #### Actualizar
  - Para Actualizar, disponemos del metodo `updateObjectFromTable(Object)`, este al pasar el objeto actualizado desde el codigo, busca un objeto en la tabla con el ID igual y lo actualiza.


### Mongo
Para el mapeo de objetos a colecciones, utilizamos la clase `MongoMapping.java`. Para instaciar el id del objeto, ES NECESARIO la implmentacion de un atributo llamado id en el objeto, ya que este atributo luego sera tomado para ser el ID en la coleccion
```java
MongoMapping(String URL, String MONGODB_NAME);
```

#### Insertar
- Para realizar la insercion de objetos a colecciones, utilizamos el metodo `insertObjectOnCollection(Object)`, al pasar por parametro el objeto, este realiza el mapeo del objeto y lo envia por medio de un archivo Json

#### Seleccion
- Para recuperacion de objetos en colecciones, disponemos de `getCollectionFromDB(Class<T>)`, `getObjectFromDB(Class<T>, Object id)` permite obtener los objetos de la coleccion.

#### Borrar
- Para la eliminacion de un objecto en la coleccion, disponemos del metodo `deleteObjectFromCollectionWithID(Class<T>, Object id)`, el cual permite la eliminacion de un objeto en la base de datos con el id.
 
#### Actualizar
- Para actualizar un objeto de la coleccion, podemos utilizar el metodo `updateObjectFromCollection(Object)` el cual actualiza el objeto con el id similar.
