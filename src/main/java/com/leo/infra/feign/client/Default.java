package com.leo.infra.feign.client;

import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.util.StringUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

import static feign.Util.CONTENT_ENCODING;
import static feign.Util.CONTENT_LENGTH;
import static feign.Util.ENCODING_DEFLATE;
import static feign.Util.ENCODING_GZIP;
import static java.lang.String.format;

public class Default implements Client {
    
    private final SSLSocketFactory sslContextFactory;
    
    private final HostnameVerifier hostnameVerifier;
    
    
    private final String proxyHost;
    
    private final Integer proxyPort;
    
    /**
     * Disable the request body internal buffering for {@code HttpURLConnection}.
     *
     * @see HttpURLConnection#setFixedLengthStreamingMode(int)
     * @see HttpURLConnection#setFixedLengthStreamingMode(long)
     * @see HttpURLConnection#setChunkedStreamingMode(int)
     */
    private final boolean disableRequestBuffering;
    
    /**
     * Create a new client, which disable request buffering by default.
     *
     * @param sslContextFactory SSLSocketFactory for secure https URL connections.
     * @param hostnameVerifier  the host name verifier.
     */
    public Default(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier, String host, Integer port) {
        this.sslContextFactory = sslContextFactory;
        this.hostnameVerifier = hostnameVerifier;
        this.disableRequestBuffering = true;
        this.proxyHost = host;
        this.proxyPort = port;
    }
    
    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        HttpURLConnection connection = convertAndSend(request, options);
        return convertResponse(connection, request);
    }
    
    Response convertResponse(HttpURLConnection connection, Request request) throws IOException {
        int status = connection.getResponseCode();
        String reason = connection.getResponseMessage();
        
        if (status < 0) {
            throw new IOException(format("Invalid status(%s) executing %s %s", status, connection.getRequestMethod(), connection.getURL()));
        }
        
        Map<String, Collection<String>> headers = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> field : connection.getHeaderFields().entrySet()) {
            // response message
            if (field.getKey() != null) {
                headers.put(field.getKey(), field.getValue());
            }
        }
        
        Integer length = connection.getContentLength();
        if (length == -1) {
            length = null;
        }
        InputStream stream;
        if (status >= 400) {
            stream = connection.getErrorStream();
        } else {
            if (this.isGzip(connection.getHeaderFields().get(CONTENT_ENCODING))) {
                stream = new GZIPInputStream(connection.getInputStream());
            } else if (this.isDeflate(connection.getHeaderFields().get(CONTENT_ENCODING))) {
                stream = new InflaterInputStream(connection.getInputStream());
            } else {
                stream = connection.getInputStream();
            }
        }
        return Response.builder().status(status).reason(reason).headers(headers).request(request).body(stream, length).build();
    }
    
    public HttpURLConnection getConnection(final URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }
    
    HttpURLConnection convertAndSend(Request request, Request.Options options) throws IOException {
        
        Proxy proxy = null;
        if (StringUtils.hasText(proxyHost)) {
            proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
        }
        final HttpURLConnection
                connection =
                (HttpURLConnection) (proxy == null ? new URL(request.url()).openConnection() : new URL(request.url()).openConnection(proxy));
        
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection sslCon = (HttpsURLConnection) connection;
            if (sslContextFactory != null) {
                sslCon.setSSLSocketFactory(sslContextFactory);
            }
            if (hostnameVerifier != null) {
                sslCon.setHostnameVerifier(hostnameVerifier);
            }
        }
        connection.setConnectTimeout(options.connectTimeoutMillis());
        connection.setReadTimeout(options.readTimeoutMillis());
        connection.setAllowUserInteraction(false);
        connection.setInstanceFollowRedirects(options.isFollowRedirects());
        connection.setRequestMethod(request.httpMethod().name());
        
        Collection<String> contentEncodingValues = request.headers().get(CONTENT_ENCODING);
        boolean gzipEncodedRequest = this.isGzip(contentEncodingValues);
        boolean deflateEncodedRequest = this.isDeflate(contentEncodingValues);
        
        boolean hasAcceptHeader = false;
        Integer contentLength = null;
        for (String field : request.headers().keySet()) {
            if (field.equalsIgnoreCase("Accept")) {
                hasAcceptHeader = true;
            }
            for (String value : request.headers().get(field)) {
                if (field.equals(CONTENT_LENGTH)) {
                    if (!gzipEncodedRequest && !deflateEncodedRequest) {
                        contentLength = Integer.valueOf(value);
                        connection.addRequestProperty(field, value);
                    }
                } else {
                    connection.addRequestProperty(field, value);
                }
            }
        }
        // Some servers choke on the default accept string.
        if (!hasAcceptHeader) {
            connection.addRequestProperty("Accept", "*/*");
        }
        
        if (request.body() != null) {
            if (disableRequestBuffering) {
                if (contentLength != null) {
                    connection.setFixedLengthStreamingMode(contentLength);
                } else {
                    connection.setChunkedStreamingMode(8196);
                }
            }
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            if (gzipEncodedRequest) {
                out = new GZIPOutputStream(out);
            } else if (deflateEncodedRequest) {
                out = new DeflaterOutputStream(out);
            }
            try {
                out.write(request.body());
            } finally {
                try {
                    out.close();
                } catch (IOException suppressed) { // NOPMD
                }
            }
        }
        return connection;
    }
    
    private boolean isGzip(Collection<String> contentEncodingValues) {
        return contentEncodingValues != null && !contentEncodingValues.isEmpty() && contentEncodingValues.contains(ENCODING_GZIP);
    }
    
    private boolean isDeflate(Collection<String> contentEncodingValues) {
        return contentEncodingValues != null && !contentEncodingValues.isEmpty() && contentEncodingValues.contains(ENCODING_DEFLATE);
    }
}