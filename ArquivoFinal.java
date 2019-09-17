package compilador.projeto;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ArquivoFinal {

	private String nome;
	private ArrayList<String> linhas;
	
	public ArquivoFinal(String nome) {
		this.linhas = new ArrayList<>();
		this.nome = nome;
		
	}
	
	public void addLinha(String linha) {
		linhas.add(linha);
	}
	
	public void createOutputFile() {
		try {
			FileWriter fw = new FileWriter(nome);
			for(String lin : linhas) {
				fw.write(lin);
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Pré compilação completa!");
	}
}
