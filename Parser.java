package compilador.projeto;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.ArrayList;

public class Parser {
	
	public Parser(Scanner scanner, String nome, String bibliotecas){
		HashMap<String, String> defines = new HashMap<String, String>();
		HashMap<String, String> typedef = new HashMap<String, String>();
		
		run(scanner, defines, typedef, nome, bibliotecas);
	}
    
	boolean temParametroEspecial(String palavra, HashMap<String, String> typedef){
		boolean resp = false;
		
		resp = (palavra.equals("int") || palavra.equals("char") || palavra.equals("double") || palavra.equals("float") || palavra.equals("FILE") || palavra.equals("void")
        		|| palavra.equals("short") || palavra.equals("unsigned") || palavra.equals("signed") || palavra.equals("long") || palavra.equals("struct") || palavra.equals("return"));
		
		
		for(Entry<String, String> typedefs : typedef.entrySet()) {//caso especial para tratar dos typdefs em structs
			if(!palavra.equals("")){
				String[] partes = typedefs.getValue().split(" "); // essa parte vai dividir o typedef em partes
				for(int i=0;i<partes.length;i++){ // vai checar se uma das partes dela é igual a palavra que nois estamos procurando
					if(partes[i].contentEquals(palavra))resp = true; 
				}
				
			}
		}
		
		return resp;
			
	}
	
    String removeEspaco(String frase, HashMap<String, String> typedef){ 
    	String novo = "";      
        String[] subs;
        int i = 0;
        
        subs = frase.split("\\s+"); //Divide a string em blocos e poe no array
        while(i<subs.length){
        	//System.out.println(subs[i] + "      indice: " + i);
        	novo = novo + subs[i].trim();
        	if(temParametroEspecial(subs[i], typedef)){
        		novo = novo + " ";
            }
            i++;
        }
        return novo;
	}
	
    String trataEspaco(String frase, HashMap<String, String> typedef){
        ArrayList<String> asubs = new ArrayList<String>();
        String novo = "";
        int i = 0;
        int j = 0;
        
        if(frase.contains("\""));{
            while(j<frase.length()){
                j = frase.indexOf("\"", i+1);
                if(j>i && j<=frase.length()){
                    asubs.add(frase.substring (i,j));
                }
                
                if(j == -1){
                    asubs.add(frase.substring(i, frase.length()));
                    j = frase.length();
                }
                i = j;
            }
        }
        
        i=0;
        while(i<asubs.size()){
            if(i%2==0){
            	novo = novo + removeEspaco(asubs.get(i), typedef);
            }else{
                novo = novo + asubs.get(i);
            }
            i++;
        }
        return novo;
    }
    
    
	private void run(Scanner scanner, HashMap<String, String> defines, HashMap<String, String> typedef, String nome, String bibliotecas) {
		if(scanner != null) {
			ArquivoFinal arquivo = new ArquivoFinal(nome);
			int toggleAdd = 1; //addLinha so eh acionado se for == 1
			System.out.println("Pre compilacao iniciada!");
			while(scanner.hasNextLine()) {
				//uma string vai receber a linha que estamos lendo e a outra vai ser a linha que vai ser modificada pelo compilador
				String antiga = scanner.nextLine();
				String atualizada = antiga;
				//checar se tem algum define pra substituir
				for(Entry<String, String> definidos : defines.entrySet()) {
					if(antiga.contains(definidos.getKey())) {
						antiga = antiga.replace(definidos.getKey(), definidos.getValue());
					}
				}
				//checar se tem algum typedef pra substituir
				for(Entry<String, String> typedefizados : typedef.entrySet()) {
					if(antiga.contains(typedefizados.getKey())) {
						antiga = antiga.replace(typedefizados.getKey(), typedefizados.getValue());
					}
				}
				
				//vai adicionar as linhas do include para as linhas do arquivo final
				if(antiga.startsWith("#include")) {
					String sub = antiga.substring(9);//vai pegar o nome do arquivo a ser adicionado
					sub = sub.replaceAll("\"*", "");
					sub = sub.replaceAll("<|>", "");
					File file = new File(bibliotecas+sub); 
					Scanner sc = null;
					try {
						sc = new Scanner(file);
					} catch (FileNotFoundException e) {
						System.out.println("Nao foi possivel achar esse diretorio: "+bibliotecas+sub);
						e.printStackTrace();
					}
					while(sc.hasNextLine()) {
						arquivo.addLinha(sc.nextLine()); //aqui vai adicionar os arquivos da biblioteca diretamente no codigo final
					}
				}
				if(antiga.startsWith("#define")) {
					//vai adicionar no map para substituir no programa inteiro
					String sub = antiga.substring(8);
					String[] split = sub.split(" ");
					String junto = "";
					for(int i=1;i<split.length;i++) {
						junto+=split[i];
					}
					defines.put(split[0], junto);	
				}
				if(antiga.startsWith("typedef")) {
					//vai adicionar no map para substituir no programa inteiro
					String sub = antiga.substring(8);
					sub = sub.replace(";", "");//retira o ;
					String[] split = sub.split(" ");
					String junto = "";
					for(int i=0;i<split.length-1;i++) {
						junto+=" "+split[i];
					}
					typedef.put(split[split.length-1], junto);
				}
				
				//a partir daqui os includes e defines já foram tratados
				//aqui os textos serão tratados no string antiga e somente tudo será concatenado na nova string
				antiga = antiga.replaceAll("#define.*", "");
				antiga = antiga.replaceAll("#include.*", "");
				antiga = antiga.replaceAll("typedef.*", "");
                
				if(antiga.contains("/*")){
                	toggleAdd = 0;
                    antiga = antiga.replaceAll("\\/\\*.*", ""); //Apaga tudo apos o /* nesta linha
                }
                if(antiga.contains("*/")){
                	toggleAdd = 1;
                	antiga = antiga.replaceAll(".*\\*\\/", ""); //apaga tudo antes do */ nesta linha
                }
                          
				antiga = antiga.replaceAll("\\/{2}(.*)", ""); //apaga os // e o que vier depois
				
				atualizada = trataEspaco(antiga, typedef);
                                
                if(toggleAdd == 1){
                	arquivo.addLinha(atualizada);
                }
			}
			
			arquivo.createOutputFile();
		}
	}
}
