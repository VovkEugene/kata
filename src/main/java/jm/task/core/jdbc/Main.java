package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
        UserService service = new UserServiceImpl();

        service.createUsersTable();
        service.saveUser("Fedor", "Pumpkin", (byte) 41);
        service.saveUser("Vasya", "Banana", (byte) 18);
        service.saveUser("Lena", "Cabbage", (byte) 22);
        service.saveUser("Petya", "Superhero", (byte) 30);
        service.getAllUsers();
        service.cleanUsersTable();
        service.dropUsersTable();
    }
}
