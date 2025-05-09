import java.util.*;
import java.sql.*;

public class Main {
    public static void data_base(){
        String url = "jdbc:sqlite:fitness_center.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                createTable(conn);

                // Вставка тестовых пользователей
                insertTestUser(conn, "director", "admin", "1234");
                insertTestUser(conn, "client", "client1", "pass");
                insertTestUser(conn, "manager", "manager1", "qwerty");
                insertTestUser(conn, "personal", "trainer", "abcd");

                key_word(conn);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка подключения к БД: " + e.getMessage());
        }
    }

    static void createTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "role TEXT NOT NULL," +
                "login TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void login(String key_word, Connection conn){
        Scanner scan = new Scanner(System.in);

        System.out.print("Login: ");
        String login = scan.nextLine();

        System.out.print("Password: ");
        String password = scan.nextLine();

        String sql = "SELECT * FROM users WHERE login = ? AND password = ? AND role = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            pstmt.setString(3, key_word);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Добро пожаловать, " + key_word + "!");
            } else {
                System.out.println("Неверный логин или пароль.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка авторизации: " + e.getMessage());
        }
    }

    public static void personal(Connection conn){
        login("personal", conn);
    }

    public static void director(Connection conn){
        Scanner scan = new Scanner(System.in);
        login("director", conn);

        while (true) {
            System.out.println("\nМеню директора:");
            System.out.println("1. Зарегистрировать нового пользователя");
            System.out.println("2. Посмотреть всех пользователей");
            System.out.println("3. Удалить пользователя");
            System.out.println("4. Выйти");
            System.out.print("Выбор: ");
            String choice = scan.nextLine();

            switch (choice) {
                case "1":
                    registerUser(conn, "director");
                    break;
                case "2":
                    viewAllUsers(conn);
                    break;
                case "3":
                    deleteUser(conn);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Неверный выбор.");
            }
        }
    }

    public static void manager(Connection conn){
        Scanner scan = new Scanner(System.in);
        login("manager", conn);

        while (true) {
            System.out.println("\nМеню менеджера:");
            System.out.println("1. Зарегистрировать нового пользователя");
            System.out.println("2. Выйти");
            System.out.print("Выбор: ");
            String choice = scan.nextLine();

            switch (choice) {
                case "1":
                    registerUser(conn, "manager");
                    break;
                case "2":
                    return;
                default:
                    System.out.println("Неверный выбор.");
            }
        }
    }

    public static void client(Connection conn){
        login("client", conn);
    }

    public static void registerUser(Connection conn, String creatorRole) {
        Scanner scan = new Scanner(System.in);

        System.out.println("\n=== Регистрация нового пользователя ===");

        System.out.print("Введите роль (client / personal / manager / director): ");
        String role = scan.nextLine();

        System.out.print("Придумайте логин: ");
        String login = scan.nextLine();

        System.out.print("Придумайте пароль: ");
        String password = scan.nextLine();

        String sql = "INSERT INTO users (role, login, password) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            pstmt.setString(2, login);
            pstmt.setString(3, password);
            pstmt.executeUpdate();

            System.out.println("✅ Пользователь успешно зарегистрирован.");
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("⚠ Такой логин уже существует.");
            } else {
                System.out.println("Ошибка регистрации: " + e.getMessage());
            }
        }
    }

    public static void deleteUser(Connection conn) {
        Scanner scan = new Scanner(System.in);

        System.out.print("Введите логин пользователя, которого хотите удалить: ");
        String login = scan.nextLine();

        // Запрещаем удалять самого себя или админа
        if (login.equals("admin")) {
            System.out.println("⚠ Вы не можете удалить главного администратора.");
            return;
        }

        String sql = "DELETE FROM users WHERE login = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("✅ Пользователь удалён.");
            } else {
                System.out.println("⚠ Пользователь с таким логином не найден.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }

    public static void viewAllUsers(Connection conn) {
        String sql = "SELECT id, role, login FROM users ORDER BY id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n📋 Список пользователей:");
            System.out.println("ID | Роль     | Логин");
            System.out.println("----------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role");
                String login = rs.getString("login");
                System.out.printf("%-2d | %-8s | %s%n", id, role, login);
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при получении пользователей: " + e.getMessage());
        }
    }

    public static void key_word(Connection conn){
        Scanner scan = new Scanner(System.in);

        System.out.println("--------");
        System.out.println("personal");
        System.out.println("director");
        System.out.println("manager");
        System.out.println("client");
        System.out.println("--------");

        boolean error = true;

        while (error) {
            error = false;
            System.out.print("your key word: ");
            switch (scan.nextLine()){
                case "personal":
                    personal(conn); break;
                case "director":
                    director(conn); break;
                case "manager":
                    manager(conn); break;
                case "client":
                    client(conn); break;
                default:
                    error = true;
                    System.out.println("error");
            }
        }
    }

    public static void insertTestUser(Connection conn, String role, String login, String password) {
        String sql = "INSERT OR IGNORE INTO users(role, login, password) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            pstmt.setString(2, login);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
            System.out.println("Пользователь добавлен: " + login + " (" + role + ")");
        } catch (SQLException e) {
            System.out.println("Ошибка добавления пользователя: " + e.getMessage());
        }
    }

    public static void main(String[] args){
        data_base();
    }
}