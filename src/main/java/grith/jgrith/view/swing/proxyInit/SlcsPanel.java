package grith.jgrith.view.swing.proxyInit;

import grisu.jcommons.commonInterfaces.HttpProxyInfoHolder;
import grisu.jcommons.commonInterfaces.ProxyCreatorHolder;
import grisu.jcommons.commonInterfaces.ProxyCreatorPanel;
import grisu.jcommons.exceptions.CredentialException;
import grisu.jcommons.interfaces.IdpListener;
import grisu.jcommons.interfaces.SlcsListener;
import grith.gsindl.SLCS;
import grith.jgrith.plainProxy.PlainProxy;
import grith.jgrith.utils.CredentialHelpers;
import grith.sibboleth.ShibListener;
import grith.sibboleth.ShibLoginPanel;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.ietf.jgss.GSSCredential;
import org.python.core.PyInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SlcsPanel extends JPanel implements SlcsListener,
ProxyCreatorPanel, ShibListener, IdpListener {

	private static final Logger myLogger = LoggerFactory.getLogger(SlcsPanel.class
			.getName());

	private ShibLoginPanel shibLoginPanel;
	private JButton button;

	private ProxyCreatorHolder holder = null;
	private SLCS slcs = null;

	private String url;

	private static final String DEFAULT_URL = "https://slcs1.arcs.org.au/SLCS/login";

	/**
	 * Create the panel.
	 */
	public SlcsPanel(String url) {

		if ((url == null) || "".equals(url)) {
			this.url = DEFAULT_URL;
		} else {
			this.url = url;
		}
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getShibLoginPanel(), "2, 2, fill, fill");
		add(getLoginButton(), "2, 4, right, default");
		slcs = new SLCS(getShibLoginPanel());
		slcs.addSlcsListener(this);
		enablePanel(false);
		getShibLoginPanel().refreshIdpList();

	}

	private void enablePanel(boolean enable) {
		if (enable) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
		getLoginButton().setEnabled(enable);
	}

	public Map<String, String> getCurrentSettings() {

		return new HashMap<String, String>();

	}

	private JButton getLoginButton() {
		if (button == null) {
			button = new JButton("Authenticate");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					getShibLoginPanel().login();

				}
			});
		}
		return button;
	}

	public JPanel getPanel() {
		return this;
	}

	private ShibLoginPanel getShibLoginPanel() {
		if (shibLoginPanel == null) {
			shibLoginPanel = new ShibLoginPanel(url, true);
			shibLoginPanel.addIdpListener(this);
			shibLoginPanel.addShibListener(this);
		}
		return shibLoginPanel;
	}

	public void idpListLoaded(SortedSet<String> idpList) {

		enablePanel(true);

	}

	public void setHttpProxyInfoHolder(HttpProxyInfoHolder holder) {
		// TODO Auto-generated method stub

	}

	public void setProxyCreatorHolder(ProxyCreatorHolder holder) {
		this.holder = holder;
	}

	public void shibLoginComplete(PyInstance response) {
	}

	public void shibLoginFailed(Exception e) {
		enablePanel(true);
	}

	public void shibLoginStarted() {

		enablePanel(false);
	}

	public void slcsLoginComplete(X509Certificate cert, PrivateKey privateKey) {

		try {
			enablePanel(true);
			if (holder != null) {
				GSSCredential proxy = PlainProxy.init(slcs.getCertificate(),
						slcs.getPrivateKey(), 24 * 10);
				holder.proxyCreated(CredentialHelpers
						.unwrapGlobusCredential(proxy));
			}

		} catch (CredentialException e) {

			if (holder != null) {
				holder.proxyCreationFailed(e.getLocalizedMessage());
			}
			myLogger.error("SLCS login could not be completed.", e);
		}

	}

	public void slcsLoginFailed(String message, Exception optionalException) {

		myLogger.error("SLCS login failed.", optionalException);
		enablePanel(true);

		if (holder != null) {
			holder.proxyCreationFailed(message);
		}
	}
}
