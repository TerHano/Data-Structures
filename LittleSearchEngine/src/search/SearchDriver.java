package search;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by terryhanoman on 3/30/17.
 */
public class SearchDriver {
    static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        System.out.println("Enter document list file name");
        String doc = scan.nextLine();
        doc = exist(doc);
        LittleSearchEngine test = new LittleSearchEngine();
        test.makeIndex(doc, "noisewords.txt");
        System.out.println("Would you like to search this document? (Yes/No)");
        String ques = scan.nextLine();
        while (!ques.equalsIgnoreCase("No")) {
            System.out.println("Enter your first keyword");
            String search1 = scan.nextLine();
            System.out.println("Enter your second keyword");
            String search2 = scan.nextLine();
            ArrayList<String> result = test.top5search(search1,search2);
            if(result == null)
            {
                System.out.println("No matches!");
            }
            else {
                for (String s : result) {
                    System.out.println(s);
                }
            }
            System.out.println("Would you like to search this document again? (Yes/No)");
            ques = scan.nextLine();
        }
    }
    public static String exist(String Filename)
    {
        try{
            new Scanner(new File(Filename));
        }
        catch (IOException e)
        {
            System.out.println("Wrong filename or file does not exist!");
            System.out.println("Enter correct file name or type " +  "\"quit\"" + " to terminate program");
            Filename = scan.nextLine();
            if(!Filename.equals("quit")) {
                return exist(Filename);
            }
            else{
                System.out.println("Terminating...");
                System.exit(0);
            }
        }
        return Filename;
    }
}
