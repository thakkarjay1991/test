package java17upgrade;

import okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Central factory for {@link OkHttpClient} instances.
 *
 * Every outbound call to the Anypoint platform is created here so a corporate
 * HTTP proxy can be applied uniformly. Proxy details are supplied as JVM system
 * properties by the launcher script (java-17-upgrade-assessment-automation.bat):
 *
 *   -Dproxy.url=host:port      (or http://host:port)   optional
 *   -Dproxy.user=username                              optional
 *   -Dproxy.pass=password                              optional
 *
 * When proxy.url is not set a plain (direct) client is returned, preserving the
 * original behaviour.
 */
public class ProxyHttpClient {

    public static OkHttpClient create() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        String proxyUrl = trimToNull(System.getProperty("proxy.url"));
        if (proxyUrl != null) {
            String hostPort = proxyUrl;

            // strip an optional scheme e.g. http:// or https://
            int schemeIdx = hostPort.indexOf("://");
            if (schemeIdx >= 0) {
                hostPort = hostPort.substring(schemeIdx + 3);
            }
            // drop any trailing path if one was supplied
            int slashIdx = hostPort.indexOf('/');
            if (slashIdx >= 0) {
                hostPort = hostPort.substring(0, slashIdx);
            }

            String host;
            int port;
            int colonIdx = hostPort.lastIndexOf(':');
            if (colonIdx >= 0) {
                host = hostPort.substring(0, colonIdx);
                port = Integer.parseInt(hostPort.substring(colonIdx + 1).trim());
            } else {
                host = hostPort;
                port = 80; // sensible default when only a host is given
            }

            System.out.println("Using HTTP proxy host=" + host + " port=" + port);
            builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port)));

            final String proxyUser = trimToNull(System.getProperty("proxy.user"));
            final String proxyPass = System.getProperty("proxy.pass");
            if (proxyUser != null) {
                final String credential = Credentials.basic(proxyUser, proxyPass == null ? "" : proxyPass);
                builder.proxyAuthenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        // Avoid an infinite retry loop if the proxy keeps rejecting us.
                        if (response.request().header("Proxy-Authorization") != null) {
                            return null;
                        }
                        return response.request().newBuilder()
                                .header("Proxy-Authorization", credential)
                                .build();
                    }
                });
            }
        }
        return builder.build();
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}
