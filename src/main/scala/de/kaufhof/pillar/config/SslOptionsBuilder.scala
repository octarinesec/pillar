package de.kaufhof.pillar.config

import java.io.FileInputStream
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManager, SSLContext, TrustManagerFactory}

import com.datastax.driver.core.{JdkSSLOptions, SSLOptions}

class SslOptionsBuilder {
  var keystore: KeyStore = null
  var trustManagerFactory: TrustManagerFactory = null
  var sslContext: SSLContext = null

  def withKeyStore(storeFile: String, storePassword: String, storeType: String = "JKS") = {
    keystore = KeyStore.getInstance(storeType)
    keystore.load(new FileInputStream(storeFile), storePassword.toCharArray)
  }

  def withTrustManager(trustManagerAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm) = {
    trustManagerFactory = TrustManagerFactory.getInstance(trustManagerAlgorithm)
  }

  def withSslContext(sslContextType: String = "SSL") = {
    sslContext = SSLContext.getInstance(sslContextType)
  }

  def build(): SSLOptions = {
    trustManagerFactory.init(keystore)
    sslContext.init(new Array[KeyManager](0), trustManagerFactory.getTrustManagers, new SecureRandom())
    JdkSSLOptions.builder().withSSLContext(sslContext).build();
  }
}
