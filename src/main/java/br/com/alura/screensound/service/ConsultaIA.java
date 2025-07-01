package br.com.alura.screensound.service;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.springframework.stereotype.Service;

@Service
public class ConsultaIA {

    private static final String PROJECT_ID = System.getenv("PROJECT_ID");
    private static final String LOCATION = "us-east4";
    private static final String MODEL_NAME = "gemini-2.5-pro";

    public static String obterInformacoesArtista(String nomeArtista) {
        try (VertexAI vertexAI = new VertexAI(PROJECT_ID, LOCATION)) {

            GenerationConfig generationConfig = GenerationConfig.newBuilder()
                    .setMaxOutputTokens(2000)
                    .setTemperature(0.7F)
                    .build();

            GenerativeModel model = new GenerativeModel(MODEL_NAME, vertexAI)
                    .withGenerationConfig(generationConfig);

            String prompt = "Me forneça um breve histórico e principais características musicais do artista/banda " + nomeArtista +
                    ". Responda de forma concisa e objetiva. Inclua o gênero musical principal e 2 ou 3 músicas famosas." +
                    "Siga sem introduções o seguinte modelo, sem negrito ou qualquer outra formatação:"
                    + "Artista: " + nomeArtista + "\n" +
                    "Histórico: \n" +
                    "Gênero Musical: \n" +
                    "Nacionalidade: \n"
                    + "Musica 1: \n"
                    + "Musica 2: \n"
                    + "Musica 3: \n";

            Content content = Content.newBuilder()
                    .addParts(com.google.cloud.vertexai.api.Part.newBuilder().setText(prompt).build())
                    .setRole("user")
                    .build();

            System.out.println("Enviando requisição, aguarde... " + nomeArtista);
            GenerateContentResponse response = model.generateContent(content);

            return ResponseHandler.getText(response);

        } catch (Exception e) {
            System.err.println("Erro ao consultar a IA para informações do artista " + nomeArtista + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}