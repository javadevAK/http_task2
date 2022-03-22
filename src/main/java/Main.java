import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    private final static String KEY = "QyDRC6wpeq7PPKPDrT2dBsDUigUyvI4NNPgC7S7D";
    private final static String URI = "https://api.nasa.gov/planetary/apod?api_key=" + KEY;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

//создаем http-client
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        //объект запроса
        HttpGet request = new HttpGet(URI);

        //отправка запроса
        CloseableHttpResponse response = httpClient.execute(request);

        Post post = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
        });

        HttpGet request2 = new HttpGet(post.getHdurl());
        CloseableHttpResponse response2 = httpClient.execute(request2);

        InputStream inputStream = response2.getEntity().getContent();
        String[] fileURL = post.getHdurl().split("/");
        String fileName = fileURL[fileURL.length - 1];

        // сохранение файла
        saveFile(fileName, inputStream);
    }

    public static void saveFile(String fileName, InputStream inputFile) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            int inByte;
            while ((inByte = inputFile.read()) != -1)
                fos.write(inByte);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
