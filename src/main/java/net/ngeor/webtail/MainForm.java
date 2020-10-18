package net.ngeor.webtail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author ngeor
 */
public class MainForm extends javax.swing.JFrame {

    private static final int DELAY = 1000;
    private static final int PORT_HTTPS = 443;
    private static final int PREF_SCROLLPANE1_SIZE = 325;
    private static final int PREF_SCROLLPANE1_HORIZONTAL_SIZE = 750;
    private final DefaultListModel model = new DefaultListModel();
    private final Timer timer;
    private RemoteFile remoteFile;

    private javax.swing.JButton btnRemote;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList lstLines;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtUrl;
    private javax.swing.JTextField txtUsername;

    /**
     * Creates new form MainForm.
     */
    public MainForm() {
        initComponents();

        timer = new Timer(DELAY, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    updateRemoteFile();
                } catch (IOException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        timer.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings({ "unchecked", "checkstyle:LineLength" })
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lstLines = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        txtUrl = new javax.swing.JTextField();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JTextField();
        btnRemote = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WebTail");

        lstLines.setModel(model);
        jScrollPane1.setViewportView(lstLines);

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onButtonClick(evt);
            }
        });

        txtUrl.setText("url");
        jPanel1.add(txtUrl);

        txtUsername.setText("username");
        jPanel1.add(txtUsername);

        txtPassword.setText("password");
        jPanel1.add(txtPassword);

        btnRemote.setText("Tail remote file");
        btnRemote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoteActionPerformed(evt);
            }
        });
        jPanel1.add(btnRemote);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, PREF_SCROLLPANE1_HORIZONTAL_SIZE,
                        Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup().addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton1).addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addContainerGap().addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane1,
                                javax.swing.GroupLayout.DEFAULT_SIZE, PREF_SCROLLPANE1_SIZE, Short.MAX_VALUE)
                        .addContainerGap()));

        pack();
    }

    private void onButtonClick(java.awt.event.ActionEvent evt) {
        try {
            updateRemoteFile();
        } catch (IOException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void btnRemoteActionPerformed(java.awt.event.ActionEvent evt) {
        String url = txtUrl.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        remoteFile = new RemoteFile(url, new Credentials(username, password));
    }

    /**
     * Starts the main form.
     */
    public static void main(String[] args) {
        // Set the Nimbus look and feel
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    private void update(BufferedReader reader) throws IOException {
        int index = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            if (index < model.getSize()) {
                model.set(index, line);
            } else {
                model.addElement(line);
            }

            index++;
        }

        if (model.size() > index) {
            model.removeRange(index, model.size() - 1);
        }

        lstLines.ensureIndexIsVisible(model.size() - 1);
    }

    private void updateFile() throws IOException {
        // support: 1. local file 2. local command 3. remote file (http)
        try (BufferedReader reader = new BufferedReader(new FileReader("test.log"))) {
            update(reader);
        }
    }

    private void updateRemoteFile() throws IOException {
        if (remoteFile == null) {
            return;
        }

        final String url = remoteFile.getUrl();

        // fixme: network operations should be on a background thread
        DefaultHttpClient httpClient = new DefaultHttpClient();

        Credentials credentials = remoteFile.getCredentials();
        if (credentials != null && !credentials.isEmpty()) {
            httpClient.getCredentialsProvider().setCredentials(new AuthScope(new URL(url).getHost(), PORT_HTTPS),
                    new UsernamePasswordCredentials(credentials.getUsername(), credentials.getPassword()));
        }

        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity responseEntity = httpResponse.getEntity();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()))) {
            update(reader);
        }
    }
}
