package grith.jgrith.utils;

import grith.jgrith.certificate.CertificateHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.globus.common.CoGProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CertificateFiles {

	static final Logger myLogger = LoggerFactory
			.getLogger(CertificateFiles.class.getName());

	public static final File SYSTEM_CA_CERT_DIR = new File(
			"/etc/grid-security/certificates");

	private static boolean caCertsCopied = false;

	/**
	 * This one copies the CA certificates (in the certificates.zip file) into
	 * the .globus/certificates directory if they are not already there...
	 * 
	 * @throws Exception
	 * 
	 */
	public static void copyCACerts(boolean force) throws Exception {

		if (!force && SYSTEM_CA_CERT_DIR.exists()
				&& SYSTEM_CA_CERT_DIR.isDirectory()) {
			myLogger.info("Using system ca cert dir in /etc/grid-security/certificates");
			return;
		}

		if (!caCertsCopied) {

			// needed for APAC signing policy file...
			// there's a bug in jglobus that doesn't work with it...
			CoGProperties.getDefault().setProperty(
					CoGProperties.ENFORCE_SIGNING_POLICY, "false");

			createGlobusDirectory();

			createCertificatesDirectory();

			// File certDir = CertificateHelper.getCertificatesDir();
			File certDir = new File(CertificateHelper.getGlobusDir(),
					"certificates");
			int BUFFER_SIZE = 8192;
			int count;
			byte data[] = new byte[BUFFER_SIZE];

			InputStream in = CertificateFiles.class
					.getResourceAsStream("/certificates.zip");
			ZipInputStream certStream = new ZipInputStream(in);

			BufferedOutputStream dest = null;

			try {

				ZipEntry cert = null;

				while ((cert = certStream.getNextEntry()) != null) {

					if (!cert.isDirectory()) {

						try {
							myLogger.debug("Certificate name: "
									+ cert.getName());
							File cert_file = new File(certDir, cert.getName());

							// exception for the apacgrid cert
							if (!cert_file.exists()
									|| cert_file.getName().startsWith(
											"1e12d831")
											|| cert_file.getName().startsWith(
													"1ed4795f")) {

								// Write the file to the file system
								FileOutputStream fos = new FileOutputStream(
										cert_file);
								dest = new BufferedOutputStream(fos,
										BUFFER_SIZE);
								while ((count = certStream.read(data, 0,
										BUFFER_SIZE)) != -1) {
									dest.write(data, 0, count);
								}
								dest.flush();
								dest.close();
							}
						} catch (Exception e) {
							myLogger.debug("Could not write certificate: "
									+ cert.getName());
						}

					}
				}

			} catch (IOException e) {
				myLogger.debug(e.getLocalizedMessage());
				throw new Exception("Could not write certificate: "
						+ e.getLocalizedMessage(), e);
			}
			caCertsCopied = true;
		}
	}

	/**
	 * Creates the certificates directory if it doesn't exist yet
	 * 
	 * @throws Exception
	 *             if something goes wrong
	 */
	public static void createCertificatesDirectory() throws Exception {

		// File certDir = CertificateHelper.getCertificatesDir();
		File certDir = new File(CertificateHelper.getGlobusDir(),
				"certificates");
		if (!certDir.exists()) {
			if (!certDir.mkdirs()) {
				myLogger.error("Could not create certificates directory");
				throw new Exception(
						"Could not create certificates directory. Please set permissions for "
								+ certDir.toString() + " to be created");
			}
		}

	}

	/**
	 * Creates the globus directory if it doesn't exist yet.
	 * 
	 * @throws Exception
	 *             if something goes wrong
	 */
	public static void createGlobusDirectory() throws Exception {

		File globusDir = CertificateHelper.getGlobusDir();

		if (!globusDir.exists()) {
			if (!globusDir.mkdirs()) {
				myLogger.error("Could not create globus directory.");
				throw new Exception(
						"Could not create globus directory. Please set permissions for "
								+ globusDir.toString() + " to be created.");
			}
		}
	}

}
