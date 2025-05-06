package DataAccess;

import java.io.*;

import BusinessLogic.TasksManagement;

public class SerializationOperations {

    private static final String FILE_NAME = "tasks_data.ser";

    public static void saveTasksManagement(TasksManagement tasksManagement) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            objectOutputStream.writeObject(tasksManagement);
        } catch (IOException e) {
            System.out.println("Eroare la salvarea datelor: " + e.getMessage());
        }
    }

    public static TasksManagement loadTasksManagement() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (TasksManagement) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            return new TasksManagement();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Eroare la încărcarea datelor: " + e.getMessage());
            return new TasksManagement();
        }
    }
}
