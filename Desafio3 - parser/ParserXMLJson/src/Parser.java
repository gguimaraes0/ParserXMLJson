import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parser {
    public static void main(String[] args) throws Exception {
        System.out.println("Digite o caminho para o arquivo XML que você quer converter para JSON: ");

        Scanner scanner = new Scanner(System.in);
        var path = System.console().readLine();
        System.out.println("----------------------------------");
        
        Path caminho = Paths.get(path);
        //List<String> list = Files.readAllLines(caminho, StandardCharsets.UTF_8);
        List<String> list = new ArrayList<String>();
        try (Scanner scanner2 = new Scanner(caminho)) {
            while (scanner2.hasNext()) {
                list.add(scanner2.nextLine());
            }
        }

        if(list.isEmpty()){
            System.out.println("Não foi possível encontrar o arquivo.");
        }else{

            try {
                var response = parseToJson(list);
                System.out.println(response);
            
                System.out.println("----------------------------------");
                File arquivo = new File("Saida.json");
                arquivo.createNewFile();
                try (FileWriter escritor = new FileWriter("Saida.json")) {
                    escritor.write(response);
                }
                System.out.println("\nArquivo de saída criado!");
            } catch (IOException erro) {
                System.out.println("Ocorreu um erro!");
            }
        }
        
        System.out.println("----------------------------------");
        scanner.close();
    }

    public static String parseToJson(List<String> lines) {
        String response = "";
        boolean elements = false;
        boolean isListing = false;
        boolean previousOpenTag = false;
        String currentTag;
        String previousTag = "";

        response += "{";
        
        for (String line : lines) {
            line = line.trim();
            line = line.substring(1);
            if (line.contains("<")) {
                // Tem abre e fecha tag
                if (elements) {
                    response += ",";
                }
                response += System.lineSeparator();
                elements = true;
                response += "\"" + line.substring(0, line.indexOf('>')) + "\": \""
                        + line.substring(line.indexOf('>') + 1, line.indexOf('<')) + "\"";
                previousOpenTag = false;
            } else if (line.contains("/")) {
                // um fecha tag
                elements = false;
                currentTag = line.substring(1, line.length() - 1);
                if (isListing && !previousTag.equals(currentTag)) {
                    response += System.lineSeparator() + "]";
                    isListing = false;
                }
                response += System.lineSeparator() + "}";
                previousOpenTag = false;
            } else {
                // um abre tag
                elements = false;
                currentTag = line.substring(0, line.length() - 1);
                if (previousTag.equals(currentTag)) {
                    response += "," + System.lineSeparator() + "{";
                } else {
                    if (!previousTag.equals("") && previousOpenTag) {
                        int indice = response.lastIndexOf(previousTag) + previousTag.length() + 2;
                        response = response.substring(0, indice) + response.substring(indice + 2);
                        isListing = false;
                    }
                    if (isListing) {
                        response += System.lineSeparator() + "]";
                    }
                    response += System.lineSeparator() + "\"" + currentTag + "\": [ {";
                    isListing = true;
                }
                previousTag = currentTag;
                previousOpenTag = true;
            }
        }
        response += System.lineSeparator() + "}";

        return response;
    }

}
