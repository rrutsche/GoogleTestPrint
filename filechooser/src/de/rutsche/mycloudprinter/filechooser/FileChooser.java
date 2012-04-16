package de.rutsche.mycloudprinter.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.beuthhochschule.swp.cloudprinter.filechooser.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * This Class is a {@code ListActivity} allowing the user to choose a file from
 * his SD-card. The {@code FileChooser} should be started in the following way:<br>
 * <br>
 * {@code startActivityForResult(intent, FileChooser.FILE_CHOOSER_REQUEST_CODE);}
 * <br>
 * <br>
 * In the calling {@code Activity} override the {@code onActivityResult} method
 * to catch the result of the {@code FileChooser}. Here's an example:
 * 
 * <pre>
 * {@code
 * protected void onActivityResult(int requestCode, int resultCode, Intent data){
 *      super.onActivityResult(requestCode, resultCode, data); 
 *      if (requestCode == FileChooser.FILE_CHOOSER_REQUEST_CODE) {
 *           if (resultCode == FileChooser.FILE_CHOOSER_RESULT_CODE_SUCCESS) { 
 *              doSomething();
 *           }
 *      }
 * }
 * </pre>
 * 
 * @author Frank Schmidt
 * 
 */
public class FileChooser extends ListActivity {

    /**
     * The Request code for using {@code createActivityForResult} method
     */
    public static final int FILE_CHOOSER_REQUEST_CODE = 123321998;

    /**
     * The Result code for success when using {@code createActivityForResult}
     * method
     */
    public static final int FILE_CHOOSER_RESULT_CODE_SUCCESS = 0;

    /**
     * The Result code for failure when using {@code createActivityForResult}
     * method
     */
    public static final int FILE_CHOOSER_RESULT_CODE_FAILURE = -1;

    private static final String NODE_TEXT_KEY = "NodeText";
    private static final String NODE_IMAGE_KEY = "NodeImage";

    private TextView currentDirectoryText;

    private Button selectButton;
    private Button previewButton;

    /**
     * List storing the nodes of the current directory
     */
    private List<IFileNode> nodes;

    /**
     * List storing maps (one for each node) containing data for showing nodes
     * in the ListView
     */
    private ArrayList<HashMap<String, Object>> nodesList;

    private RootNode defaultRootNode;
    private RootNode currentRootNode;

    /**
     * The File currently selected. The path of this file is the one being sent
     * to the calling {@code Activity} if the user clicks on the
     * {@code selectButton}
     */
    private FileNode selectedFile = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_chooser);

        defaultRootNode = new RootNode(new File("/sdcard/"));
        currentRootNode = defaultRootNode;

        currentDirectoryText = (TextView) findViewById(R.id.fcTextViewCurrentDirectory);
        currentDirectoryText.setText(currentRootNode.getFile().getPath());

        // setup select button
        selectButton = (Button) findViewById(R.id.fcButtonSelect);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // finish the original intend by sending the selected files path
                // back to the calling activity
                Intent intent = new Intent();
                setResult(FileChooser.FILE_CHOOSER_RESULT_CODE_SUCCESS, intent);
                intent.putExtra("SelectedFile", selectedFile.getFile()
                        .getAbsolutePath());
                // TODO placeholder for BusyIndicator
                placeholderTimer();
                // finish();
            }
        });

        // the select button is only enabled when a file is selected
        selectButton.setEnabled(false);

        // setup preview button
        previewButton = (Button) findViewById(R.id.fcButtonPreview);

        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(selectedFile.getFile().getAbsolutePath());

                if (file.toString().contains(".pdf")) {

                    Uri uri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        showAllert();
                    }

                }
            }
        });

        // the select button is only enabled when a file is selected
        previewButton.setEnabled(false);

        createNodeList(currentRootNode);

    }

    private void showAllert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Log.w("DEBUG", "SHOW ALERT!!!!!!!!!!");
        builder.setTitle("No Application found");
        builder.setMessage("Download Adobe Reader from Android Market?");
        builder.setPositiveButton("Yes, Please",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri
                                .parse("market://details?id=com.adobe.reader"));
                        try {
                            startActivity(marketIntent);
                        } catch (Exception e) {
                            Log.w("DEBUG", "Exception Catched");
                            WebView webView = new WebView(
                                    getApplicationContext());
                            webView.getSettings().setJavaScriptEnabled(true);
                            String url = "www.github.com";
                            WebViewClient webViewClient = new WebViewClient();
                            webView.setWebViewClient(webViewClient);
                            webView.loadUrl(url);
                        }
                    }
                });
        builder.setNegativeButton("No, Thanks", null);
        builder.create().show();
    }

    /**
     * Creates a {@code List} of {@code IFileNodes} for the children of the
     * given {@code RootNode} then stores those nodes in a {@code List} of
     * {@code Maps}, one for each node, and sets these as input for the
     * ListView.
     * 
     * @param rootNode
     */
    private void createNodeList(RootNode rootNode) {

        nodes = new ArrayList<IFileNode>();

        // if the given rootNode is not the defaultRootNode, the rootNode has to
        // be placed on the list to go back
        if (!rootNode.getFile().getName()
                .equals(defaultRootNode.getFile().getName())) {
            nodes.add(rootNode);
        }

        // directories and files have to be collected in different lists because
        // directories are shown before files in the list view
        ArrayList<DirectoryNode> directories = new ArrayList<DirectoryNode>();
        ArrayList<FileNode> files = new ArrayList<FileNode>();

        File[] fileList = rootNode.getFile().listFiles();

        // fill the lists of directories and files
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    directories.add(new DirectoryNode(file));
                } else if (isSupportedType(file)) {
                    files.add(new FileNode(file));
                }
            }
        }

        nodes.addAll(directories);
        nodes.addAll(files);

        // create the input for the ListView
        nodesList = new ArrayList<HashMap<String, Object>>();

        // create the maps for the ListViews input
        for (IFileNode node : nodes) {
            HashMap<String, Object> nodeMap = new HashMap<String, Object>();
            nodeMap.put(NODE_TEXT_KEY, node.getName());
            nodeMap.put(NODE_IMAGE_KEY, node.getImageId());
            nodesList.add(nodeMap);
        }

        // create an Adapter handling the data for the ListView
        SimpleAdapter fileListAdapter = new SimpleAdapter(this, nodesList,
                R.layout.file_chooser_list_item, new String[] { NODE_TEXT_KEY,
                        NODE_IMAGE_KEY }, new int[] { R.id.fcListItemText,
                        R.id.fcListItemImage });

        // make sure the ListView is notified of data changes by the Adapter so
        // it updates itself
        fileListAdapter.notifyDataSetChanged();
        setListAdapter(fileListAdapter);
    }

    private boolean isSupportedType(File file) {
        // TODO Filter unsupported files
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
     * android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView listView, View view, int position,
            long id) {
        IFileNode selectedNode = nodes.get(position);
        if (selectedNode instanceof DirectoryNode) {
            handleDirectorySelected((DirectoryNode) selectedNode);
        } else if (selectedNode instanceof RootNode) {
            handleRootNodeSelected((RootNode) selectedNode);
        } else if (selectedNode instanceof FileNode) {
            handleFileSelected((FileNode) selectedNode, view);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            returnToCallingActivity();
        }
        return true;
    }

    /**
     * Method called when a {@code FileNode} was selected in the ListView
     * 
     * @param selectedFile
     * @param view
     */
    private void handleFileSelected(FileNode selectedFile, View view) {
        // set the file as selectedFile and enable the selectButton
        this.selectedFile = selectedFile;
        view.setSelected(true);
        selectButton.setEnabled(true);
        if (selectedFile.getFile().toString().endsWith(".pdf")) {
            previewButton.setEnabled(true);
        } else {
            previewButton.setEnabled(false);
        }

    }

    /**
     * Method called when a {@code RootNode} was selected in the ListView
     * 
     * @param selectedRootNode
     */
    private void handleRootNodeSelected(RootNode selectedRootNode) {
        if (!selectedRootNode.getFile().getName()
                .equals(defaultRootNode.getFile().getName())) {
            // set the parent of the currentRootNode as the new currentRootNode
            // (remember, we wanna go back)
            currentRootNode = new RootNode(currentRootNode.getFile()
                    .getParentFile());
            currentDirectoryText.setText(currentRootNode.getFile().getPath());
            // fill the ListView with the new data
            createNodeList(currentRootNode);
        }
    }

    /**
     * Method called when a {@code DirectoryNode} was selected in the ListView
     * 
     * @param selectedDirectory
     */
    private void handleDirectorySelected(DirectoryNode selectedDirectory) {
        selectButton.setEnabled(false);
        // check whether the selected directory is accessible
        if (selectedDirectory.getFile().canRead()) {
            // the selected directory is the new currentRootNode
            currentRootNode = new RootNode(selectedDirectory.getFile());
            currentDirectoryText.setText(currentRootNode.getFile().getPath());
            // fill the ListView with the new data
            createNodeList(currentRootNode);
        } else {
            // TODO Exchange with a localized String
            createNewAlert("Ordner kann nicht gelesen werden");
        }
    }

    /**
     * Called to return to the calling {@code Activity} with a failure result
     * code
     */
    protected void returnToCallingActivity() {
        Intent intent = new Intent();
        setResult(FileChooser.FILE_CHOOSER_RESULT_CODE_FAILURE, intent);
        finish();
    }

    /**
     * Creates a new {@code AlertDialog} with the given {@code String} as text
     * 
     * @param text
     */
    private void createNewAlert(String text) {
        // TODO Exchange image with the real one
        new AlertDialog.Builder(this).setIcon(R.drawable.folder_temp)
                .setTitle(text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                }).show();
    }

    // TODO placeholder for BusyIndicator
    private void placeholderTimer() {
        final int TIMER = 100;
        final int LOGTIME = 3000;

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading File...");
        pd.show();
        Thread logThread = new Thread() {
            @Override
            public void run() {

                try {
                    int waited = 0;
                    while (waited < LOGTIME) {
                        sleep(TIMER);
                        waited += TIMER;
                    }
                    FileChooser.this.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        logThread.start();

    }
}
