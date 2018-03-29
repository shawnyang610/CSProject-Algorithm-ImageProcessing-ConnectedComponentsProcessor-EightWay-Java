//CS780-37 Project3 8-connected component algm
//Shawn Yang
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner infile=null;
        PrintWriter outfile1=null;
        PrintWriter outfile2=null;
        PrintWriter outfile3=null;
        EightWayConnectedComponent eightCC = null;


        if (args.length<4){
            System.out.println("Please supply 1 input and 3 output files");
            System.exit(1);
        }
        try {
            infile = new Scanner(new FileReader(args[0]));
            outfile1= new PrintWriter(args[1]);
            outfile2= new PrintWriter(args[2]);
            outfile3= new PrintWriter(args[3]);
        }catch (IOException e){
            System.out.println("Error opening files");
            System.exit(1);
        }

        eightCC= new EightWayConnectedComponent(infile, outfile1, outfile2, outfile3);
        eightCC.run();

        infile.close();
        outfile1.close();
        outfile2.close();
        outfile3.close();

    }
}
