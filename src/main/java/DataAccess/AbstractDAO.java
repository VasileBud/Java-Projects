package DataAccess;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import Connection.ConnectionFactory;

/**
 * Abstract Data Access Object (DAO) class providing generic CRUD operations for entities.
 * Implements basic database operations using reflection to work with any entity type.
 *
 * @param <T> The type of entity this DAO handles
 * @author Bud Vasile-Stefan
 * @version 1.0
 */

public class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    private final Class<T> type;

    /**
     * Constructor that determines the entity type T using reflection.
     */
    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Creates a SELECT SQL query for the given field.
     *
     * @param field The field name to query by
     * @return The complete SELECT query string
     */
    private String createSelectQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(" * ");
        sb.append(" FROM ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE " + field + " =?");
        return sb.toString();
    }

    /**
     * Creates an INSERT SQL query for the given fields.
     *
     * @param field Comma-separated list of field names to insert
     * @return The complete INSERT query string
     */
    private String createInsertQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(type.getSimpleName());
        sb.append(" (");
        sb.append(field);
        sb.append(") VALUES (");

        int paramCount = field.split(",").length;
        for (int i = 0; i < paramCount; i++) {
            sb.append("?");
            if (i < paramCount - 1) {
                sb.append(", ");
            }
        }

        sb.append(");");
        return sb.toString();
    }

    /**
     * Creates an UPDATE SQL query for the given fields.
     *
     * @param field The fields and values to update
     * @return The complete UPDATE query string
     */
    private String createUpdateQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(type.getSimpleName());
        sb.append(" SET ");
        sb.append(field);
        sb.append(" WHERE id = ?");
        return sb.toString();
    }

    /**
     * Creates a DELETE SQL query for the given field.
     *
     * @param field The field to filter the deletion by
     * @return The complete DELETE query string
     */
    private String createDeleteQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE ");
        sb.append(field);
        sb.append(" = ?");
        return sb.toString();
    }

    /**
     * Retrieves all records of type T from the database.
     *
     * @return List of all records, or null if error occurs
     */
    public List<T> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("1");
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, 1);
            resultSet = statement.executeQuery();

            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    /**
     * Finds a record by its ID.
     *
     * @param id The ID to search for
     * @return The found record, or null if not found
     */
    public T findById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            return createObjects(resultSet).get(0);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    /**
     * Creates objects of type T from a ResultSet using reflection.
     *
     * @param resultSet The ResultSet containing the data
     * @return List of created objects
     */
    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<T>();
        Constructor[] ctors = type.getDeclaredConstructors();
        Constructor ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 0)
                break;
        }
        try {
            while (resultSet.next()) {
                ctor.setAccessible(true);
                T instance = (T) ctor.newInstance();
                for (Field field : type.getDeclaredFields()) {
                    String fieldName = field.getName();
                    Object value = resultSet.getObject(fieldName);
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Inserts a new record into the database.
     *
     * @param t The object to insert
     * @return The inserted object with generated ID
     */
    public T insert(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        StringBuilder fieldBuilder = new StringBuilder();
        List<Object> values = new ArrayList<>();

        try {
            Arrays.stream(type.getDeclaredFields())
                    .filter(field -> !field.getName().equalsIgnoreCase("id"))
                    .forEach(field -> {
                        field.setAccessible(true);
                        try {
                            if (fieldBuilder.length() > 0) {
                                fieldBuilder.append(", ");
                            }
                            fieldBuilder.append(field.getName());
                            values.add(field.get(t));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });

            String fieldString = fieldBuilder.toString();
            String query = createInsertQuery(fieldString);

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query, statement.RETURN_GENERATED_KEYS);

            PreparedStatement finalStatement = statement;
            IntStream.range(0, values.size())
                    .forEach(i -> {
                        try {
                            finalStatement.setObject(i + 1, values.get(i));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

            statement.executeUpdate();

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);

                Field idField = Arrays.stream(type.getDeclaredFields())
                        .filter(f -> f.getName().equalsIgnoreCase("id"))
                        .findFirst()
                        .orElseThrow();

                idField.setAccessible(true);
                idField.set(t, generatedId);
            }

        } catch (SQLException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(generatedKeys);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return t;
    }

    /**
     * Updates an existing record in the database.
     *
     * @param t The object to update
     * @return The updated object
     */
    public T update(T t) {
        Connection connection = null;
        PreparedStatement statement = null;

        StringBuilder setFields = new StringBuilder();
        List<Object> values = new ArrayList<>();
        Object idValue = null;

        try {
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getName().equalsIgnoreCase("id")) {
                    idValue = field.get(t);
                } else {
                    if (setFields.length() > 0) {
                        setFields.append(", ");
                    }
                    setFields.append(field.getName()).append(" = ?");
                    values.add(field.get(t));
                }
            }

            String query = createUpdateQuery(setFields.toString());

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);

            PreparedStatement finalStatement = statement;
            IntStream.range(0, values.size())
                    .forEach(i -> {
                        try {
                            finalStatement.setObject(i + 1, values.get(i));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

            statement.setObject(values.size() + 1, idValue);

            statement.executeUpdate();

        } catch (SQLException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return t;
    }

    /**
     * Deletes a record from the database.
     *
     * @param t The object to delete
     * @return The deleted object
     */
    public T delete(T t) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String field;
            Object value;

            Field idField = Arrays.stream(type.getDeclaredFields())
                    .filter(f -> f.getName().equals("id"))
                    .findFirst()
                    .orElseThrow();

            idField.setAccessible(true);
            field = idField.getName();
            value = idField.get(t);

            String query = createDeleteQuery(field);
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setObject(1, value);
            statement.executeUpdate();

        } catch (SQLException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:delete " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return t;
    }
} 