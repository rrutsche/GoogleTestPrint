package de.rutsche.mycloudprinter;

import java.io.File;

import de.rutsche.mycloudprinter.filechooser.FileChooser;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MyCloudPrinterActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(v.getContext(), FileChooser.class);
                startActivityForResult(intent,
                        FileChooser.FILE_CHOOSER_REQUEST_CODE);
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FileChooser.FILE_CHOOSER_REQUEST_CODE) {
            if (resultCode == FileChooser.FILE_CHOOSER_RESULT_CODE_SUCCESS) {
                Bundle extras = data.getExtras();
                File file = new File((String) extras.get("SelectedFile"));
                Log.w("Debug", "File from FileChooser: " + file.getName());
                sendDoc(file);
            }
        }
    }
    
    private void sendDoc(File file){
    	Intent printIntent = new Intent(this, PrintDialogActivity.class);
    	Uri docUri = Uri.fromFile(file);
//    	String docMimeType = "pdf";
    	printIntent.setDataAndType(docUri, "application.pdf");
    	String docTitle = file.getName();
    	printIntent.putExtra("title", docTitle);
    	startActivity(printIntent);
    }
}
