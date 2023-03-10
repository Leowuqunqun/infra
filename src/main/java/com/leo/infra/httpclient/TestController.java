package com.leo.infra.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @PostMapping(path = "/input")
    public Object insertList(MultipartFile excel, @RequestParam("name") String name) throws IOException {
        return name;
    }
    
    @GetMapping
    public Object send() throws IOException {
        
        // 管道流
        PipedInputStream pipedInputStream = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        pipedInputStream.connect(pipedOutputStream);
        
        // 本地文件
        InputStream file = Files.newInputStream(Paths.get("/Users/leo/Downloads/Readme.txt"));
        File file11 = new File("/Users/leo/Downloads/Readme.txt");
    
        // 构建Multipart请求
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                // 表单数据
                .addPart("ttt", new StringBody(UriUtils.encode("222", StandardCharsets.UTF_8), ContentType.APPLICATION_FORM_URLENCODED))
                // 文件数据
                .addPart("excel", new FileBody(file11)).build();
        
        // 异步写入数据到管道流
        new Thread(() -> {
            try (file;
                    pipedOutputStream) {
                httpEntity.writeTo(pipedOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        
        HttpClient httpClient = HttpClient.newHttpClient();
        
        try (pipedInputStream) {
            // 创建请求和请求体
            HttpRequest request = HttpRequest.newBuilder(new URI("http://localhost:8090/test/input"))
                    // 设置ContentType
                    .header("Content-Type", httpEntity.getContentType().getValue()).header("Accept", "text/plain")
                    // 从管道流读取数据，提交给服务器
                    .POST(HttpRequest.BodyPublishers.ofInputStream(() -> pipedInputStream)).build();
            
            // 执行请求，获取响应
            HttpResponse<String> responseBody = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            
            System.out.println(responseBody.body());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    
        return null;
    }
    
}
