import java.util.*;
import java.io.*;

public class Main {
    public static ArrayList<String> logins = new ArrayList<>();

    public static HashMap<String, Boolean> HM_login_global = new HashMap<>();

    public static HashMap<String, String> HM_login_check = new HashMap<>();

    public static HashMap<String, String> HM_password = new HashMap<>();

    public static void data_base(){
        try {
            File file = new File("data_base.txt");

            if (file.createNewFile()) {
                System.out.println("The file is created");
            }
            else {
                System.out.println("The file already exists");
            }
        }
        catch (IOException e) {
            System.out.println("Error creating the file");
        }

        try {
            FileReader fileReader = new FileReader("data_base.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String keyword = "";
                String login = "";
                String password = "";

                for (int i=0, k=0, c=0; i<line.length(); ++i){
                    if (line.charAt(i) == ' ' || i+1 == line.length()){
                        ++c;
                        if (c == 1){
                            keyword = line.substring(0, i);
                        }
                        if (c == 2){
                            login = line.substring(k, i);
                        }
                        else{
                            password = line.substring(k, i+1);
                        }
                        k = i+1;
                    }
                }
                logins.add(line);

                HM_login_global.put(login, true);
                HM_login_check.put(keyword, login);
                HM_password.put(login, password);
            }

            bufferedReader.close();
        }
        catch (IOException e) {
            System.out.println("Error creating the file");
        }
    }

    public static void key(){
        Scanner sc = new Scanner(System.in);
        boolean flag = true;

        while(flag){
            flag = false;

            System.out.print("Enter your keyword: ");
            String key = sc.nextLine();

            if(key.equals("personal")){
                sign_in();
                personal();
            }
            else if (key.equals("director")) {
                sign_in();
            }
            else if(key.equals("manager")){
                sign_in();
            }
            else if (key.equals("client")){
                sign_in();
            }
            else {
                System.out.println("Unnexpected keyword");
                flag = true;
            }
        }
    }

    public static void sign_in(){
        Scanner sc = new Scanner(System.in);


    }

    public static void personal(){
        Scanner sc = new Scanner(System.in);


    }

    public static void main(String[] args) {
        data_base();

        key();

        sign_in();
    }
}