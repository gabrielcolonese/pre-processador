package compilador.projeto;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ClassePrincipal {

	public static void main(String[] args) {
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String url = scanner.nextLine(); //esse url vai pegar o arquivo de onde deve-se pré-compilar
		String nome = scanner.nextLine(); //esse nome vai pegar onde criar o novo arquivo depois de pré-compilado
		String bibliotecas = scanner.nextLine(); //esse bibliotecas vai ser o endereço onde procurar os includes
		File file = new File(url); 
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		new Parser(sc, nome, bibliotecas);
		
		/*
		while (sc.hasNextLine()) {
			System.out.println(sc.nextLine());
		}
		*/
	} 
}
