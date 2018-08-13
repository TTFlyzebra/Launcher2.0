package com.ppfuns.ppfunstv.http;

import android.content.Context;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okio.Buffer;

/**
 * Created by Administrator on 2016/5/18.
 */
public class FlySSLSocketFactory {
    /**
     * 网站证书
     */
    private static String CER_ME = "-----BEGIN CERTIFICATE-----\n" +
            "MIICujCCAiOgAwIBAgIJAJ76rhKF6TOsMA0GCSqGSIb3DQEBCwUAMHYxCzAJBgNV\n" +
            "BAYTAkNOMRIwEAYDVQQIDAlHdWFuZ0RvbmcxETAPBgNVBAcMCFNoZW5aaGVuMREw\n" +
            "DwYDVQQKDAhGbHlaZWJyYTERMA8GA1UECwwIUmVkTWFwbGUxGjAYBgNVBAMMEWZs\n" +
            "eXplYnJhLndpY3AubmV0MB4XDTE2MDUxNzE1MjQ1M1oXDTE3MDUxNzE1MjQ1M1ow\n" +
            "djELMAkGA1UEBhMCQ04xEjAQBgNVBAgMCUd1YW5nRG9uZzERMA8GA1UEBwwIU2hl\n" +
            "blpoZW4xETAPBgNVBAoMCEZseVplYnJhMREwDwYDVQQLDAhSZWRNYXBsZTEaMBgG\n" +
            "A1UEAwwRZmx5emVicmEud2ljcC5uZXQwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJ\n" +
            "AoGBALtCWiITVpKfdWKyfNQ+jsJSht2hMkVw58xHKy2tAgFsVLqSXWUEgzrKxSWB\n" +
            "zT0hmT6pUbJrtUkD9vlMgTMz67mq3uOFloVGRwnaSbAY0HTSjKix2keiHpBatN9R\n" +
            "+q8Rcdr8YiX3skIDiUvzKe5uWRdDSPds6hiBARYVZ/2elDDpAgMBAAGjUDBOMB0G\n" +
            "A1UdDgQWBBQok01zFd4cBfrti6ZdZBlph2j5PjAfBgNVHSMEGDAWgBQok01zFd4c\n" +
            "Bfrti6ZdZBlph2j5PjAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBCwUAA4GBAKtX\n" +
            "4wXqjzqJChJXUMI4BkTThNg8IeFLI/7Vqv2fHGRanDQe+TLVrNJ/0KD2kt5QNTwo\n" +
            "e/H94dOADkBbtt0Wpmg5tPNZtLBjhGZL1c2nhhCmwGeevLerz+dUg/UBFhf1wIN9\n" +
            "KM8wunU9rwhzyuoplYpp4H16SSPwE+EOno4iz6mk\n" +
            "-----END CERTIFICATE-----";

    /**
     * Https 支持
     *
     * @param certificates
     * @return
     */
    public static SSLContext setCertificates(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SSLContext getStringCertificates() {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(new Buffer().writeUtf8(CER_ME).inputStream()));
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从raw文件中生存SSLSocketFactory
     *
     * @param context
     * @param certRawResId
     * @return
     */
    public static SSLSocketFactory buildSSLSocketFactory(Context context, int certRawResId) {
        KeyStore keyStore = null;
        try {
            keyStore = buildKeyStore(context, certRawResId);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext.getSocketFactory();
    }

    public static HttpClient getHttpClient(Context context, int certRawResId) {
        KeyStore keyStore = null;
        try {
            keyStore = buildKeyStore(context, certRawResId);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (keyStore != null) {
        }
        org.apache.http.conn.ssl.SSLSocketFactory sslSocketFactory = null;
        try {
            sslSocketFactory = new org.apache.http.conn.ssl.SSLSocketFactory(keyStore);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        HttpParams params = new BasicHttpParams();

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));

        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);


        return new DefaultHttpClient(cm, params);
    }

    private static KeyStore buildKeyStore(Context context, int certRawResId) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);

        Certificate cert = readCert(context, certRawResId);
        keyStore.setCertificateEntry("ca", cert);

        return keyStore;
    }

    private static Certificate readCert(Context context, int certResourceID) {
        InputStream inputStream = context.getResources().openRawResource(certResourceID);
        Certificate ca = null;
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            ca = cf.generateCertificate(inputStream);

        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return ca;
    }
}
