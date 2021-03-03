
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Main {
	
	
	public static void main(String[] args) {
		LZ77 lz77 = new LZ77();
		String Data = null;
		Scanner scanner = new Scanner(System.in);
		Data=scanner.nextLine();
		lz77.setString(Data);
		try {
			PrintWriter writer = new PrintWriter("/home/seif/Desktop/mm.txt","UTF-8");
			writer.print(lz77.show_tags());
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		File file = new File("/home/seif/Desktop/mm.txt");
		try {
			Scanner scan = new Scanner(file);
			while(scan.hasNextLine())
			{
				String line =null;
				line = scan.nextLine();
				System.out.println("Tags"+line);
				
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
		}
		System.out.println(lz77.getString());
	}

}
